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
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.CreateCustomerTypeRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.UpdateCustomerTypeRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.CustomerType
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.CustomerTypeService
import java.time.LocalDateTime

@WebMvcTest(CustomerTypeController::class)
class CustomerTypeControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var customerTypeService: CustomerTypeService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val sampleCustomerType = CustomerType(
        id = 1,
        name = "Individual",
        createdAt = LocalDateTime.now(),
        updatedAt = null
    )

    private val sampleCustomerTypeList = listOf(
        sampleCustomerType,
        CustomerType(
            id = 2,
            name = "Corporate",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
    )

    @Test
    fun `getAllCustomerTypes should return paged customer types with default parameters`() {
        // Given
        val pageable = PageRequest.of(0, 20, org.springframework.data.domain.Sort.by("name"))
        val page = PageImpl(sampleCustomerTypeList, pageable, sampleCustomerTypeList.size.toLong())
        whenever(customerTypeService.findAll(any())).thenReturn(page)

        // When & Then
        mockMvc.perform(get("/api/v1/customer-types"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Individual"))
            .andExpect(jsonPath("$.content[1].id").value(2))
            .andExpect(jsonPath("$.content[1].name").value("Corporate"))

        verify(customerTypeService).findAll(any())
    }

    @Test
    fun `getAllCustomerTypes should return paged customer types with custom parameters`() {
        // Given
        val pageable = PageRequest.of(1, 5, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "name"))
        val page = PageImpl(sampleCustomerTypeList, pageable, sampleCustomerTypeList.size.toLong())
        whenever(customerTypeService.findAll(any())).thenReturn(page)

        // When & Then
        mockMvc.perform(
            get("/api/v1/customer-types")
                .param("page", "1")
                .param("size", "5")
                .param("sortBy", "name")
                .param("sortDir", "desc")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray)

        verify(customerTypeService).findAll(any())
    }

    @Test
    fun `getCustomerTypeById should return customer type when exists`() {
        // Given
        whenever(customerTypeService.findById(1)).thenReturn(sampleCustomerType)

        // When & Then
        mockMvc.perform(get("/api/v1/customer-types/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Individual"))

        verify(customerTypeService).findById(1)
    }

    @Test
    fun `getCustomerTypeById should return 404 when not exists`() {
        // Given
        whenever(customerTypeService.findById(1)).thenThrow(EntityNotFoundException("CustomerType", 1))

        // When & Then
        mockMvc.perform(get("/api/v1/customer-types/1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("CustomerType with identifier '1' not found"))

        verify(customerTypeService).findById(1)
    }

    @Test
    fun `createCustomerType should create and return customer type with valid request`() {
        // Given
        val request = CreateCustomerTypeRequest(id = 1, name = "Individual")
        whenever(customerTypeService.create(any())).thenReturn(sampleCustomerType)

        // When & Then
        mockMvc.perform(
            post("/api/v1/customer-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Individual"))

        verify(customerTypeService).create(any())
    }

    @Test
    fun `createCustomerType should return 400 with invalid request - missing name`() {
        // Given
        val request = CreateCustomerTypeRequest(id = 1, name = "")

        // When & Then
        mockMvc.perform(
            post("/api/v1/customer-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Validation Failed"))

        verify(customerTypeService, never()).create(any())
    }

    @Test
    fun `createCustomerType should return 400 with invalid request - null id`() {
        // Given
        val invalidJson = """{"name": "Individual"}"""

        // When & Then
        mockMvc.perform(
            post("/api/v1/customer-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest)

        verify(customerTypeService, never()).create(any())
    }

    @Test
    fun `updateCustomerType should update and return customer type with valid request`() {
        // Given
        val request = UpdateCustomerTypeRequest(name = "Updated Individual")
        val updatedCustomerType = sampleCustomerType.copy(name = "Updated Individual")
        whenever(customerTypeService.findById(1)).thenReturn(sampleCustomerType)
        whenever(customerTypeService.update(eq(1), any())).thenReturn(updatedCustomerType)

        // When & Then
        mockMvc.perform(
            put("/api/v1/customer-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Updated Individual"))

        verify(customerTypeService).findById(1)
        verify(customerTypeService).update(eq(1), any())
    }

    @Test
    fun `updateCustomerType should return 404 when customer type not exists`() {
        // Given
        val request = UpdateCustomerTypeRequest(name = "Updated Individual")
        whenever(customerTypeService.findById(1)).thenThrow(EntityNotFoundException("CustomerType", 1))

        // When & Then
        mockMvc.perform(
            put("/api/v1/customer-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(customerTypeService).findById(1)
        verify(customerTypeService, never()).update(any(), any())
    }

    @Test
    fun `updateCustomerType should return 400 with invalid request - blank name`() {
        // Given
        val request = UpdateCustomerTypeRequest(name = "")

        // When & Then
        mockMvc.perform(
            put("/api/v1/customer-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Validation Failed"))

        verify(customerTypeService, never()).findById(any())
        verify(customerTypeService, never()).update(any(), any())
    }

    @Test
    fun `deleteCustomerType should delete customer type when exists`() {
        // Given
        doNothing().whenever(customerTypeService).deleteById(1)

        // When & Then
        mockMvc.perform(delete("/api/v1/customer-types/1"))
            .andExpect(status().isNoContent)

        verify(customerTypeService).deleteById(1)
    }

    @Test
    fun `deleteCustomerType should return 404 when not exists`() {
        // Given
        doThrow(EntityNotFoundException("CustomerType", 1)).whenever(customerTypeService).deleteById(1)

        // When & Then
        mockMvc.perform(delete("/api/v1/customer-types/1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(customerTypeService).deleteById(1)
    }

    @Test
    fun `searchCustomerTypes should return matching customer types`() {
        // Given
        val searchResults = listOf(sampleCustomerType)
        whenever(customerTypeService.searchByName("Ind")).thenReturn(searchResults)

        // When & Then
        mockMvc.perform(
            get("/api/v1/customer-types/search")
                .param("name", "Ind")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Individual"))

        verify(customerTypeService).searchByName("Ind")
    }

    @Test
    fun `searchCustomerTypes should return paginated results`() {
        // Given
        val searchResults = (1..25).map { 
            CustomerType(
                id = it,
                name = "Type $it",
                createdAt = LocalDateTime.now(),
                updatedAt = null
            )
        }
        whenever(customerTypeService.searchByName("Type")).thenReturn(searchResults)

        // When & Then
        mockMvc.perform(
            get("/api/v1/customer-types/search")
                .param("name", "Type")
                .param("page", "1")
                .param("size", "10")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(10))

        verify(customerTypeService).searchByName("Type")
    }

    @Test
    fun `getCustomerTypeSummaries should return all customer type summaries`() {
        // Given
        whenever(customerTypeService.findAll()).thenReturn(sampleCustomerTypeList)

        // When & Then
        mockMvc.perform(get("/api/v1/customer-types/summary"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Individual"))

        verify(customerTypeService).findAll()
    }

    @Test
    fun `customerTypeExists should return true when exists`() {
        // Given
        whenever(customerTypeService.existsById(1)).thenReturn(true)

        // When & Then
        mockMvc.perform(get("/api/v1/customer-types/1/exists"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(true))

        verify(customerTypeService).existsById(1)
    }

    @Test
    fun `customerTypeExists should return false when not exists`() {
        // Given
        whenever(customerTypeService.existsById(1)).thenReturn(false)

        // When & Then
        mockMvc.perform(get("/api/v1/customer-types/1/exists"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(false))

        verify(customerTypeService).existsById(1)
    }

    @Test
    fun `getCustomerTypeCount should return total count`() {
        // Given
        whenever(customerTypeService.count()).thenReturn(10L)

        // When & Then
        mockMvc.perform(get("/api/v1/customer-types/count"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.count").value(10))

        verify(customerTypeService).count()
    }
}