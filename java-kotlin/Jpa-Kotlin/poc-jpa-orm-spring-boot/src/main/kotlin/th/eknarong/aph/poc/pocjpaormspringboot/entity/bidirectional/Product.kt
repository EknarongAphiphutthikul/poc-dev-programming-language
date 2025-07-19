package th.eknarong.aph.poc.pocjpaormspringboot.entity.bidirectional

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val name: String,
    
    @Column(length = 1000)
    val description: String?,
    
    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,
    
    @Column(name = "stock_quantity", nullable = false)
    val stockQuantity: Int,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    val orders: List<Order> = emptyList()
)