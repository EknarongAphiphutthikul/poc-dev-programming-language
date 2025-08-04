package th.eknarong.aph.poc.pocmongodbkotlinspringboot.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountStatusHistory
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.AccountStatusHistoryService
import java.time.LocalDateTime
import java.util.*

/**
 * REST Controller for AccountStatusHistory entity
 * Provides CRUD operations and search functionality
 */
@RestController
@RequestMapping("/api/v1/account-status-histories")
@CrossOrigin(origins = ["*"])
class AccountStatusHistoryController(
    private val accountStatusHistoryService: AccountStatusHistoryService
) {

    /**
     * Get all account status histories with pagination and sorting
     */
    @GetMapping
    fun getAllAccountStatusHistories(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDir: String
    ): ResponseEntity<Page<AccountStatusHistoryResponse>> {
        val direction = if (sortDir.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(direction, sortBy))
        
        val accountStatusHistoryPage = accountStatusHistoryService.findAll(pageable)
        val responsePage = accountStatusHistoryPage.map { it.toResponse() }
        
        return ResponseEntity.ok(responsePage)
    }

    /**
     * Get account status history by ID
     */
    @GetMapping("/{id}")
    fun getAccountStatusHistoryById(@PathVariable id: String): ResponseEntity<AccountStatusHistoryResponse> {
        val accountStatusHistory = accountStatusHistoryService.findById(id)
        return ResponseEntity.ok(accountStatusHistory.toResponse())
    }

    /**
     * Create a new account status history
     */
    @PostMapping
    fun createAccountStatusHistory(
        @Valid @RequestBody request: CreateAccountStatusHistoryRequest
    ): ResponseEntity<AccountStatusHistoryResponse> {
        val accountStatusHistory = AccountStatusHistory(
            id = UUID.randomUUID().toString(),
            accountId = request.accountId,
            statusId = request.statusId,
            createdAt = LocalDateTime.now(),
            createdBy = request.createdBy,
            updatedAt = null,
            updatedBy = null
        )
        
        val createdAccountStatusHistory = accountStatusHistoryService.create(accountStatusHistory)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccountStatusHistory.toResponse())
    }

    /**
     * Update an existing account status history
     */
    @PutMapping("/{id}")
    fun updateAccountStatusHistory(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateAccountStatusHistoryRequest
    ): ResponseEntity<AccountStatusHistoryResponse> {
        val existingAccountStatusHistory = accountStatusHistoryService.findById(id)
        val updatedAccountStatusHistory = existingAccountStatusHistory.copy(
            accountId = request.accountId,
            statusId = request.statusId,
            updatedAt = LocalDateTime.now(),
            updatedBy = request.updatedBy
        )
        
        val savedAccountStatusHistory = accountStatusHistoryService.update(id, updatedAccountStatusHistory)
        return ResponseEntity.ok(savedAccountStatusHistory.toResponse())
    }

    /**
     * Delete account status history by ID
     */
    @DeleteMapping("/{id}")
    fun deleteAccountStatusHistory(@PathVariable id: String): ResponseEntity<Void> {
        accountStatusHistoryService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * Search account status histories by account ID
     */
    @GetMapping("/search")
    fun searchAccountStatusHistories(
        @RequestParam(required = false) accountId: String?,
        @RequestParam(required = false) statusId: Int?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<AccountStatusHistorySummary>> {
        val accountStatusHistories = when {
            accountId != null -> accountStatusHistoryService.findByAccountId(accountId)
            statusId != null -> accountStatusHistoryService.findByStatusId(statusId)
            else -> accountStatusHistoryService.findAll()
        }
        
        val summaries = accountStatusHistories.map { it.toSummary() }
        
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
     * Get account status histories by account ID
     */
    @GetMapping("/by-account/{accountId}")
    fun getAccountStatusHistoriesByAccount(
        @PathVariable accountId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDir: String
    ): ResponseEntity<List<AccountStatusHistoryResponse>> {
        val accountStatusHistories = accountStatusHistoryService.findByAccountId(accountId)
        
        // Sort the results
        val sortedHistories = when {
            sortBy == "createdAt" && sortDir.lowercase() == "desc" -> 
                accountStatusHistories.sortedByDescending { it.createdAt }
            sortBy == "createdAt" && sortDir.lowercase() == "asc" -> 
                accountStatusHistories.sortedBy { it.createdAt }
            else -> accountStatusHistories.sortedByDescending { it.createdAt }
        }
        
        val responses = sortedHistories.map { it.toResponse() }
        
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
     * Get account status histories by status ID
     */
    @GetMapping("/by-status/{statusId}")
    fun getAccountStatusHistoriesByStatus(
        @PathVariable statusId: Int,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<AccountStatusHistorySummary>> {
        val accountStatusHistories = accountStatusHistoryService.findByStatusId(statusId)
        val summaries = accountStatusHistories.map { it.toSummary() }
        
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
     * Get account status history summary list
     */
    @GetMapping("/summary")
    fun getAccountStatusHistorySummaries(): ResponseEntity<List<AccountStatusHistorySummary>> {
        val accountStatusHistories = accountStatusHistoryService.findAll()
        val summaries = accountStatusHistories.map { it.toSummary() }
        return ResponseEntity.ok(summaries)
    }

    /**
     * Check if account status history exists by ID
     */
    @GetMapping("/{id}/exists")
    fun accountStatusHistoryExists(@PathVariable id: String): ResponseEntity<Map<String, Boolean>> {
        val exists = accountStatusHistoryService.existsById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    /**
     * Get account status history count
     */
    @GetMapping("/count")
    fun getAccountStatusHistoryCount(): ResponseEntity<Map<String, Long>> {
        val count = accountStatusHistoryService.count()
        return ResponseEntity.ok(mapOf("count" to count))
    }

    /**
     * Get account status history count by account ID
     */
    @GetMapping("/count/by-account/{accountId}")
    fun getAccountStatusHistoryCountByAccount(@PathVariable accountId: String): ResponseEntity<Map<String, Int>> {
        val accountStatusHistories = accountStatusHistoryService.findByAccountId(accountId)
        val count = accountStatusHistories.size
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
 * Extension function to convert AccountStatusHistory entity to AccountStatusHistoryResponse DTO
 */
private fun AccountStatusHistory.toResponse(): AccountStatusHistoryResponse {
    return AccountStatusHistoryResponse(
        id = this.id,
        accountId = this.accountId,
        statusId = this.statusId,
        createdAt = this.createdAt,
        createdBy = this.createdBy,
        updatedAt = this.updatedAt,
        updatedBy = this.updatedBy
    )
}

/**
 * Extension function to convert AccountStatusHistory entity to AccountStatusHistorySummary DTO
 */
private fun AccountStatusHistory.toSummary(): AccountStatusHistorySummary {
    return AccountStatusHistorySummary(
        id = this.id,
        accountId = this.accountId,
        statusId = this.statusId,
        createdAt = this.createdAt
    )
}