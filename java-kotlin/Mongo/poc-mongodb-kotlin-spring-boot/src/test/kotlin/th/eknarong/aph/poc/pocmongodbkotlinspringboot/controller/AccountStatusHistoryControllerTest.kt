package th.eknarong.aph.poc.pocmongodbkotlinspringboot.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.CreateAccountStatusHistoryRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.UpdateAccountStatusHistoryRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountStatusHistory
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.AccountStatusHistoryService
import java.time.LocalDateTime

@WebMvcTest(AccountStatusHistoryController::class)
class AccountStatusHistoryControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var accountStatusHistoryService: AccountStatusHistoryService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val sampleAccountStatusHistory = AccountStatusHistory(
        id = "history-1",
        accountId = "account-1",
        statusId = 1,
        createdAt = LocalDateTime.now(),
        createdBy = "admin",
        updatedAt = null,
        updatedBy = null
    )

    private val sampleAccountStatusHistoryList = listOf(
        sampleAccountStatusHistory,
        AccountStatusHistory(
            id = "history-2",
            accountId = "account-2",
            statusId = 2,
            createdAt = LocalDateTime.now(),
            createdBy = "admin",
            updatedAt = null,
            updatedBy = null
        )
    )

    @Test
    fun `getAllAccountStatusHistories should return paged histories`() {
        // Given
        val pageable = PageRequest.of(0, 20, org.springframework.data.domain.Sort.by("createdAt"))
        val page = PageImpl(sampleAccountStatusHistoryList, pageable, sampleAccountStatusHistoryList.size.toLong())
        whenever(accountStatusHistoryService.findAll(any())).thenReturn(page)

        // When & Then
        mockMvc.perform(get("/api/v1/account-status-histories"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].id").value("history-1"))
            .andExpect(jsonPath("$.content[0].accountId").value("account-1"))

        verify(accountStatusHistoryService).findAll(any())
    }

    @Test
    fun `getAccountStatusHistoryById should return history when exists`() {
        // Given
        whenever(accountStatusHistoryService.findById("history-1")).thenReturn(sampleAccountStatusHistory)

        // When & Then
        mockMvc.perform(get("/api/v1/account-status-histories/history-1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("history-1"))
            .andExpect(jsonPath("$.accountId").value("account-1"))
            .andExpect(jsonPath("$.statusId").value(1))

        verify(accountStatusHistoryService).findById("history-1")
    }

    @Test
    fun `getAccountStatusHistoryById should return 404 when not exists`() {
        // Given
        whenever(accountStatusHistoryService.findById("history-1")).thenThrow(EntityNotFoundException("AccountStatusHistory", "history-1"))

        // When & Then
        mockMvc.perform(get("/api/v1/account-status-histories/history-1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(accountStatusHistoryService).findById("history-1")
    }

    @Test
    fun `createAccountStatusHistory should create and return history`() {
        // Given
        val request = CreateAccountStatusHistoryRequest(
            accountId = "account-1",
            statusId = 1,
            createdBy = "admin"
        )
        whenever(accountStatusHistoryService.create(any())).thenReturn(sampleAccountStatusHistory)

        // When & Then
        mockMvc.perform(
            post("/api/v1/account-status-histories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accountId").value("account-1"))
            .andExpect(jsonPath("$.statusId").value(1))

        verify(accountStatusHistoryService).create(any())
    }

    @Test
    fun `updateAccountStatusHistory should update and return history`() {
        // Given
        val request = UpdateAccountStatusHistoryRequest(
            accountId = "account-1",
            statusId = 2,
            updatedBy = "updater"
        )
        val updatedHistory = sampleAccountStatusHistory.copy(statusId = 2)
        whenever(accountStatusHistoryService.findById("history-1")).thenReturn(sampleAccountStatusHistory)
        whenever(accountStatusHistoryService.update(eq("history-1"), any(), eq("updater"))).thenReturn(updatedHistory)

        // When & Then
        mockMvc.perform(
            put("/api/v1/account-status-histories/history-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("history-1"))
            .andExpect(jsonPath("$.statusId").value(2))

        verify(accountStatusHistoryService).findById("history-1")
        verify(accountStatusHistoryService).update(eq("history-1"), any(), eq("updater"))
    }

    @Test
    fun `deleteAccountStatusHistory should delete history when exists`() {
        // Given
        doNothing().whenever(accountStatusHistoryService).deleteById("history-1")

        // When & Then
        mockMvc.perform(delete("/api/v1/account-status-histories/history-1"))
            .andExpect(status().isNoContent)

        verify(accountStatusHistoryService).deleteById("history-1")
    }

    @Test
    fun `deleteAccountStatusHistory should return 404 when not exists`() {
        // Given
        doThrow(EntityNotFoundException("AccountStatusHistory", "history-1")).whenever(accountStatusHistoryService).deleteById("history-1")

        // When & Then
        mockMvc.perform(delete("/api/v1/account-status-histories/history-1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(accountStatusHistoryService).deleteById("history-1")
    }

    @Test
    fun `getAccountStatusHistoriesByAccountId should return histories for account`() {
        // Given
        val accountId = "account-1"
        val accountHistories = listOf(sampleAccountStatusHistory)
        whenever(accountStatusHistoryService.findByAccountId(accountId)).thenReturn(accountHistories)

        // When & Then
        mockMvc.perform(get("/api/v1/account-status-histories/account/account-1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value("history-1"))

        verify(accountStatusHistoryService).findByAccountId(accountId)
    }

    @Test
    fun `getAccountStatusHistoryCount should return total count`() {
        // Given
        whenever(accountStatusHistoryService.count()).thenReturn(50L)

        // When & Then
        mockMvc.perform(get("/api/v1/account-status-histories/count"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.count").value(50))

        verify(accountStatusHistoryService).count()
    }
}