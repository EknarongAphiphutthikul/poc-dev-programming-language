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
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.CreateCustomerRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.UpdateCustomerRequest
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.Customer
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.CustomerService
import java.time.LocalDateTime

@WebMvcTest(CustomerController::class)
class CustomerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var customerService: CustomerService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val sampleCustomer = Customer(
        id = "customer-1",
        cifId = "CIF001",
        refKey = "REF001",
        customerTypeId = 1,
        createdAt = LocalDateTime.now(),
        createdBy = "admin",
        updatedAt = null,
        updatedBy = null
    )

    private val sampleCustomerList = listOf(
        sampleCustomer,
        Customer(
            id = "customer-2",
            cifId = "CIF002",
            refKey = "REF002",
            customerTypeId = 2,
            createdAt = LocalDateTime.now(),
            createdBy = "admin",
            updatedAt = null,
            updatedBy = null
        )
    )

    @Test
    fun `getAllCustomers should return paged customers with default parameters`() {
        // Given
        val pageable = PageRequest.of(0, 20, org.springframework.data.domain.Sort.by("cifId"))
        val page = PageImpl(sampleCustomerList, pageable, sampleCustomerList.size.toLong())
        whenever(customerService.findAll(any())).thenReturn(page)

        // When & Then
        mockMvc.perform(get("/api/v1/customers"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].id").value("customer-1"))
            .andExpect(jsonPath("$.content[0].cifId").value("CIF001"))
            .andExpect(jsonPath("$.content[1].id").value("customer-2"))
            .andExpect(jsonPath("$.content[1].cifId").value("CIF002"))

        verify(customerService).findAll(any())
    }

    @Test
    fun `getAllCustomers should return paged customers with custom parameters`() {
        // Given
        val pageable = PageRequest.of(1, 5, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "cifId"))
        val page = PageImpl(sampleCustomerList, pageable, sampleCustomerList.size.toLong())
        whenever(customerService.findAll(any())).thenReturn(page)

        // When & Then
        mockMvc.perform(
            get("/api/v1/customers")
                .param("page", "1")
                .param("size", "5")
                .param("sortBy", "cifId")
                .param("sortDir", "desc")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray)

        verify(customerService).findAll(any())
    }

    @Test
    fun `getCustomerById should return customer when exists`() {
        // Given
        whenever(customerService.findById("customer-1")).thenReturn(sampleCustomer)

        // When & Then
        mockMvc.perform(get("/api/v1/customers/customer-1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("customer-1"))
            .andExpect(jsonPath("$.cifId").value("CIF001"))
            .andExpect(jsonPath("$.refKey").value("REF001"))
            .andExpect(jsonPath("$.customerTypeId").value(1))

        verify(customerService).findById("customer-1")
    }

    @Test
    fun `getCustomerById should return 404 when not exists`() {
        // Given
        whenever(customerService.findById("customer-1")).thenThrow(EntityNotFoundException("Customer", "customer-1"))

        // When & Then
        mockMvc.perform(get("/api/v1/customers/customer-1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Customer with identifier 'customer-1' not found"))

        verify(customerService).findById("customer-1")
    }

    @Test
    fun `createCustomer should create and return customer with valid request`() {
        // Given
        val request = CreateCustomerRequest(
            cifId = "CIF001",
            refKey = "REF001",
            customerTypeId = 1,
            createdBy = "admin"
        )
        whenever(customerService.create(any())).thenReturn(sampleCustomer)

        // When & Then
        mockMvc.perform(
            post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.cifId").value("CIF001"))
            .andExpect(jsonPath("$.refKey").value("REF001"))
            .andExpect(jsonPath("$.customerTypeId").value(1))

        verify(customerService).create(any())
    }

    @Test
    fun `createCustomer should return 400 with invalid request - missing cifId`() {
        // Given
        val request = CreateCustomerRequest(
            cifId = "",
            refKey = "REF001",
            customerTypeId = 1,
            createdBy = "admin"
        )

        // When & Then
        mockMvc.perform(
            post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Validation Failed"))

        verify(customerService, never()).create(any())
    }

    @Test
    fun `createCustomer should return 400 with invalid request - null customerTypeId`() {
        // Given
        val invalidJson = """{"cifId": "CIF001", "refKey": "REF001", "createdBy": "admin"}"""

        // When & Then
        mockMvc.perform(
            post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest)

        verify(customerService, never()).create(any())
    }

    @Test
    fun `createCustomer should return 400 with invalid request - missing createdBy`() {
        // Given
        val request = CreateCustomerRequest(
            cifId = "CIF001",
            refKey = "REF001",
            customerTypeId = 1,
            createdBy = ""
        )

        // When & Then
        mockMvc.perform(
            post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Validation Failed"))

        verify(customerService, never()).create(any())
    }

    @Test
    fun `updateCustomer should update and return customer with valid request`() {
        // Given
        val request = UpdateCustomerRequest(
            cifId = "CIF001-UPDATED",
            refKey = "REF001-UPDATED",
            customerTypeId = 2,
            updatedBy = "updater"
        )
        val updatedCustomer = sampleCustomer.copy(
            cifId = "CIF001-UPDATED",
            refKey = "REF001-UPDATED",
            customerTypeId = 2
        )
        whenever(customerService.findById("customer-1")).thenReturn(sampleCustomer)
        whenever(customerService.update(eq("customer-1"), any())).thenReturn(updatedCustomer)

        // When & Then
        mockMvc.perform(
            put("/api/v1/customers/customer-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("customer-1"))
            .andExpect(jsonPath("$.cifId").value("CIF001-UPDATED"))
            .andExpect(jsonPath("$.refKey").value("REF001-UPDATED"))
            .andExpect(jsonPath("$.customerTypeId").value(2))

        verify(customerService).findById("customer-1")
        verify(customerService).update(eq("customer-1"), any())
    }

    @Test
    fun `updateCustomer should return 404 when customer not exists`() {
        // Given
        val request = UpdateCustomerRequest(
            cifId = "CIF001-UPDATED",
            refKey = "REF001-UPDATED",
            customerTypeId = 2,
            updatedBy = "updater"
        )
        whenever(customerService.findById("customer-1")).thenThrow(EntityNotFoundException("Customer", "customer-1"))

        // When & Then
        mockMvc.perform(
            put("/api/v1/customers/customer-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(customerService).findById("customer-1")
        verify(customerService, never()).update(any(), any())
    }

    @Test
    fun `updateCustomer should return 400 with invalid request - blank cifId`() {
        // Given
        val request = UpdateCustomerRequest(
            cifId = "",
            refKey = "REF001",
            customerTypeId = 1,
            updatedBy = "updater"
        )

        // When & Then
        mockMvc.perform(
            put("/api/v1/customers/customer-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Validation Failed"))

        verify(customerService, never()).findById(any())
        verify(customerService, never()).update(any(), any())
    }

    @Test
    fun `deleteCustomer should delete customer when exists`() {
        // Given
        doNothing().whenever(customerService).deleteById("customer-1")

        // When & Then
        mockMvc.perform(delete("/api/v1/customers/customer-1"))
            .andExpect(status().isNoContent)

        verify(customerService).deleteById("customer-1")
    }

    @Test
    fun `deleteCustomer should return 404 when not exists`() {
        // Given
        doThrow(EntityNotFoundException("Customer", "customer-1")).whenever(customerService).deleteById("customer-1")

        // When & Then
        mockMvc.perform(delete("/api/v1/customers/customer-1"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Not Found"))

        verify(customerService).deleteById("customer-1")
    }

    @Test
    fun `searchCustomers should return matching customers by cifId`() {
        // Given
        val searchResults = listOf(sampleCustomer)
        whenever(customerService.searchByCifId("CIF001")).thenReturn(searchResults)

        // When & Then
        mockMvc.perform(
            get("/api/v1/customers/search")
                .param("cifId", "CIF001")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value("customer-1"))
            .andExpect(jsonPath("$[0].cifId").value("CIF001"))

        verify(customerService).searchByCifId("CIF001")
    }

    @Test
    fun `searchCustomers should return customers by customerTypeId`() {
        // Given
        val searchResults = listOf(sampleCustomer)
        whenever(customerService.findByCustomerTypeId(1)).thenReturn(searchResults)

        // When & Then
        mockMvc.perform(
            get("/api/v1/customers/search")
                .param("customerTypeId", "1")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))

        verify(customerService).findByCustomerTypeId(1)
    }

    @Test
    fun `searchCustomers should return all customers when no search params`() {
        // Given
        whenever(customerService.findAll()).thenReturn(sampleCustomerList)

        // When & Then
        mockMvc.perform(get("/api/v1/customers/search"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))

        verify(customerService).findAll()
    }

    @Test
    fun `searchCustomers should return paginated results`() {
        // Given
        val searchResults = (1..25).map { 
            Customer(
                id = "customer-$it",
                cifId = "CIF00$it",
                refKey = "REF00$it",
                customerTypeId = 1,
                createdAt = LocalDateTime.now(),
                createdBy = "admin",
                updatedAt = null,
                updatedBy = null
            )
        }
        whenever(customerService.searchByCifId("CIF")).thenReturn(searchResults)

        // When & Then
        mockMvc.perform(
            get("/api/v1/customers/search")
                .param("cifId", "CIF")
                .param("page", "1")
                .param("size", "10")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(10))

        verify(customerService).searchByCifId("CIF")
    }

    @Test
    fun `getCustomersByCustomerType should return customers for customer type`() {
        // Given
        val customers = listOf(sampleCustomer)
        whenever(customerService.findByCustomerTypeId(1)).thenReturn(customers)

        // When & Then
        mockMvc.perform(get("/api/v1/customers/by-customer-type/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value("customer-1"))

        verify(customerService).findByCustomerTypeId(1)
    }

    @Test
    fun `getCustomerSummaries should return all customer summaries`() {
        // Given
        whenever(customerService.findAll()).thenReturn(sampleCustomerList)

        // When & Then
        mockMvc.perform(get("/api/v1/customers/summary"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("customer-1"))
            .andExpect(jsonPath("$[0].cifId").value("CIF001"))

        verify(customerService).findAll()
    }

    @Test
    fun `customerExists should return true when exists`() {
        // Given
        whenever(customerService.existsById("customer-1")).thenReturn(true)

        // When & Then
        mockMvc.perform(get("/api/v1/customers/customer-1/exists"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(true))

        verify(customerService).existsById("customer-1")
    }

    @Test
    fun `customerExists should return false when not exists`() {
        // Given
        whenever(customerService.existsById("customer-1")).thenReturn(false)

        // When & Then
        mockMvc.perform(get("/api/v1/customers/customer-1/exists"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exists").value(false))

        verify(customerService).existsById("customer-1")
    }

    @Test
    fun `getCustomerCount should return total count`() {
        // Given
        whenever(customerService.count()).thenReturn(10L)

        // When & Then
        mockMvc.perform(get("/api/v1/customers/count"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.count").value(10))

        verify(customerService).count()
    }
}