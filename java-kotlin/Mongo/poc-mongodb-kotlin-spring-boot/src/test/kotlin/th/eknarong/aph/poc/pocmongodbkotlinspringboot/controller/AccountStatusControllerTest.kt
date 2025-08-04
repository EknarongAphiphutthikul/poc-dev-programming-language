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
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.CreateAccountStatusRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.UpdateAccountStatusRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountStatus
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.AccountStatusService
import java.time.LocalDateTime

@WebMvcTest(AccountStatusController::class)
class AccountStatusControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var accountStatusService: AccountStatusService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val sampleAccountStatus = AccountStatus(
        id = 1,
        name = "Active",
        createdAt = LocalDateTime.now(),
        updatedAt = null
    )

    private val sampleAccountStatusList = listOf(
        sampleAccountStatus,
        AccountStatus(
            id = 2,
            name = "Inactive",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
    )

    @Test
    fun `getAllAccountStatuses should return paged account statuses`() {
        // Given
        val pageable = PageRequest.of(0, 20, org.springframework.data.domain.Sort.by("name"))
        val page = PageImpl(sampleAccountStatusList, pageable, sampleAccountStatusList.size.toLong())
        whenever(accountStatusService.findAll(any())).thenReturn(page)

        // When & Then
        mockMvc.perform(get("/api/v1/account-statuses"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Active"))

        verify(accountStatusService).findAll(any())
    }

    @Test
    fun `getAccountStatusById should return account status when exists`() {
        // Given
        whenever(accountStatusService.findById(1)).thenReturn(sampleAccountStatus)

        // When & Then
        mockMvc.perform(get("/api/v1/account-statuses/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Active"))

        verify(accountStatusService).findById(1)
    }

    @Test
    fun `getAccountStatusById should return 404 when not exists`() {
        // Given
        whenever(accountStatusService.findById(1)).thenThrow(EntityNotFoundException("AccountStatus", 1))

        // When & Then
        mockMvc.perform(get("/api/v1/account-statuses/1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(accountStatusService).findById(1)
    }

    @Test
    fun `createAccountStatus should create and return account status`() {
        // Given
        val request = CreateAccountStatusRequest(id = 1, name = "Active")
        whenever(accountStatusService.create(any())).thenReturn(sampleAccountStatus)

        // When & Then
        mockMvc.perform(
            post("/api/v1/account-statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Active"))

        verify(accountStatusService).create(any())
    }

    @Test
    fun `createAccountStatus should return 400 with invalid request`() {
        // Given
        val request = CreateAccountStatusRequest(id = 1, name = "")

        // When & Then
        mockMvc.perform(
            post("/api/v1/account-statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Validation Failed"))

        verify(accountStatusService, never()).create(any())
    }

    @Test
    fun `updateAccountStatus should update and return account status`() {
        // Given
        val request = UpdateAccountStatusRequest(name = "Updated Active")
        val updatedAccountStatus = sampleAccountStatus.copy(name = "Updated Active")
        whenever(accountStatusService.findById(1)).thenReturn(sampleAccountStatus)
        whenever(accountStatusService.update(eq(1), any())).thenReturn(updatedAccountStatus)

        // When & Then
        mockMvc.perform(
            put("/api/v1/account-statuses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Updated Active"))

        verify(accountStatusService).findById(1)
        verify(accountStatusService).update(eq(1), any())
    }

    @Test
    fun `deleteAccountStatus should delete account status when exists`() {
        // Given
        doNothing().whenever(accountStatusService).deleteById(1)

        // When & Then
        mockMvc.perform(delete("/api/v1/account-statuses/1"))
            .andExpect(status().isNoContent)

        verify(accountStatusService).deleteById(1)
    }

    @Test
    fun `deleteAccountStatus should return 404 when not exists`() {
        // Given
        doThrow(EntityNotFoundException("AccountStatus", 1)).whenever(accountStatusService).deleteById(1)

        // When & Then
        mockMvc.perform(delete("/api/v1/account-statuses/1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(accountStatusService).deleteById(1)
    }

    @Test
    fun `searchAccountStatuses should return matching account statuses`() {
        // Given
        val searchResults = listOf(sampleAccountStatus)
        whenever(accountStatusService.searchByName("Act")).thenReturn(searchResults)

        // When & Then
        mockMvc.perform(
            get("/api/v1/account-statuses/search")
                .param("name", "Act")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Active"))

        verify(accountStatusService).searchByName("Act")
    }

    @Test
    fun `getAccountStatusSummaries should return all summaries`() {
        // Given
        whenever(accountStatusService.findAll()).thenReturn(sampleAccountStatusList)

        // When & Then
        mockMvc.perform(get("/api/v1/account-statuses/summary"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))

        verify(accountStatusService).findAll()
    }

    @Test
    fun `accountStatusExists should return true when exists`() {
        // Given
        whenever(accountStatusService.existsById(1)).thenReturn(true)

        // When & Then
        mockMvc.perform(get("/api/v1/account-statuses/1/exists"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(true))

        verify(accountStatusService).existsById(1)
    }

    @Test
    fun `getAccountStatusCount should return total count`() {
        // Given
        whenever(accountStatusService.count()).thenReturn(5L)

        // When & Then
        mockMvc.perform(get("/api/v1/account-statuses/count"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.count").value(5))

        verify(accountStatusService).count()
    }
}