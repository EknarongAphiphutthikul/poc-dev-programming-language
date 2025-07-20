package th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity

import jakarta.persistence.*
import org.hibernate.Hibernate
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(nullable = false)
    var name: String? = null,
    
    @Column(length = 1000)
    var description: String? = null,
    
    @Column(nullable = false, precision = 10, scale = 2)
    var price: BigDecimal? = null,
    
    @Column(name = "stock_quantity", nullable = false)
    var stockQuantity: Int? = null,
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = null,
    
    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    var orders: MutableList<Order>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Product

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , description = $description , price = $price , stockQuantity = $stockQuantity , createdAt = $createdAt )"
    }
}