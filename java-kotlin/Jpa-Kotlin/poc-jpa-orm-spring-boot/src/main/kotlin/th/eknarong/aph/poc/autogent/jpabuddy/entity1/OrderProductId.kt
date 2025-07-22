package th.eknarong.aph.poc.autogent.jpabuddy.entity1

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*

@Embeddable
open class OrderProductId : Serializable {
    @NotNull
    @Column(name = "order_id", nullable = false)
    open var orderId: Long? = null

    @NotNull
    @Column(name = "product_id", nullable = false)
    open var productId: Long? = null
    override fun hashCode(): Int = Objects.hash(orderId, productId)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as OrderProductId

        return orderId == other.orderId &&
                productId == other.productId
    }

    companion object {
        private const val serialVersionUID = 0L
    }
}