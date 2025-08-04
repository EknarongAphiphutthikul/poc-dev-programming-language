package th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * Request DTO for creating a new AccountStatusHistory
 */
data class CreateAccountStatusHistoryRequest(
    @field:NotBlank(message = "Account ID cannot be blank")
    val accountId: String,
    
    @field:NotNull(message = "Status ID cannot be null")
    val statusId: Int,
    
    @field:NotBlank(message = "Created by cannot be blank")
    @field:Size(min = 1, max = 100, message = "Created by must be between 1 and 100 characters")
    val createdBy: String
)

/**
 * Request DTO for updating an existing AccountStatusHistory
 */
data class UpdateAccountStatusHistoryRequest(
    @field:NotBlank(message = "Account ID cannot be blank")
    val accountId: String,
    
    @field:NotNull(message = "Status ID cannot be null")
    val statusId: Int,
    
    @field:NotBlank(message = "Updated by cannot be blank")
    @field:Size(min = 1, max = 100, message = "Updated by must be between 1 and 100 characters")
    val updatedBy: String
)

/**
 * Response DTO for AccountStatusHistory
 */
data class AccountStatusHistoryResponse(
    val id: String?,
    val accountId: String,
    val statusId: Int,
    val createdAt: LocalDateTime,
    val createdBy: String,
    val updatedAt: LocalDateTime?,
    val updatedBy: String?
)

/**
 * Summary DTO for AccountStatusHistory (used in lists)
 */
data class AccountStatusHistorySummary(
    val id: String?,
    val accountId: String,
    val statusId: Int,
    val createdAt: LocalDateTime
)