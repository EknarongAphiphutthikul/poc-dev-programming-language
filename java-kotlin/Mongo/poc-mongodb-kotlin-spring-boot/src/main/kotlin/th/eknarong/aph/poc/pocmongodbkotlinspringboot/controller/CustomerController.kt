package th.eknarong.aph.poc.pocmongodbkotlinspringboot.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.Customer
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.CustomerService
import java.time.LocalDateTime
import java.util.*

/**
 * REST Controller for Customer entity
 * Provides CRUD operations and search functionality
 */
@RestController
@RequestMapping("/api/v1/customers")
@CrossOrigin(origins = ["*"])
class CustomerController(
    private val customerService: CustomerService
) {

    /**
     * Get all customers with pagination and sorting
     */
    @GetMapping
    fun getAllCustomers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "cifId") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String
    ): ResponseEntity<Page<CustomerResponse>> {
        val direction = if (sortDir.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(direction, sortBy))
        
        val customerPage = customerService.findAll(pageable)
        val responsePage = customerPage.map { it.toResponse() }
        
        return ResponseEntity.ok(responsePage)
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    fun getCustomerById(@PathVariable id: String): ResponseEntity<CustomerResponse> {
        val customer = customerService.findById(id)
        return ResponseEntity.ok(customer.toResponse())
    }

    /**
     * Create a new customer
     */
    @PostMapping
    fun createCustomer(
        @Valid @RequestBody request: CreateCustomerRequest
    ): ResponseEntity<CustomerResponse> {
        val customer = Customer(
            id = UUID.randomUUID().toString(),
            cifId = request.cifId,
            refKey = request.refKey,
            customerTypeId = request.customerTypeId,
            createdAt = LocalDateTime.now(),
            createdBy = request.createdBy,
            updatedAt = null,
            updatedBy = null
        )
        
        val createdCustomer = customerService.create(customer)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer.toResponse())
    }

    /**
     * Update an existing customer
     */
    @PutMapping("/{id}")
    fun updateCustomer(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateCustomerRequest
    ): ResponseEntity<CustomerResponse> {
        val existingCustomer = customerService.findById(id)
        val updatedCustomer = existingCustomer.copy(
            cifId = request.cifId,
            refKey = request.refKey,
            customerTypeId = request.customerTypeId,
            updatedAt = LocalDateTime.now(),
            updatedBy = request.updatedBy
        )
        
        val savedCustomer = customerService.update(id, updatedCustomer)
        return ResponseEntity.ok(savedCustomer.toResponse())
    }

    /**
     * Delete customer by ID
     */
    @DeleteMapping("/{id}")
    fun deleteCustomer(@PathVariable id: String): ResponseEntity<Void> {
        customerService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * Search customers by CIF ID
     */
    @GetMapping("/search")
    fun searchCustomers(
        @RequestParam(required = false) cifId: String?,
        @RequestParam(required = false) customerTypeId: Int?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<CustomerSummary>> {
        val customers = when {
            cifId != null -> customerService.searchByCifId(cifId)
            customerTypeId != null -> customerService.findByCustomerTypeId(customerTypeId)
            else -> customerService.findAll()
        }
        
        val summaries = customers.map { it.toSummary() }
        
        // Manual pagination for search results
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, summaries.size)
        val paginatedResults = if (startIndex < summaries.size) {
            summaries.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        return ResponseEntity.ok(paginatedResults)
    }

    /**
     * Get customers by customer type ID
     */
    @GetMapping("/by-customer-type/{customerTypeId}")
    fun getCustomersByCustomerType(
        @PathVariable customerTypeId: Int,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<CustomerSummary>> {
        val customers = customerService.findByCustomerTypeId(customerTypeId)
        val summaries = customers.map { it.toSummary() }
        
        // Manual pagination
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, summaries.size)
        val paginatedResults = if (startIndex < summaries.size) {
            summaries.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        return ResponseEntity.ok(paginatedResults)
    }

    /**
     * Get customer summary list
     */
    @GetMapping("/summary")
    fun getCustomerSummaries(): ResponseEntity<List<CustomerSummary>> {
        val customers = customerService.findAll()
        val summaries = customers.map { it.toSummary() }
        return ResponseEntity.ok(summaries)
    }

    /**
     * Check if customer exists by ID
     */
    @GetMapping("/{id}/exists")
    fun customerExists(@PathVariable id: String): ResponseEntity<Map<String, Boolean>> {
        val exists = customerService.existsById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    /**
     * Get customer count
     */
    @GetMapping("/count")
    fun getCustomerCount(): ResponseEntity<Map<String, Long>> {
        val count = customerService.count()
        return ResponseEntity.ok(mapOf("count" to count))
    }

    /**
     * Exception handler for EntityNotFoundException
     */
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<Map<String, String>> {
        val errorResponse = mapOf(
            "error" to "Not Found",
            "message" to ex.message.orEmpty(),
            "timestamp" to LocalDateTime.now().toString()
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: org.springframework.web.bind.MethodArgumentNotValidException
    ): ResponseEntity<Map<String, Any>> {
        val errors = ex.bindingResult.fieldErrors.associate { 
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        
        val errorResponse = mapOf(
            "error" to "Validation Failed",
            "message" to "Request validation failed",
            "errors" to errors,
            "timestamp" to LocalDateTime.now().toString()
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * Exception handler for general exceptions
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<Map<String, String>> {
        val errorResponse = mapOf(
            "error" to "Internal Server Error",
            "message" to (ex.message ?: "An unexpected error occurred"),
            "timestamp" to LocalDateTime.now().toString()
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}

/**
 * Extension function to convert Customer entity to CustomerResponse DTO
 */
private fun Customer.toResponse(): CustomerResponse {
    return CustomerResponse(
        id = this.id,
        cifId = this.cifId,
        refKey = this.refKey,
        customerTypeId = this.customerTypeId,
        createdAt = this.createdAt,
        createdBy = this.createdBy,
        updatedAt = this.updatedAt,
        updatedBy = this.updatedBy
    )
}

/**
 * Extension function to convert Customer entity to CustomerSummary DTO
 */
private fun Customer.toSummary(): CustomerSummary {
    return CustomerSummary(
        id = this.id,
        cifId = this.cifId,
        customerTypeId = this.customerTypeId
    )
}