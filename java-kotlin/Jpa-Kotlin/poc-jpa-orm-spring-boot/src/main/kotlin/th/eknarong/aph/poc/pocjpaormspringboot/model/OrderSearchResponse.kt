package th.eknarong.aph.poc.pocjpaormspringboot.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderSearchResponse(
    val id: Long? = null,
    val orderNumber: String? = null,
    val totalAmount: BigDecimal? = null,
    val status: OrderStatus,
    val createdAt: LocalDateTime? = null,
    val user: UserSearchResponse? = null,
    val products: MutableList<ProductSearchResponse>? = null
)