package th.eknarong.aph.poc.backup.dto

import jakarta.validation.constraints.*
import java.math.BigDecimal

data class CreateProductRequest(
    @field:NotBlank(message = "Product name is required")
    @field:Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    val name: String,
    
    @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String?,
    
    @field:NotNull(message = "Price is required")
    @field:DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @field:Digits(integer = 8, fraction = 2, message = "Price must have at most 8 digits before decimal and 2 after")
    val price: BigDecimal,
    
    @field:NotNull(message = "Stock quantity is required")
    @field:Min(value = 0, message = "Stock quantity must be non-negative")
    val stockQuantity: Int
)

data class UpdateProductRequest(
    @field:Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    val name: String?,
    
    @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String?,
    
    @field:DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @field:Digits(integer = 8, fraction = 2, message = "Price must have at most 8 digits before decimal and 2 after")
    val price: BigDecimal?,
    
    @field:Min(value = 0, message = "Stock quantity must be non-negative")
    val stockQuantity: Int?
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stockQuantity: Int,
    val createdAt: String
)