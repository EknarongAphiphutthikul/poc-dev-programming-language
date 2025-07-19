package th.eknarong.aph.poc.backup.service

import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*
import org.springframework.stereotype.Service
import th.eknarong.aph.poc.backup.entity.Order
import th.eknarong.aph.poc.backup.entity.OrderStatus
import th.eknarong.aph.poc.backup.entity.Product
import th.eknarong.aph.poc.backup.entity.User
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class OrderSearchService(
    private val entityManager: EntityManager
) {
    
    fun searchOrders(
        userId: Long? = null,
        status: OrderStatus? = null,
        minAmount: BigDecimal? = null,
        maxAmount: BigDecimal? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        userEmail: String? = null
    ): List<Order> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Order::class.java)
        val root = criteriaQuery.from(Order::class.java)
        
        val predicates = mutableListOf<Predicate>()
        
        userId?.let { uid ->
            predicates.add(
                criteriaBuilder.equal(root.get<User>("user").get<Long>("id"), uid)
            )
        }
        
        status?.let { s ->
            predicates.add(
                criteriaBuilder.equal(root.get<OrderStatus>("status"), s)
            )
        }
        
        minAmount?.let { min ->
            predicates.add(
                criteriaBuilder.greaterThanOrEqualTo(
                    root.get<BigDecimal>("totalAmount"),
                    min
                )
            )
        }
        
        maxAmount?.let { max ->
            predicates.add(
                criteriaBuilder.lessThanOrEqualTo(
                    root.get<BigDecimal>("totalAmount"),
                    max
                )
            )
        }
        
        startDate?.let { sd ->
            predicates.add(
                criteriaBuilder.greaterThanOrEqualTo(
                    root.get<LocalDateTime>("createdAt"),
                    sd
                )
            )
        }
        
        endDate?.let { ed ->
            predicates.add(
                criteriaBuilder.lessThanOrEqualTo(
                    root.get<LocalDateTime>("createdAt"),
                    ed
                )
            )
        }
        
        userEmail?.let { email ->
            val userJoin = root.join<Order, User>("user", JoinType.INNER)
            predicates.add(
                criteriaBuilder.equal(userJoin.get<String>("email"), email)
            )
        }
        
        if (predicates.isNotEmpty()) {
            criteriaQuery.where(*predicates.toTypedArray())
        }
        
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get<LocalDateTime>("createdAt")))
        
        return entityManager.createQuery(criteriaQuery).resultList
    }
    
    fun getOrdersContainingProduct(productId: Long): List<Order> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Order::class.java)
        val root = criteriaQuery.from(Order::class.java)
        val productJoin = root.join<Order, Product>("products", JoinType.INNER)
        
        criteriaQuery.where(
            criteriaBuilder.equal(productJoin.get<Long>("id"), productId)
        )
        
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get<LocalDateTime>("createdAt")))
        
        return entityManager.createQuery(criteriaQuery).resultList
    }
    
    fun getOrdersWithMultipleProducts(minProductCount: Int = 2): List<Order> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Order::class.java)
        val root = criteriaQuery.from(Order::class.java)
        val productJoin = root.join<Order, Product>("products", JoinType.INNER)
        
        criteriaQuery.groupBy(root.get<Long>("id"))
        criteriaQuery.having(
            criteriaBuilder.greaterThanOrEqualTo(
                criteriaBuilder.count(productJoin.get<Long>("id")),
                minProductCount.toLong()
            )
        )
        
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get<LocalDateTime>("createdAt")))
        
        return entityManager.createQuery(criteriaQuery).resultList
    }
    
    fun getDailyOrderSummary(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<DailyOrderSummary> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(DailyOrderSummary::class.java)
        val root = criteriaQuery.from(Order::class.java)
        
        val dateFunction = criteriaBuilder.function(
            "DATE",
            java.sql.Date::class.java,
            root.get<LocalDateTime>("createdAt")
        )
        
        criteriaQuery.multiselect(
            dateFunction,
            criteriaBuilder.count(root.get<Long>("id")),
            criteriaBuilder.sum(root.get<BigDecimal>("totalAmount")),
            criteriaBuilder.avg(root.get<BigDecimal>("totalAmount"))
        )
        
        criteriaQuery.where(
            criteriaBuilder.between(
                root.get<LocalDateTime>("createdAt"),
                startDate,
                endDate
            )
        )
        
        criteriaQuery.groupBy(dateFunction)
        criteriaQuery.orderBy(criteriaBuilder.asc(dateFunction))
        
        return entityManager.createQuery(criteriaQuery).resultList
    }
    
    fun getOrdersByStatusCount(): List<OrderStatusCount> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(OrderStatusCount::class.java)
        val root = criteriaQuery.from(Order::class.java)
        
        criteriaQuery.multiselect(
            root.get<OrderStatus>("status"),
            criteriaBuilder.count(root.get<Long>("id"))
        )
        
        criteriaQuery.groupBy(root.get<OrderStatus>("status"))
        criteriaQuery.orderBy(criteriaBuilder.desc(criteriaBuilder.count(root.get<Long>("id"))))
        
        return entityManager.createQuery(criteriaQuery).resultList
    }
}

data class DailyOrderSummary(
    val date: java.sql.Date,
    val orderCount: Long,
    val totalAmount: BigDecimal,
    val averageAmount: Double
)

data class OrderStatusCount(
    val status: OrderStatus,
    val count: Long
)