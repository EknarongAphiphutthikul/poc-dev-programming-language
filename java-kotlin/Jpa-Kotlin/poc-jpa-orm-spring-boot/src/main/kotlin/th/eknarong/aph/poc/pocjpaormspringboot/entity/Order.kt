package th.eknarong.aph.poc.pocjpaormspringboot.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "order_number", unique = true, nullable = false)
    val orderNumber: String,
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    val totalAmount: BigDecimal,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: OrderStatus,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "order_products",
        joinColumns = [JoinColumn(name = "order_id")],
        inverseJoinColumns = [JoinColumn(name = "product_id")]
    )
    val products: List<Product> = emptyList()
)

enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}