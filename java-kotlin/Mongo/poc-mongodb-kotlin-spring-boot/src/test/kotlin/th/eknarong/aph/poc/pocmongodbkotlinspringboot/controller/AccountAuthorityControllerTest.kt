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
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.CreateAccountAuthorityRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.UpdateAccountAuthorityRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountAuthority
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.AccountAuthorityService
import java.time.LocalDateTime

@WebMvcTest(AccountAuthorityController::class)
class AccountAuthorityControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var accountAuthorityService: AccountAuthorityService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val sampleAccountAuthority = AccountAuthority(
        id = "authority-1",
        customerId = "customer-1",
        accountNumber = "ACC123456",
        position = "Primary",
        effectiveDateFrom = LocalDateTime.now(),
        effectiveDateTo = null,
        createdAt = LocalDateTime.now(),
        createdBy = "admin",
        updatedAt = null,
        updatedBy = null
    )

    private val sampleAccountAuthorityList = listOf(
        sampleAccountAuthority,
        AccountAuthority(
            id = "authority-2",
            customerId = "customer-2",
            accountNumber = "ACC123457",
            position = "Secondary",
            effectiveDateFrom = LocalDateTime.now(),
            effectiveDateTo = null,
            createdAt = LocalDateTime.now(),
            createdBy = "admin",
            updatedAt = null,
            updatedBy = null
        )
    )

    @Test
    fun `getAllAccountAuthorities should return paged authorities`() {
        // Given
        val pageable = PageRequest.of(0, 20, org.springframework.data.domain.Sort.by("createdAt"))
        val page = PageImpl(sampleAccountAuthorityList, pageable, sampleAccountAuthorityList.size.toLong())
        whenever(accountAuthorityService.findAll(any())).thenReturn(page)

        // When & Then
        mockMvc.perform(get("/api/v1/account-authorities"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].id").value("authority-1"))
            .andExpect(jsonPath("$.content[0].customerId").value("customer-1"))

        verify(accountAuthorityService).findAll(any())
    }

    @Test
    fun `getAccountAuthorityById should return authority when exists`() {
        // Given
        whenever(accountAuthorityService.findById("authority-1")).thenReturn(sampleAccountAuthority)

        // When & Then
        mockMvc.perform(get("/api/v1/account-authorities/authority-1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("authority-1"))
            .andExpect(jsonPath("$.customerId").value("customer-1"))
            .andExpect(jsonPath("$.accountNumber").value("ACC123456"))

        verify(accountAuthorityService).findById("authority-1")
    }

    @Test
    fun `getAccountAuthorityById should return 404 when not exists`() {
        // Given
        whenever(accountAuthorityService.findById("authority-1")).thenThrow(EntityNotFoundException("AccountAuthority", "authority-1"))

        // When & Then
        mockMvc.perform(get("/api/v1/account-authorities/authority-1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(accountAuthorityService).findById("authority-1")
    }

    @Test
    fun `createAccountAuthority should create and return authority`() {
        // Given
        val request = CreateAccountAuthorityRequest(
            customerId = "customer-1",
            accountNumber = "ACC123456",
            position = "Primary",
            effectiveDateFrom = LocalDateTime.now(),
            effectiveDateTo = null,
            createdBy = "admin"
        )
        whenever(accountAuthorityService.create(any())).thenReturn(sampleAccountAuthority)

        // When & Then
        mockMvc.perform(
            post("/api/v1/account-authorities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.customerId").value("customer-1"))
            .andExpect(jsonPath("$.accountNumber").value("ACC123456"))

        verify(accountAuthorityService).create(any())
    }

    @Test
    fun `updateAccountAuthority should update and return authority`() {
        // Given
        val request = UpdateAccountAuthorityRequest(
            customerId = "customer-1",
            accountNumber = "ACC123456",
            position = "Updated Primary",
            effectiveDateFrom = LocalDateTime.now(),
            effectiveDateTo = null,
            updatedBy = "updater"
        )
        val updatedAuthority = sampleAccountAuthority.copy(position = "Updated Primary")
        whenever(accountAuthorityService.findById("authority-1")).thenReturn(sampleAccountAuthority)
        whenever(accountAuthorityService.update(eq("authority-1"), any(), eq("updater"))).thenReturn(updatedAuthority)

        // When & Then
        mockMvc.perform(
            put("/api/v1/account-authorities/authority-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("authority-1"))
            .andExpect(jsonPath("$.position").value("Updated Primary"))

        verify(accountAuthorityService).findById("authority-1")
        verify(accountAuthorityService).update(eq("authority-1"), any(), eq("updater"))
    }

    @Test
    fun `deleteAccountAuthority should delete authority when exists`() {
        // Given
        doNothing().whenever(accountAuthorityService).deleteById("authority-1")

        // When & Then
        mockMvc.perform(delete("/api/v1/account-authorities/authority-1"))
            .andExpect(status().isNoContent)

        verify(accountAuthorityService).deleteById("authority-1")
    }

    @Test
    fun `deleteAccountAuthority should return 404 when not exists`() {
        // Given
        doThrow(EntityNotFoundException("AccountAuthority", "authority-1")).whenever(accountAuthorityService).deleteById("authority-1")

        // When & Then
        mockMvc.perform(delete("/api/v1/account-authorities/authority-1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(accountAuthorityService).deleteById("authority-1")
    }

    @Test
    fun `getAccountAuthoritiesByCustomerId should return authorities for customer`() {
        // Given
        val customerId = "customer-1"
        val customerAuthorities = listOf(sampleAccountAuthority)
        whenever(accountAuthorityService.findByCustomerId(customerId)).thenReturn(customerAuthorities)

        // When & Then
        mockMvc.perform(get("/api/v1/account-authorities/customer/customer-1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value("authority-1"))

        verify(accountAuthorityService).findByCustomerId(customerId)
    }

    @Test
    fun `getAccountAuthoritiesByAccountNumber should return authorities for account`() {
        // Given
        val accountNumber = "ACC123456"
        val accountAuthorities = listOf(sampleAccountAuthority)
        whenever(accountAuthorityService.findByAccountNumber(accountNumber)).thenReturn(accountAuthorities)

        // When & Then
        mockMvc.perform(get("/api/v1/account-authorities/account/ACC123456"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value("authority-1"))

        verify(accountAuthorityService).findByAccountNumber(accountNumber)
    }

    @Test
    fun `getAccountAuthorityCount should return total count`() {
        // Given
        whenever(accountAuthorityService.count()).thenReturn(25L)

        // When & Then
        mockMvc.perform(get("/api/v1/account-authorities/count"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.count").value(25))

        verify(accountAuthorityService).count()
    }
}