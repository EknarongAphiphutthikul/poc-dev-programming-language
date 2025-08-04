package th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * Request DTO for creating a new AccountAuthority
 */
data class CreateAccountAuthorityRequest(
    @field:NotBlank(message = "Customer ID cannot be blank")
    val customerId: String,
    
    @field:NotBlank(message = "Account number cannot be blank")
    @field:Size(min = 1, max = 50, message = "Account number must be between 1 and 50 characters")
    val accountNumber: String,
    
    @field:NotBlank(message = "Position cannot be blank")
    @field:Size(min = 1, max = 100, message = "Position must be between 1 and 100 characters")
    val position: String,
    
    val effectiveDateFrom: LocalDateTime? = null,
    
    val effectiveDateTo: LocalDateTime? = null,
    
    @field:NotBlank(message = "Created by cannot be blank")
    @field:Size(min = 1, max = 100, message = "Created by must be between 1 and 100 characters")
    val createdBy: String
)

/**
 * Request DTO for updating an existing AccountAuthority
 */
data class UpdateAccountAuthorityRequest(
    @field:NotBlank(message = "Customer ID cannot be blank")
    val customerId: String,
    
    @field:NotBlank(message = "Account number cannot be blank")
    @field:Size(min = 1, max = 50, message = "Account number must be between 1 and 50 characters")
    val accountNumber: String,
    
    @field:NotBlank(message = "Position cannot be blank")
    @field:Size(min = 1, max = 100, message = "Position must be between 1 and 100 characters")
    val position: String,
    
    val effectiveDateFrom: LocalDateTime? = null,
    
    val effectiveDateTo: LocalDateTime? = null,
    
    @field:NotBlank(message = "Updated by cannot be blank")
    @field:Size(min = 1, max = 100, message = "Updated by must be between 1 and 100 characters")
    val updatedBy: String
)

/**
 * Response DTO for AccountAuthority
 */
data class AccountAuthorityResponse(
    val id: String?,
    val customerId: String,
    val accountNumber: String,
    val position: String,
    val effectiveDateFrom: LocalDateTime?,
    val effectiveDateTo: LocalDateTime?,
    val createdAt: LocalDateTime,
    val createdBy: String,
    val updatedAt: LocalDateTime?,
    val updatedBy: String?
)

/**
 * Summary DTO for AccountAuthority (used in lists)
 */
data class AccountAuthoritySummary(
    val id: String?,
    val customerId: String,
    val accountNumber: String,
    val position: String
)