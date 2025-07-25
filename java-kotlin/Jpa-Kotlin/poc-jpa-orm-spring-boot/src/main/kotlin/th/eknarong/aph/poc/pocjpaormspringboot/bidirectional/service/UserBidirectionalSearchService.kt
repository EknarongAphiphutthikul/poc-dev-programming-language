package th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.service

import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*
import org.springframework.stereotype.Service
import th.eknarong.aph.poc.pocjpaormspringboot.model.OrderStatus
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity.User
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity.UserProfile
import java.math.BigDecimal
import java.time.LocalDateTime

// TODO : Check the correctness because the code below is generated by using AI.

@Service
class UserBidirectionalSearchService(
    private val entityManager: EntityManager
) {
    
    fun searchUsers(
        name: String? = null,
        email: String? = null,
        hasProfile: Boolean? = null,
        createdAfter: LocalDateTime? = null,
        hasOrdersAbove: BigDecimal? = null
    ): List<User> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(User::class.java)
        val root = criteriaQuery.from(User::class.java)
        
        val predicates = mutableListOf<Predicate>()
        
        name?.let { n ->
            predicates.add(
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%${n.lowercase()}%"
                )
            )
        }
        
        email?.let { e ->
            predicates.add(
                criteriaBuilder.equal(root.get<String>("email"), e)
            )
        }
        
        hasProfile?.let { hp ->
            val profileJoin = root.join<User, UserProfile>("profile", JoinType.LEFT)
            if (hp) {
                predicates.add(criteriaBuilder.isNotNull(profileJoin.get<Any>("id")))
            } else {
                predicates.add(criteriaBuilder.isNull(profileJoin.get<Any>("id")))
            }
        }
        
        createdAfter?.let { ca ->
            predicates.add(
                criteriaBuilder.greaterThanOrEqualTo(
                    root.get<LocalDateTime>("createdAt"),
                    ca
                )
            )
        }
        
        hasOrdersAbove?.let { amount ->
            val orderSubquery = criteriaQuery.subquery(Long::class.java)
            val orderRoot = orderSubquery.from(Order::class.java)
            orderSubquery.select(orderRoot.get<User>("user").get("id"))
            orderSubquery.where(
                criteriaBuilder.and(
                    criteriaBuilder.equal(orderRoot.get<User>("user").get<Long>("id"), root.get<Long>("id")),
                    criteriaBuilder.greaterThan(orderRoot.get<BigDecimal>("totalAmount"), amount)
                )
            )
            predicates.add(criteriaBuilder.exists(orderSubquery))
        }
        
        if (predicates.isNotEmpty()) {
            criteriaQuery.where(*predicates.toTypedArray())
        }
        
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get<LocalDateTime>("createdAt")))
        
        return entityManager.createQuery(criteriaQuery).resultList
    }
    
    fun getUsersWithOrderStatistics(): List<UserOrderStats> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(UserOrderStats::class.java)
        val root = criteriaQuery.from(User::class.java)
        val orderJoin = root.join<User, Order>("orders", JoinType.LEFT)
        
        criteriaQuery.multiselect(
            root.get<Long>("id"),
            root.get<String>("name"),
            root.get<String>("email"),
            criteriaBuilder.count(orderJoin.get<Long>("id")),
            criteriaBuilder.coalesce(
                criteriaBuilder.sum(orderJoin.get<BigDecimal>("totalAmount")),
                BigDecimal.ZERO
            )
        )
        
        criteriaQuery.groupBy(root.get<Long>("id"), root.get<String>("name"), root.get<String>("email"))
        criteriaQuery.orderBy(criteriaBuilder.desc(criteriaBuilder.count(orderJoin.get<Long>("id"))))
        
        return entityManager.createQuery(criteriaQuery).resultList
    }
    
    fun findUsersWithOrdersInStatus(status: OrderStatus): List<User> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(User::class.java)
        val root = criteriaQuery.from(User::class.java)
        
        val orderSubquery = criteriaQuery.subquery(Long::class.java)
        val orderRoot = orderSubquery.from(Order::class.java)
        orderSubquery.select(orderRoot.get<User>("user").get("id"))
        orderSubquery.where(
            criteriaBuilder.and(
                criteriaBuilder.equal(orderRoot.get<User>("user").get<Long>("id"), root.get<Long>("id")),
                criteriaBuilder.equal(orderRoot.get<OrderStatus>("status"), status)
            )
        )
        
        criteriaQuery.where(criteriaBuilder.exists(orderSubquery))
        criteriaQuery.distinct(true)
        
        return entityManager.createQuery(criteriaQuery).resultList
    }
}

data class UserOrderStats(
    val userId: Long,
    val name: String,
    val email: String,
    val orderCount: Long,
    val totalSpent: BigDecimal
)