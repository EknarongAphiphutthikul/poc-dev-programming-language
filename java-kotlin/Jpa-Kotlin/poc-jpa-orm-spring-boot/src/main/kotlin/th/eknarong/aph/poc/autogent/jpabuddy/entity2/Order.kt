package th.eknarong.aph.poc.autogent.jpabuddy.entity2

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "orders")
open class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Size(max = 100)
    @NotNull
    @Column(name = "order_number", nullable = false, length = 100)
    open var orderNumber: String? = null

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    open var totalAmount: BigDecimal? = null

    @NotNull
    @Lob
    @Column(name = "status", nullable = false)
    open var status: String? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    open var createdAt: Instant? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User? = null
}