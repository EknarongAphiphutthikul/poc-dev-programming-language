package th.eknarong.aph.poc.pocmongodbkotlinspringboot.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.CustomerType
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.CustomerTypeService
import java.time.LocalDateTime

/**
 * REST Controller for CustomerType entity
 * Provides CRUD operations and search functionality
 */
@RestController
@RequestMapping("/api/v1/customer-types")
@CrossOrigin(origins = ["*"])
class CustomerTypeController(
    private val customerTypeService: CustomerTypeService
) {

    /**
     * Get all customer types with pagination and sorting
     * 
     * @param page Page number (0-based, default: 0)
     * @param size Page size (default: 20)
     * @param sortBy Sort field (default: "name")
     * @param sortDir Sort direction (default: "asc")
     * @return Page of customer types
     */
    @GetMapping
    fun getAllCustomerTypes(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "name") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String
    ): ResponseEntity<Page<CustomerTypeResponse>> {
        val direction = if (sortDir.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(direction, sortBy))
        
        val customerTypePage = customerTypeService.findAll(pageable)
        val responsePagee = customerTypePage.map { it.toResponse() }
        
        return ResponseEntity.ok(responsePagee)
    }

    /**
     * Get customer type by ID
     * 
     * @param id Customer type ID
     * @return Customer type details
     */
    @GetMapping("/{id}")
    fun getCustomerTypeById(@PathVariable id: Int): ResponseEntity<CustomerTypeResponse> {
        val customerType = customerTypeService.findById(id)
        return ResponseEntity.ok(customerType.toResponse())
    }

    /**
     * Create a new customer type
     * 
     * @param request Customer type creation request
     * @return Created customer type
     */
    @PostMapping
    fun createCustomerType(
        @Valid @RequestBody request: CreateCustomerTypeRequest
    ): ResponseEntity<CustomerTypeResponse> {
        val customerType = CustomerType(
            id = request.id,
            name = request.name,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
        
        val createdCustomerType = customerTypeService.create(customerType)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomerType.toResponse())
    }

    /**
     * Update an existing customer type
     * 
     * @param id Customer type ID
     * @param request Customer type update request
     * @return Updated customer type
     */
    @PutMapping("/{id}")
    fun updateCustomerType(
        @PathVariable id: Int,
        @Valid @RequestBody request: UpdateCustomerTypeRequest
    ): ResponseEntity<CustomerTypeResponse> {
        val existingCustomerType = customerTypeService.findById(id)
        val updatedCustomerType = existingCustomerType.copy(
            name = request.name,
            updatedAt = LocalDateTime.now()
        )
        
        val savedCustomerType = customerTypeService.update(id, updatedCustomerType)
        return ResponseEntity.ok(savedCustomerType.toResponse())
    }

    /**
     * Delete customer type by ID
     * 
     * @param id Customer type ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    fun deleteCustomerType(@PathVariable id: Int): ResponseEntity<Void> {
        customerTypeService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * Search customer types by name
     * 
     * @param name Search term for customer type name
     * @param page Page number (0-based, default: 0)
     * @param size Page size (default: 20)
     * @return List of matching customer types
     */
    @GetMapping("/search")
    fun searchCustomerTypes(
        @RequestParam name: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<CustomerTypeSummary>> {
        val customerTypes = customerTypeService.searchByName(name)
        val summaries = customerTypes.map { it.toSummary() }
        
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
     * Get customer type summary list (for dropdowns)
     * 
     * @return List of customer type summaries
     */
    @GetMapping("/summary")
    fun getCustomerTypeSummaries(): ResponseEntity<List<CustomerTypeSummary>> {
        val customerTypes = customerTypeService.findAll()
        val summaries = customerTypes.map { it.toSummary() }
        return ResponseEntity.ok(summaries)
    }

    /**
     * Check if customer type exists by ID
     * 
     * @param id Customer type ID
     * @return Boolean indicating existence
     */
    @GetMapping("/{id}/exists")
    fun customerTypeExists(@PathVariable id: Int): ResponseEntity<Map<String, Boolean>> {
        val exists = customerTypeService.existsById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    /**
     * Get customer type count
     * 
     * @return Total count of customer types
     */
    @GetMapping("/count")
    fun getCustomerTypeCount(): ResponseEntity<Map<String, Long>> {
        val count = customerTypeService.count()
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
 * Extension function to convert CustomerType entity to CustomerTypeResponse DTO
 */
private fun CustomerType.toResponse(): CustomerTypeResponse {
    return CustomerTypeResponse(
        id = this.id,
        name = this.name,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Extension function to convert CustomerType entity to CustomerTypeSummary DTO
 */
private fun CustomerType.toSummary(): CustomerTypeSummary {
    return CustomerTypeSummary(
        id = this.id,
        name = this.name
    )
}