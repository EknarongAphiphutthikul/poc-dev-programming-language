package th.eknarong.aph.poc.pocmongodbkotlinspringboot.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountStatus
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.AccountStatusService
import java.time.LocalDateTime

/**
 * REST Controller for AccountStatus entity
 * Provides CRUD operations and search functionality
 */
@RestController
@RequestMapping("/api/v1/account-statuses")
@CrossOrigin(origins = ["*"])
class AccountStatusController(
    private val accountStatusService: AccountStatusService
) {

    /**
     * Get all account statuses with pagination and sorting
     */
    @GetMapping
    fun getAllAccountStatuses(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "name") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String
    ): ResponseEntity<Page<AccountStatusResponse>> {
        val direction = if (sortDir.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(direction, sortBy))
        
        val accountStatusPage = accountStatusService.findAll(pageable)
        val responsePage = accountStatusPage.map { it.toResponse() }
        
        return ResponseEntity.ok(responsePage)
    }

    /**
     * Get account status by ID
     */
    @GetMapping("/{id}")
    fun getAccountStatusById(@PathVariable id: Int): ResponseEntity<AccountStatusResponse> {
        val accountStatus = accountStatusService.findById(id)
        return ResponseEntity.ok(accountStatus.toResponse())
    }

    /**
     * Create a new account status
     */
    @PostMapping
    fun createAccountStatus(
        @Valid @RequestBody request: CreateAccountStatusRequest
    ): ResponseEntity<AccountStatusResponse> {
        val accountStatus = AccountStatus(
            id = request.id,
            name = request.name,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
        
        val createdAccountStatus = accountStatusService.create(accountStatus)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccountStatus.toResponse())
    }

    /**
     * Update an existing account status
     */
    @PutMapping("/{id}")
    fun updateAccountStatus(
        @PathVariable id: Int,
        @Valid @RequestBody request: UpdateAccountStatusRequest
    ): ResponseEntity<AccountStatusResponse> {
        val existingAccountStatus = accountStatusService.findById(id)
        val updatedAccountStatus = existingAccountStatus.copy(
            name = request.name,
            updatedAt = LocalDateTime.now()
        )
        
        val savedAccountStatus = accountStatusService.update(id, updatedAccountStatus)
        return ResponseEntity.ok(savedAccountStatus.toResponse())
    }

    /**
     * Delete account status by ID
     */
    @DeleteMapping("/{id}")
    fun deleteAccountStatus(@PathVariable id: Int): ResponseEntity<Void> {
        accountStatusService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * Search account statuses by name
     */
    @GetMapping("/search")
    fun searchAccountStatuses(
        @RequestParam name: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<AccountStatusSummary>> {
        val accountStatuses = accountStatusService.searchByName(name)
        val summaries = accountStatuses.map { it.toSummary() }
        
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
     * Get account status summary list (for dropdowns)
     */
    @GetMapping("/summary")
    fun getAccountStatusSummaries(): ResponseEntity<List<AccountStatusSummary>> {
        val accountStatuses = accountStatusService.findAll()
        val summaries = accountStatuses.map { it.toSummary() }
        return ResponseEntity.ok(summaries)
    }

    /**
     * Check if account status exists by ID
     */
    @GetMapping("/{id}/exists")
    fun accountStatusExists(@PathVariable id: Int): ResponseEntity<Map<String, Boolean>> {
        val exists = accountStatusService.existsById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    /**
     * Get account status count
     */
    @GetMapping("/count")
    fun getAccountStatusCount(): ResponseEntity<Map<String, Long>> {
        val count = accountStatusService.count()
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
 * Extension function to convert AccountStatus entity to AccountStatusResponse DTO
 */
private fun AccountStatus.toResponse(): AccountStatusResponse {
    return AccountStatusResponse(
        id = this.id,
        name = this.name,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Extension function to convert AccountStatus entity to AccountStatusSummary DTO
 */
private fun AccountStatus.toSummary(): AccountStatusSummary {
    return AccountStatusSummary(
        id = this.id,
        name = this.name
    )
}