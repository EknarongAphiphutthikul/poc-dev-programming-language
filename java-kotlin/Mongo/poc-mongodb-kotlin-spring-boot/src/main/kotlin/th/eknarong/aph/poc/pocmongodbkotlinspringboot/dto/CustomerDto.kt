package th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * Request DTO for creating a new Customer
 */
data class CreateCustomerRequest(
    @field:NotBlank(message = "CIF ID cannot be blank")
    @field:Size(min = 1, max = 50, message = "CIF ID must be between 1 and 50 characters")
    val cifId: String,
    
    @field:Size(max = 100, message = "Reference key must not exceed 100 characters")
    val refKey: String? = null,
    
    @field:NotNull(message = "Customer type ID cannot be null")
    val customerTypeId: Int,
    
    @field:NotBlank(message = "Created by cannot be blank")
    @field:Size(min = 1, max = 100, message = "Created by must be between 1 and 100 characters")
    val createdBy: String
)

/**
 * Request DTO for updating an existing Customer
 */
data class UpdateCustomerRequest(
    @field:NotBlank(message = "CIF ID cannot be blank")
    @field:Size(min = 1, max = 50, message = "CIF ID must be between 1 and 50 characters")
    val cifId: String,
    
    @field:Size(max = 100, message = "Reference key must not exceed 100 characters")
    val refKey: String? = null,
    
    @field:NotNull(message = "Customer type ID cannot be null")
    val customerTypeId: Int,
    
    @field:NotBlank(message = "Updated by cannot be blank")
    @field:Size(min = 1, max = 100, message = "Updated by must be between 1 and 100 characters")
    val updatedBy: String
)

/**
 * Response DTO for Customer
 */
data class CustomerResponse(
    val id: String,
    val cifId: String,
    val refKey: String?,
    val customerTypeId: Int,
    val createdAt: LocalDateTime,
    val createdBy: String,
    val updatedAt: LocalDateTime?,
    val updatedBy: String?
)

/**
 * Summary DTO for Customer (used in lists)
 */
data class CustomerSummary(
    val id: String,
    val cifId: String,
    val customerTypeId: Int
)