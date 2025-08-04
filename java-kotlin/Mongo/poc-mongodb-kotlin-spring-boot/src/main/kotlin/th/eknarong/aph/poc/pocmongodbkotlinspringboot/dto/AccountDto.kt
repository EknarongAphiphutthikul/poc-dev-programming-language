package th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * Request DTO for creating a new Account
 */
data class CreateAccountRequest(
    @field:NotBlank(message = "Customer ID cannot be blank")
    val customerId: String,
    
    @field:NotBlank(message = "Product ID cannot be blank")
    val productId: String,
    
    @field:NotBlank(message = "Product code cannot be blank")
    @field:Size(min = 1, max = 50, message = "Product code must be between 1 and 50 characters")
    val productCode: String,
    
    @field:NotBlank(message = "Product category cannot be blank")
    @field:Size(min = 1, max = 100, message = "Product category must be between 1 and 100 characters")
    val productCategory: String,
    
    @field:Size(max = 50, message = "Account number must not exceed 50 characters")
    val accountNumber: String? = null,
    
    val parentAccountId: String? = null,
    
    @field:Size(max = 100, message = "Account reference key must not exceed 100 characters")
    val accountRefKey: String? = null,
    
    @field:NotNull(message = "Account category ID cannot be null")
    val accountCategoryId: Int,
    
    @field:NotNull(message = "Status ID cannot be null")
    val statusId: Int,
    
    @field:NotBlank(message = "Interest type cannot be blank")
    @field:Size(min = 1, max = 50, message = "Interest type must be between 1 and 50 characters")
    val interestType: String,
    
    val openedDate: LocalDateTime? = null,
    
    val closureDate: LocalDateTime? = null,
    
    val attributes: Map<String, Any>? = null,
    
    @field:NotBlank(message = "Created by cannot be blank")
    @field:Size(min = 1, max = 100, message = "Created by must be between 1 and 100 characters")
    val createdBy: String
)

/**
 * Request DTO for updating an existing Account
 */
data class UpdateAccountRequest(
    @field:NotBlank(message = "Customer ID cannot be blank")
    val customerId: String,
    
    @field:NotBlank(message = "Product ID cannot be blank")
    val productId: String,
    
    @field:NotBlank(message = "Product code cannot be blank")
    @field:Size(min = 1, max = 50, message = "Product code must be between 1 and 50 characters")
    val productCode: String,
    
    @field:NotBlank(message = "Product category cannot be blank")
    @field:Size(min = 1, max = 100, message = "Product category must be between 1 and 100 characters")
    val productCategory: String,
    
    @field:Size(max = 50, message = "Account number must not exceed 50 characters")
    val accountNumber: String? = null,
    
    val parentAccountId: String? = null,
    
    @field:Size(max = 100, message = "Account reference key must not exceed 100 characters")
    val accountRefKey: String? = null,
    
    @field:NotNull(message = "Account category ID cannot be null")
    val accountCategoryId: Int,
    
    @field:NotNull(message = "Status ID cannot be null")
    val statusId: Int,
    
    @field:NotBlank(message = "Interest type cannot be blank")
    @field:Size(min = 1, max = 50, message = "Interest type must be between 1 and 50 characters")
    val interestType: String,
    
    val openedDate: LocalDateTime? = null,
    
    val closureDate: LocalDateTime? = null,
    
    val attributes: Map<String, Any>? = null,
    
    @field:NotBlank(message = "Updated by cannot be blank")
    @field:Size(min = 1, max = 100, message = "Updated by must be between 1 and 100 characters")
    val updatedBy: String
)

/**
 * Response DTO for Account
 */
data class AccountResponse(
    val id: String,
    val customerId: String,
    val productId: String,
    val productCode: String,
    val productCategory: String,
    val accountNumber: String?,
    val parentAccountId: String?,
    val accountRefKey: String?,
    val accountCategoryId: Int,
    val statusId: Int,
    val interestType: String,
    val openedDate: LocalDateTime?,
    val closureDate: LocalDateTime?,
    val attributes: Map<String, Any>?,
    val createdAt: LocalDateTime,
    val createdBy: String,
    val updatedAt: LocalDateTime?,
    val updatedBy: String?
)

/**
 * Summary DTO for Account (used in lists)
 */
data class AccountSummary(
    val id: String,
    val customerId: String,
    val productCode: String,
    val accountNumber: String?,
    val statusId: Int
)