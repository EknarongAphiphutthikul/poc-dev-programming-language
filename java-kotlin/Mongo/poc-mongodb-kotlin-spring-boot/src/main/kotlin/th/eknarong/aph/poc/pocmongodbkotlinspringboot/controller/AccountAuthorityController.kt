package th.eknarong.aph.poc.pocmongodbkotlinspringboot.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountAuthority
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.AccountAuthorityService
import java.time.LocalDateTime
import java.util.*

/**
 * REST Controller for AccountAuthority entity
 * Provides CRUD operations and search functionality
 */
@RestController
@RequestMapping("/api/v1/account-authorities")
@CrossOrigin(origins = ["*"])
class AccountAuthorityController(
    private val accountAuthorityService: AccountAuthorityService
) {

    /**
     * Get all account authorities with pagination and sorting
     */
    @GetMapping
    fun getAllAccountAuthorities(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDir: String
    ): ResponseEntity<Page<AccountAuthorityResponse>> {
        val direction = if (sortDir.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(direction, sortBy))
        
        val accountAuthorityPage = accountAuthorityService.findAll(pageable)
        val responsePage = accountAuthorityPage.map { it.toResponse() }
        
        return ResponseEntity.ok(responsePage)
    }

    /**
     * Get account authority by ID
     */
    @GetMapping("/{id}")
    fun getAccountAuthorityById(@PathVariable id: String): ResponseEntity<AccountAuthorityResponse> {
        val accountAuthority = accountAuthorityService.findById(id)
        return ResponseEntity.ok(accountAuthority.toResponse())
    }

    /**
     * Create a new account authority
     */
    @PostMapping
    fun createAccountAuthority(
        @Valid @RequestBody request: CreateAccountAuthorityRequest
    ): ResponseEntity<AccountAuthorityResponse> {
        val accountAuthority = AccountAuthority(
            id = UUID.randomUUID().toString(),
            customerId = request.customerId,
            accountNumber = request.accountNumber,
            position = request.position,
            effectiveDateFrom = request.effectiveDateFrom,
            effectiveDateTo = request.effectiveDateTo,
            createdAt = LocalDateTime.now(),
            createdBy = request.createdBy,
            updatedAt = null,
            updatedBy = null
        )
        
        val createdAccountAuthority = accountAuthorityService.create(accountAuthority)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccountAuthority.toResponse())
    }

    /**
     * Update an existing account authority
     */
    @PutMapping("/{id}")
    fun updateAccountAuthority(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateAccountAuthorityRequest
    ): ResponseEntity<AccountAuthorityResponse> {
        val existingAccountAuthority = accountAuthorityService.findById(id)
        val updatedAccountAuthority = existingAccountAuthority.copy(
            customerId = request.customerId,
            accountNumber = request.accountNumber,
            position = request.position,
            effectiveDateFrom = request.effectiveDateFrom,
            effectiveDateTo = request.effectiveDateTo,
            updatedAt = LocalDateTime.now(),
            updatedBy = request.updatedBy
        )
        
        val savedAccountAuthority = accountAuthorityService.update(id, updatedAccountAuthority)
        return ResponseEntity.ok(savedAccountAuthority.toResponse())
    }

    /**
     * Delete account authority by ID
     */
    @DeleteMapping("/{id}")
    fun deleteAccountAuthority(@PathVariable id: String): ResponseEntity<Void> {
        accountAuthorityService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * Search account authorities by various criteria
     */
    @GetMapping("/search")
    fun searchAccountAuthorities(
        @RequestParam(required = false) customerId: String?,
        @RequestParam(required = false) accountNumber: String?,
        @RequestParam(required = false) position: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<AccountAuthoritySummary>> {
        val accountAuthorities = when {
            customerId != null -> accountAuthorityService.findByCustomerId(customerId)
            accountNumber != null -> accountAuthorityService.findByAccountNumber(accountNumber)
            position != null -> accountAuthorityService.searchByPosition(position)
            else -> accountAuthorityService.findAll()
        }
        
        val summaries = accountAuthorities.map { it.toSummary() }
        
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
     * Get account authorities by customer ID
     */
    @GetMapping("/by-customer/{customerId}")
    fun getAccountAuthoritiesByCustomer(
        @PathVariable customerId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<AccountAuthorityResponse>> {
        val accountAuthorities = accountAuthorityService.findByCustomerId(customerId)
        val responses = accountAuthorities.map { it.toResponse() }
        
        // Manual pagination
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, responses.size)
        val paginatedResults = if (startIndex < responses.size) {
            responses.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        return ResponseEntity.ok(paginatedResults)
    }

    /**
     * Get account authorities by account number
     */
    @GetMapping("/by-account/{accountNumber}")
    fun getAccountAuthoritiesByAccount(
        @PathVariable accountNumber: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<AccountAuthorityResponse>> {
        val accountAuthorities = accountAuthorityService.findByAccountNumber(accountNumber)
        val responses = accountAuthorities.map { it.toResponse() }
        
        // Manual pagination
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, responses.size)
        val paginatedResults = if (startIndex < responses.size) {
            responses.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        return ResponseEntity.ok(paginatedResults)
    }

    /**
     * Get active account authorities (where effectiveDateTo is null or in the future)
     */
    @GetMapping("/active")
    fun getActiveAccountAuthorities(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<AccountAuthorityResponse>> {
        val allAuthorities = accountAuthorityService.findAll()
        val now = LocalDateTime.now()
        
        val activeAuthorities = allAuthorities.filter { authority ->
            authority.effectiveDateTo == null || authority.effectiveDateTo.isAfter(now)
        }
        
        val responses = activeAuthorities.map { it.toResponse() }
        
        // Manual pagination
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, responses.size)
        val paginatedResults = if (startIndex < responses.size) {
            responses.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        return ResponseEntity.ok(paginatedResults)
    }

    /**
     * Get expired account authorities
     */
    @GetMapping("/expired")
    fun getExpiredAccountAuthorities(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<AccountAuthorityResponse>> {
        val allAuthorities = accountAuthorityService.findAll()
        val now = LocalDateTime.now()
        
        val expiredAuthorities = allAuthorities.filter { authority ->
            authority.effectiveDateTo != null && authority.effectiveDateTo.isBefore(now)
        }
        
        val responses = expiredAuthorities.map { it.toResponse() }
        
        // Manual pagination
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, responses.size)
        val paginatedResults = if (startIndex < responses.size) {
            responses.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        return ResponseEntity.ok(paginatedResults)
    }

    /**
     * Get account authority summary list
     */
    @GetMapping("/summary")
    fun getAccountAuthoritySummaries(): ResponseEntity<List<AccountAuthoritySummary>> {
        val accountAuthorities = accountAuthorityService.findAll()
        val summaries = accountAuthorities.map { it.toSummary() }
        return ResponseEntity.ok(summaries)
    }

    /**
     * Check if account authority exists by ID
     */
    @GetMapping("/{id}/exists")
    fun accountAuthorityExists(@PathVariable id: String): ResponseEntity<Map<String, Boolean>> {
        val exists = accountAuthorityService.existsById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    /**
     * Get account authority count
     */
    @GetMapping("/count")
    fun getAccountAuthorityCount(): ResponseEntity<Map<String, Long>> {
        val count = accountAuthorityService.count()
        return ResponseEntity.ok(mapOf("count" to count))
    }

    /**
     * Get account authority count by customer ID
     */
    @GetMapping("/count/by-customer/{customerId}")
    fun getAccountAuthorityCountByCustomer(@PathVariable customerId: String): ResponseEntity<Map<String, Int>> {
        val accountAuthorities = accountAuthorityService.findByCustomerId(customerId)
        val count = accountAuthorities.size
        return ResponseEntity.ok(mapOf("count" to count))
    }

    /**
     * Get account authority count by account number
     */
    @GetMapping("/count/by-account/{accountNumber}")
    fun getAccountAuthorityCountByAccount(@PathVariable accountNumber: String): ResponseEntity<Map<String, Int>> {
        val accountAuthorities = accountAuthorityService.findByAccountNumber(accountNumber)
        val count = accountAuthorities.size
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
 * Extension function to convert AccountAuthority entity to AccountAuthorityResponse DTO
 */
private fun AccountAuthority.toResponse(): AccountAuthorityResponse {
    return AccountAuthorityResponse(
        id = this.id,
        customerId = this.customerId,
        accountNumber = this.accountNumber,
        position = this.position,
        effectiveDateFrom = this.effectiveDateFrom,
        effectiveDateTo = this.effectiveDateTo,
        createdAt = this.createdAt,
        createdBy = this.createdBy,
        updatedAt = this.updatedAt,
        updatedBy = this.updatedBy
    )
}

/**
 * Extension function to convert AccountAuthority entity to AccountAuthoritySummary DTO
 */
private fun AccountAuthority.toSummary(): AccountAuthoritySummary {
    return AccountAuthoritySummary(
        id = this.id,
        customerId = this.customerId,
        accountNumber = this.accountNumber,
        position = this.position
    )
}