package th.eknarong.aph.poc.pocmongodbkotlinspringboot.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * Request DTO for creating a new CustomerType
 */
data class CreateCustomerTypeRequest(
    @field:NotNull(message = "ID cannot be null")
    val id: Int,
    
    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    val name: String
)

/**
 * Request DTO for updating an existing CustomerType
 */
data class UpdateCustomerTypeRequest(
    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    val name: String
)

/**
 * Response DTO for CustomerType
 */
data class CustomerTypeResponse(
    val id: Int,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)

/**
 * Summary DTO for CustomerType (used in lists)
 */
data class CustomerTypeSummary(
    val id: Int,
    val name: String
)