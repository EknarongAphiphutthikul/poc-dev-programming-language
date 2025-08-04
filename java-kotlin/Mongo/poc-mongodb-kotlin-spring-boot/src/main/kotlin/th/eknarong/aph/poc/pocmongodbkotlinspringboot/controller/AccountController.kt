package th.eknarong.aph.poc.pocmongodbkotlinspringboot.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.Account
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.AccountService
import java.time.LocalDateTime
import java.util.*

/**
 * REST Controller for Account entity
 * Provides CRUD operations and search functionality
 */
@RestController
@RequestMapping("/api/v1/accounts")
@CrossOrigin(origins = ["*"])
class AccountController(
    private val accountService: AccountService
) {

    /**
     * Get all accounts with pagination and sorting
     */
    @GetMapping
    fun getAllAccounts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "productCode") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String
    ): ResponseEntity<Page<AccountResponse>> {
        val direction = if (sortDir.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(direction, sortBy))
        
        val accountPage = accountService.findAll(pageable)
        val responsePage = accountPage.map { it.toResponse() }
        
        return ResponseEntity.ok(responsePage)
    }

    /**
     * Get account by ID
     */
    @GetMapping("/{id}")
    fun getAccountById(@PathVariable id: String): ResponseEntity<AccountResponse> {
        val account = accountService.findById(id)
        return ResponseEntity.ok(account.toResponse())
    }

    /**
     * Create a new account
     */
    @PostMapping
    fun createAccount(
        @Valid @RequestBody request: CreateAccountRequest
    ): ResponseEntity<AccountResponse> {
        val account = Account(
            id = UUID.randomUUID().toString(),
            customerId = request.customerId,
            productId = request.productId,
            productCode = request.productCode,
            productCategory = request.productCategory,
            accountNumber = request.accountNumber,
            parentAccountId = request.parentAccountId,
            accountRefKey = request.accountRefKey,
            accountCategoryId = request.accountCategoryId,
            statusId = request.statusId,
            interestType = request.interestType,
            openedDate = request.openedDate,
            closureDate = request.closureDate,
            attributes = request.attributes,
            createdAt = LocalDateTime.now(),
            createdBy = request.createdBy,
            updatedAt = null,
            updatedBy = null
        )
        
        val createdAccount = accountService.create(account)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount.toResponse())
    }

    /**
     * Update an existing account
     */
    @PutMapping("/{id}")
    fun updateAccount(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateAccountRequest
    ): ResponseEntity<AccountResponse> {
        val existingAccount = accountService.findById(id)
        val updatedAccount = existingAccount.copy(
            customerId = request.customerId,
            productId = request.productId,
            productCode = request.productCode,
            productCategory = request.productCategory,
            accountNumber = request.accountNumber,
            parentAccountId = request.parentAccountId,
            accountRefKey = request.accountRefKey,
            accountCategoryId = request.accountCategoryId,
            statusId = request.statusId,
            interestType = request.interestType,
            openedDate = request.openedDate,
            closureDate = request.closureDate,
            attributes = request.attributes,
            updatedAt = LocalDateTime.now(),
            updatedBy = request.updatedBy
        )
        
        val savedAccount = accountService.update(id, updatedAccount)
        return ResponseEntity.ok(savedAccount.toResponse())
    }

    /**
     * Delete account by ID
     */
    @DeleteMapping("/{id}")
    fun deleteAccount(@PathVariable id: String): ResponseEntity<Void> {
        accountService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * Search accounts by various criteria
     */
    @GetMapping("/search")
    fun searchAccounts(
        @RequestParam(required = false) customerId: String?,
        @RequestParam(required = false) productCode: String?,
        @RequestParam(required = false) accountNumber: String?,
        @RequestParam(required = false) statusId: Int?,
        @RequestParam(required = false) accountCategoryId: Int?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<AccountSummary>> {
        val accounts = when {
            customerId != null -> accountService.findByCustomerId(customerId)
            productCode != null -> accountService.searchByProductCode(productCode)
            accountNumber != null -> accountService.findByAccountNumber(accountNumber)
            statusId != null -> accountService.findByStatusId(statusId)
            accountCategoryId != null -> accountService.findByAccountCategoryId(accountCategoryId)
            else -> accountService.findAll()
        }
        
        val summaries = accounts.map { it.toSummary() }
        
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
     * Get accounts by customer ID
     */
    @GetMapping("/by-customer/{customerId}")
    fun getAccountsByCustomer(
        @PathVariable customerId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<AccountSummary>> {
        val accounts = accountService.findByCustomerId(customerId)
        val summaries = accounts.map { it.toSummary() }
        
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
     * Get accounts by status ID
     */
    @GetMapping("/by-status/{statusId}")
    fun getAccountsByStatus(
        @PathVariable statusId: Int,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<AccountSummary>> {
        val accounts = accountService.findByStatusId(statusId)
        val summaries = accounts.map { it.toSummary() }
        
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
     * Get accounts by account category ID
     */
    @GetMapping("/by-category/{accountCategoryId}")
    fun getAccountsByCategory(
        @PathVariable accountCategoryId: Int,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<AccountSummary>> {
        val accounts = accountService.findByAccountCategoryId(accountCategoryId)
        val summaries = accounts.map { it.toSummary() }
        
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
     * Get account summary list
     */
    @GetMapping("/summary")
    fun getAccountSummaries(): ResponseEntity<List<AccountSummary>> {
        val accounts = accountService.findAll()
        val summaries = accounts.map { it.toSummary() }
        return ResponseEntity.ok(summaries)
    }

    /**
     * Check if account exists by ID
     */
    @GetMapping("/{id}/exists")
    fun accountExists(@PathVariable id: String): ResponseEntity<Map<String, Boolean>> {
        val exists = accountService.existsById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    /**
     * Get account count
     */
    @GetMapping("/count")
    fun getAccountCount(): ResponseEntity<Map<String, Long>> {
        val count = accountService.count()
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
 * Extension function to convert Account entity to AccountResponse DTO
 */
private fun Account.toResponse(): AccountResponse {
    return AccountResponse(
        id = this.id,
        customerId = this.customerId,
        productId = this.productId,
        productCode = this.productCode,
        productCategory = this.productCategory,
        accountNumber = this.accountNumber,
        parentAccountId = this.parentAccountId,
        accountRefKey = this.accountRefKey,
        accountCategoryId = this.accountCategoryId,
        statusId = this.statusId,
        interestType = this.interestType,
        openedDate = this.openedDate,
        closureDate = this.closureDate,
        attributes = this.attributes,
        createdAt = this.createdAt,
        createdBy = this.createdBy,
        updatedAt = this.updatedAt,
        updatedBy = this.updatedBy
    )
}

/**
 * Extension function to convert Account entity to AccountSummary DTO
 */
private fun Account.toSummary(): AccountSummary {
    return AccountSummary(
        id = this.id,
        customerId = this.customerId,
        productCode = this.productCode,
        accountNumber = this.accountNumber,
        statusId = this.statusId
    )
}