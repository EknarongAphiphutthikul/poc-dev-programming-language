package th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(nullable = false)
    var name: String,
    
    @Column(length = 1000)
    var description: String?,
    
    @Column(nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,
    
    @Column(name = "stock_quantity", nullable = false)
    var stockQuantity: Int,
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    var orders: MutableList<Order> = mutableListOf()
)