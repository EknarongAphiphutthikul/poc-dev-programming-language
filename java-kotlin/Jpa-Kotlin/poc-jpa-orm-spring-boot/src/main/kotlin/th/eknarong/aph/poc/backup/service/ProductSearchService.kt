package th.eknarong.aph.poc.backup.service

import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*
import org.springframework.stereotype.Service
import th.eknarong.aph.poc.pocjpaormspringboot.entity.Product
import th.eknarong.aph.poc.pocjpaormspringboot.entity.User
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class ProductSearchService(
    private val entityManager: EntityManager
) {
    
    fun searchProducts(
        name: String? = null,
        minPrice: BigDecimal? = null,
        maxPrice: BigDecimal? = null,
        minStock: Int? = null,
        orderByPopularity: Boolean = false
    ): List<Product> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Product::class.java)
        val root = criteriaQuery.from(Product::class.java)
        
        val predicates = mutableListOf<Predicate>()
        
        name?.let { n ->
            predicates.add(
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%${n.lowercase()}%"
                )
            )
        }
        
        minPrice?.let { mp ->
            predicates.add(
                criteriaBuilder.greaterThanOrEqualTo(
                    root.get<BigDecimal>("price"),
                    mp
                )
            )
        }
        
        maxPrice?.let { maxP ->
            predicates.add(
                criteriaBuilder.lessThanOrEqualTo(
                    root.get<BigDecimal>("price"),
                    maxP
                )
            )
        }
        
        minStock?.let { ms ->
            predicates.add(
                criteriaBuilder.greaterThanOrEqualTo(
                    root.get<Int>("stockQuantity"),
                    ms
                )
            )
        }
        
        if (predicates.isNotEmpty()) {
            criteriaQuery.where(*predicates.toTypedArray())
        }
        
        if (orderByPopularity) {
            val orderJoin = root.join<Product, Order>("orders", JoinType.LEFT)
            criteriaQuery.groupBy(root.get<Long>("id"))
            criteriaQuery.orderBy(criteriaBuilder.desc(criteriaBuilder.count(orderJoin.get<Long>("id"))))
        } else {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get<String>("name")))
        }
        
        return entityManager.createQuery(criteriaQuery).resultList
    }
    
    fun getProductsOrderedByUser(userId: Long): List<Product> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Product::class.java)
        val root = criteriaQuery.from(Product::class.java)
        val orderJoin = root.join<Product, Order>("orders", JoinType.INNER)
        val userJoin = orderJoin.join<Order, User>("user", JoinType.INNER)
        
        criteriaQuery.where(
            criteriaBuilder.equal(userJoin.get<Long>("id"), userId)
        )
        
        criteriaQuery.distinct(true)
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get<String>("name")))
        
        return entityManager.createQuery(criteriaQuery).resultList
    }
    
    fun getProductSalesReport(
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null
    ): List<ProductSalesStats> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(ProductSalesStats::class.java)
        val root = criteriaQuery.from(Product::class.java)
        val orderJoin = root.join<Product, Order>("orders", JoinType.LEFT)
        
        val predicates = mutableListOf<Predicate>()
        
        startDate?.let { sd ->
            predicates.add(
                criteriaBuilder.greaterThanOrEqualTo(
                    orderJoin.get<LocalDateTime>("createdAt"),
                    sd
                )
            )
        }
        
        endDate?.let { ed ->
            predicates.add(
                criteriaBuilder.lessThanOrEqualTo(
                    orderJoin.get<LocalDateTime>("createdAt"),
                    ed
                )
            )
        }
        
        if (predicates.isNotEmpty()) {
            criteriaQuery.where(*predicates.toTypedArray())
        }
        
        criteriaQuery.multiselect(
            root.get<Long>("id"),
            root.get<String>("name"),
            root.get<BigDecimal>("price"),
            root.get<Int>("stockQuantity"),
            criteriaBuilder.count(orderJoin.get<Long>("id")),
            criteriaBuilder.coalesce(
                criteriaBuilder.sum(
                    criteriaBuilder.prod(
                        root.get<BigDecimal>("price"),
                        criteriaBuilder.count(orderJoin.get<Long>("id")).`as`(BigDecimal::class.java)
                    )
                ),
                BigDecimal.ZERO
            )
        )
        
        criteriaQuery.groupBy(
            root.get<Long>("id"),
            root.get<String>("name"),
            root.get<BigDecimal>("price"),
            root.get<Int>("stockQuantity")
        )
        
        criteriaQuery.orderBy(criteriaBuilder.desc(criteriaBuilder.count(orderJoin.get<Long>("id"))))
        
        return entityManager.createQuery(criteriaQuery).resultList
    }
    
    fun findProductsNeedingRestock(threshold: Int = 10): List<Product> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Product::class.java)
        val root = criteriaQuery.from(Product::class.java)
        
        criteriaQuery.where(
            criteriaBuilder.lessThanOrEqualTo(
                root.get<Int>("stockQuantity"),
                threshold
            )
        )
        
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get<Int>("stockQuantity")))
        
        return entityManager.createQuery(criteriaQuery).resultList
    }
}

data class ProductSalesStats(
    val productId: Long,
    val name: String,
    val price: BigDecimal,
    val stockQuantity: Int,
    val orderCount: Long,
    val totalRevenue: BigDecimal
)