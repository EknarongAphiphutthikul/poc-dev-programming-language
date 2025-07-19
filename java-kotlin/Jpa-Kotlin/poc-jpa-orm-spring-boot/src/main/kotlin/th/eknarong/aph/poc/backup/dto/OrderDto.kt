package th.eknarong.aph.poc.backup.dto

import jakarta.validation.constraints.*
import th.eknarong.aph.poc.pocjpaormspringboot.entity.OrderStatus
import java.math.BigDecimal

data class CreateOrderRequest(
    @field:NotNull(message = "User ID is required")
    val userId: Long,
    
    @field:NotEmpty(message = "Product IDs list cannot be empty")
    val productIds: List<Long>,
    
    @field:NotNull(message = "Total amount is required")
    @field:DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    @field:Digits(integer = 8, fraction = 2, message = "Total amount must have at most 8 digits before decimal and 2 after")
    val totalAmount: BigDecimal,
    
    @field:NotNull(message = "Order status is required")
    val status: OrderStatus = OrderStatus.PENDING
)

data class UpdateOrderRequest(
    @field:DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    @field:Digits(integer = 8, fraction = 2, message = "Total amount must have at most 8 digits before decimal and 2 after")
    val totalAmount: BigDecimal?,
    
    val status: OrderStatus?,
    
    val productIds: List<Long>?
)

data class OrderResponse(
    val id: Long,
    val orderNumber: String,
    val totalAmount: BigDecimal,
    val status: OrderStatus,
    val createdAt: String,
    val user: UserSummary,
    val products: List<ProductSummary>
)

data class UserSummary(
    val id: Long,
    val name: String,
    val email: String
)

data class ProductSummary(
    val id: Long,
    val name: String,
    val price: BigDecimal
)