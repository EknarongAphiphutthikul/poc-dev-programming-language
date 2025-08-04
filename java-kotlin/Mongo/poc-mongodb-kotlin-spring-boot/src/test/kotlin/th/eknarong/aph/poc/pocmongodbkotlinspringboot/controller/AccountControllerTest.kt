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
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.CreateAccountRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.UpdateAccountRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.Account
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.AccountService
import java.time.LocalDateTime

@WebMvcTest(AccountController::class)
class AccountControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val sampleAccount = Account(
        id = "account-1",
        customerId = "customer-1",
        productId = "product-1",
        productCode = "SAV001",
        productCategory = "Savings",
        accountNumber = "ACC123456",
        parentAccountId = null,
        accountRefKey = "REF001",
        accountCategoryId = 1,
        statusId = 1,
        interestType = "Simple",
        openedDate = LocalDateTime.now(),
        closureDate = null,
        attributes = mapOf("branch" to "Main"),
        createdAt = LocalDateTime.now(),
        createdBy = "admin",
        updatedAt = null,
        updatedBy = null
    )

    private val sampleAccountList = listOf(
        sampleAccount,
        Account(
            id = "account-2",
            customerId = "customer-2",
            productId = "product-2",
            productCode = "CUR001",
            productCategory = "Current",
            accountNumber = "ACC123457",
            parentAccountId = null,
            accountRefKey = "REF002",
            accountCategoryId = 2,
            statusId = 1,
            interestType = "Compound",
            openedDate = LocalDateTime.now(),
            closureDate = null,
            attributes = mapOf("branch" to "Branch"),
            createdAt = LocalDateTime.now(),
            createdBy = "admin",
            updatedAt = null,
            updatedBy = null
        )
    )

    @Test
    fun `getAllAccounts should return paged accounts`() {
        // Given
        val pageable = PageRequest.of(0, 20, org.springframework.data.domain.Sort.by("accountNumber"))
        val page = PageImpl(sampleAccountList, pageable, sampleAccountList.size.toLong())
        whenever(accountService.findAll(any())).thenReturn(page)

        // When & Then
        mockMvc.perform(get("/api/v1/accounts"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].id").value("account-1"))
            .andExpect(jsonPath("$.content[0].accountNumber").value("ACC123456"))

        verify(accountService).findAll(any())
    }

    @Test
    fun `getAccountById should return account when exists`() {
        // Given
        whenever(accountService.findById("account-1")).thenReturn(sampleAccount)

        // When & Then
        mockMvc.perform(get("/api/v1/accounts/account-1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("account-1"))
            .andExpect(jsonPath("$.accountNumber").value("ACC123456"))
            .andExpect(jsonPath("$.customerId").value("customer-1"))

        verify(accountService).findById("account-1")
    }

    @Test
    fun `getAccountById should return 404 when not exists`() {
        // Given
        whenever(accountService.findById("account-1")).thenThrow(EntityNotFoundException("Account", "account-1"))

        // When & Then
        mockMvc.perform(get("/api/v1/accounts/account-1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(accountService).findById("account-1")
    }

    @Test
    fun `createAccount should create and return account`() {
        // Given
        val request = CreateAccountRequest(
            customerId = "customer-1",
            productId = "product-1",
            productCode = "SAV001",
            productCategory = "Savings",
            accountNumber = "ACC123456",
            parentAccountId = null,
            accountRefKey = "REF001",
            accountCategoryId = 1,
            statusId = 1,
            interestType = "Simple",
            openedDate = LocalDateTime.now(),
            closureDate = null,
            attributes = mapOf("branch" to "Main"),
            createdBy = "admin"
        )
        whenever(accountService.create(any())).thenReturn(sampleAccount)

        // When & Then
        mockMvc.perform(
            post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accountNumber").value("ACC123456"))
            .andExpect(jsonPath("$.customerId").value("customer-1"))

        verify(accountService).create(any())
    }

    @Test
    fun `updateAccount should update and return account`() {
        // Given
        val request = UpdateAccountRequest(
            customerId = "customer-1",
            productId = "product-1",
            productCode = "SAV001-UPDATED",
            productCategory = "Savings",
            accountNumber = "ACC123456-UPDATED",
            parentAccountId = null,
            accountRefKey = "REF001",
            accountCategoryId = 1,
            statusId = 1,
            interestType = "Simple",
            openedDate = LocalDateTime.now(),
            closureDate = null,
            attributes = mapOf("branch" to "Main"),
            updatedBy = "updater"
        )
        val updatedAccount = sampleAccount.copy(accountNumber = "ACC123456-UPDATED")
        whenever(accountService.findById("account-1")).thenReturn(sampleAccount)
        whenever(accountService.update(eq("account-1"), any(), eq("updater"))).thenReturn(updatedAccount)

        // When & Then
        mockMvc.perform(
            put("/api/v1/accounts/account-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("account-1"))
            .andExpect(jsonPath("$.accountNumber").value("ACC123456-UPDATED"))

        verify(accountService).findById("account-1")
        verify(accountService).update(eq("account-1"), any(), eq("updater"))
    }

    @Test
    fun `deleteAccount should delete account when exists`() {
        // Given
        doNothing().whenever(accountService).deleteById("account-1")

        // When & Then
        mockMvc.perform(delete("/api/v1/accounts/account-1"))
            .andExpect(status().isNoContent)

        verify(accountService).deleteById("account-1")
    }

    @Test
    fun `deleteAccount should return 404 when not exists`() {
        // Given
        doThrow(EntityNotFoundException("Account", "account-1")).whenever(accountService).deleteById("account-1")

        // When & Then
        mockMvc.perform(delete("/api/v1/accounts/account-1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(accountService).deleteById("account-1")
    }

    @Test
    fun `getAccountsByCustomerId should return accounts for customer`() {
        // Given
        val customerId = "customer-1"
        val customerAccounts = listOf(sampleAccount)
        whenever(accountService.findByCustomerId(customerId)).thenReturn(customerAccounts)

        // When & Then
        mockMvc.perform(get("/api/v1/accounts/customer/customer-1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value("account-1"))

        verify(accountService).findByCustomerId(customerId)
    }

    @Test
    fun `accountExists should return true when exists`() {
        // Given
        whenever(accountService.existsById("account-1")).thenReturn(true)

        // When & Then
        mockMvc.perform(get("/api/v1/accounts/account-1/exists"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(true))

        verify(accountService).existsById("account-1")
    }

    @Test
    fun `getAccountCount should return total count`() {
        // Given
        whenever(accountService.count()).thenReturn(100L)

        // When & Then
        mockMvc.perform(get("/api/v1/accounts/count"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.count").value(100))

        verify(accountService).count()
    }
}