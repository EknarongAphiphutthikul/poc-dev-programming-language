package th.eknarong.aph.poc.pocjpaormspringboot.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductSearchResponse(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val price: BigDecimal? = null,
    val stockQuantity: Int? = null,
    val createdAt: LocalDateTime? = null,
    val orders: MutableList<OrderSearchResponse>? = null
)