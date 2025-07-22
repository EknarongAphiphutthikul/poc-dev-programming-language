package th.eknarong.aph.poc.autogent.jpabuddy.entity2

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "products")
open class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    open var name: String? = null

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    open var description: String? = null

    @NotNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    open var price: BigDecimal? = null

    @NotNull
    @Column(name = "stock_quantity", nullable = false)
    open var stockQuantity: Int? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    open var createdAt: Instant? = null
}