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
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.CreateAccountCategoryRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.UpdateAccountCategoryRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountCategory
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.AccountCategoryService
import java.time.LocalDateTime

@WebMvcTest(AccountCategoryController::class)
class AccountCategoryControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var accountCategoryService: AccountCategoryService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val sampleAccountCategory = AccountCategory(
        id = 1,
        name = "Savings",
        createdAt = LocalDateTime.now(),
        updatedAt = null
    )

    private val sampleAccountCategoryList = listOf(
        sampleAccountCategory,
        AccountCategory(
            id = 2,
            name = "Current",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
    )

    @Test
    fun `getAllAccountCategories should return paged account categories`() {
        // Given
        val pageable = PageRequest.of(0, 20, org.springframework.data.domain.Sort.by("name"))
        val page = PageImpl(sampleAccountCategoryList, pageable, sampleAccountCategoryList.size.toLong())
        whenever(accountCategoryService.findAll(any())).thenReturn(page)

        // When & Then
        mockMvc.perform(get("/api/v1/account-categories"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Savings"))

        verify(accountCategoryService).findAll(any())
    }

    @Test
    fun `getAccountCategoryById should return account category when exists`() {
        // Given
        whenever(accountCategoryService.findById(1)).thenReturn(sampleAccountCategory)

        // When & Then
        mockMvc.perform(get("/api/v1/account-categories/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Savings"))

        verify(accountCategoryService).findById(1)
    }

    @Test
    fun `getAccountCategoryById should return 404 when not exists`() {
        // Given
        whenever(accountCategoryService.findById(1)).thenThrow(EntityNotFoundException("AccountCategory", 1))

        // When & Then
        mockMvc.perform(get("/api/v1/account-categories/1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(accountCategoryService).findById(1)
    }

    @Test
    fun `createAccountCategory should create and return account category`() {
        // Given
        val request = CreateAccountCategoryRequest(id = 1, name = "Savings")
        whenever(accountCategoryService.create(any())).thenReturn(sampleAccountCategory)

        // When & Then
        mockMvc.perform(
            post("/api/v1/account-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Savings"))

        verify(accountCategoryService).create(any())
    }

    @Test
    fun `createAccountCategory should return 400 with invalid request`() {
        // Given
        val request = CreateAccountCategoryRequest(id = 1, name = "")

        // When & Then
        mockMvc.perform(
            post("/api/v1/account-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Validation Failed"))

        verify(accountCategoryService, never()).create(any())
    }

    @Test
    fun `updateAccountCategory should update and return account category`() {
        // Given
        val request = UpdateAccountCategoryRequest(name = "Updated Savings")
        val updatedAccountCategory = sampleAccountCategory.copy(name = "Updated Savings")
        whenever(accountCategoryService.findById(1)).thenReturn(sampleAccountCategory)
        whenever(accountCategoryService.update(eq(1), any())).thenReturn(updatedAccountCategory)

        // When & Then
        mockMvc.perform(
            put("/api/v1/account-categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Updated Savings"))

        verify(accountCategoryService).findById(1)
        verify(accountCategoryService).update(eq(1), any())
    }

    @Test
    fun `deleteAccountCategory should delete account category when exists`() {
        // Given
        doNothing().whenever(accountCategoryService).deleteById(1)

        // When & Then
        mockMvc.perform(delete("/api/v1/account-categories/1"))
            .andExpect(status().isNoContent)

        verify(accountCategoryService).deleteById(1)
    }

    @Test
    fun `deleteAccountCategory should return 404 when not exists`() {
        // Given
        doThrow(EntityNotFoundException("AccountCategory", 1)).whenever(accountCategoryService).deleteById(1)

        // When & Then
        mockMvc.perform(delete("/api/v1/account-categories/1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(accountCategoryService).deleteById(1)
    }

    @Test
    fun `searchAccountCategories should return matching account categories`() {
        // Given
        val searchResults = listOf(sampleAccountCategory)
        whenever(accountCategoryService.searchByName("Sav")).thenReturn(searchResults)

        // When & Then
        mockMvc.perform(
            get("/api/v1/account-categories/search")
                .param("name", "Sav")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Savings"))

        verify(accountCategoryService).searchByName("Sav")
    }

    @Test
    fun `getAccountCategorySummaries should return all summaries`() {
        // Given
        whenever(accountCategoryService.findAll()).thenReturn(sampleAccountCategoryList)

        // When & Then
        mockMvc.perform(get("/api/v1/account-categories/summary"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))

        verify(accountCategoryService).findAll()
    }

    @Test
    fun `accountCategoryExists should return true when exists`() {
        // Given
        whenever(accountCategoryService.existsById(1)).thenReturn(true)

        // When & Then
        mockMvc.perform(get("/api/v1/account-categories/1/exists"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(true))

        verify(accountCategoryService).existsById(1)
    }

    @Test
    fun `getAccountCategoryCount should return total count`() {
        // Given
        whenever(accountCategoryService.count()).thenReturn(5L)

        // When & Then
        mockMvc.perform(get("/api/v1/account-categories/count"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.count").value(5))

        verify(accountCategoryService).count()
    }
}