package th.eknarong.aph.poc.pocmongodbkotlinspringboot.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto.*
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountCategory
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.service.AccountCategoryService
import java.time.LocalDateTime

/**
 * REST Controller for AccountCategory entity
 * Provides CRUD operations and search functionality
 */
@RestController
@RequestMapping("/api/v1/account-categories")
@CrossOrigin(origins = ["*"])
class AccountCategoryController(
    private val accountCategoryService: AccountCategoryService
) {

    /**
     * Get all account categories with pagination and sorting
     */
    @GetMapping
    fun getAllAccountCategories(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "name") sortBy: String,
        @RequestParam(defaultValue = "asc") sortDir: String
    ): ResponseEntity<Page<AccountCategoryResponse>> {
        val direction = if (sortDir.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(direction, sortBy))
        
        val accountCategoryPage = accountCategoryService.findAll(pageable)
        val responsePage = accountCategoryPage.map { it.toResponse() }
        
        return ResponseEntity.ok(responsePage)
    }

    /**
     * Get account category by ID
     */
    @GetMapping("/{id}")
    fun getAccountCategoryById(@PathVariable id: Int): ResponseEntity<AccountCategoryResponse> {
        val accountCategory = accountCategoryService.findById(id)
        return ResponseEntity.ok(accountCategory.toResponse())
    }

    /**
     * Create a new account category
     */
    @PostMapping
    fun createAccountCategory(
        @Valid @RequestBody request: CreateAccountCategoryRequest
    ): ResponseEntity<AccountCategoryResponse> {
        val accountCategory = AccountCategory(
            id = request.id,
            name = request.name,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
        
        val createdAccountCategory = accountCategoryService.create(accountCategory)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccountCategory.toResponse())
    }

    /**
     * Update an existing account category
     */
    @PutMapping("/{id}")
    fun updateAccountCategory(
        @PathVariable id: Int,
        @Valid @RequestBody request: UpdateAccountCategoryRequest
    ): ResponseEntity<AccountCategoryResponse> {
        val existingAccountCategory = accountCategoryService.findById(id)
        val updatedAccountCategory = existingAccountCategory.copy(
            name = request.name,
            updatedAt = LocalDateTime.now()
        )
        
        val savedAccountCategory = accountCategoryService.update(id, updatedAccountCategory)
        return ResponseEntity.ok(savedAccountCategory.toResponse())
    }

    /**
     * Delete account category by ID
     */
    @DeleteMapping("/{id}")
    fun deleteAccountCategory(@PathVariable id: Int): ResponseEntity<Void> {
        accountCategoryService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * Search account categories by name
     */
    @GetMapping("/search")
    fun searchAccountCategories(
        @RequestParam name: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<AccountCategorySummary>> {
        val accountCategories = accountCategoryService.searchByName(name)
        val summaries = accountCategories.map { it.toSummary() }
        
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
     * Get account category summary list (for dropdowns)
     */
    @GetMapping("/summary")
    fun getAccountCategorySummaries(): ResponseEntity<List<AccountCategorySummary>> {
        val accountCategories = accountCategoryService.findAll()
        val summaries = accountCategories.map { it.toSummary() }
        return ResponseEntity.ok(summaries)
    }

    /**
     * Check if account category exists by ID
     */
    @GetMapping("/{id}/exists")
    fun accountCategoryExists(@PathVariable id: Int): ResponseEntity<Map<String, Boolean>> {
        val exists = accountCategoryService.existsById(id)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    /**
     * Get account category count
     */
    @GetMapping("/count")
    fun getAccountCategoryCount(): ResponseEntity<Map<String, Long>> {
        val count = accountCategoryService.count()
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
 * Extension function to convert AccountCategory entity to AccountCategoryResponse DTO
 */
private fun AccountCategory.toResponse(): AccountCategoryResponse {
    return AccountCategoryResponse(
        id = this.id,
        name = this.name,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Extension function to convert AccountCategory entity to AccountCategorySummary DTO
 */
private fun AccountCategory.toSummary(): AccountCategorySummary {
    return AccountCategorySummary(
        id = this.id,
        name = this.name
    )
}