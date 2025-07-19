package th.eknarong.aph.poc.backup.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import th.eknarong.aph.poc.pocjpaormspringboot.entity.bidirectional.Order
import th.eknarong.aph.poc.pocjpaormspringboot.entity.bidirectional.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    fun findByUserId(@Param("userId") userId: Long): List<Order>
    
    @Query("SELECT o FROM Order o WHERE o.status = :status")
    fun findByStatus(@Param("status") status: OrderStatus): List<Order>
    
    @Query("SELECT o FROM Order o WHERE o.totalAmount BETWEEN :minAmount AND :maxAmount")
    fun findByTotalAmountBetween(
        @Param("minAmount") minAmount: BigDecimal,
        @Param("maxAmount") maxAmount: BigDecimal
    ): List<Order>
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.products WHERE o.id = :id")
    fun findByIdWithProducts(@Param("id") id: Long): Order?
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user WHERE o.id = :id")
    fun findByIdWithUser(@Param("id") id: Long): Order?
    
    @Query("""
        SELECT o FROM Order o 
        WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate
        ORDER BY o.createdAt DESC
    """)
    fun findOrdersBetweenDates(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Order>
    
    @Query("""
        SELECT o FROM Order o 
        JOIN o.products p 
        WHERE p.id = :productId
    """)
    fun findOrdersContainingProduct(@Param("productId") productId: Long): List<Order>
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user.id = :userId")
    fun getTotalAmountByUserId(@Param("userId") userId: Long): BigDecimal?
    
    @Query("""
        SELECT o FROM Order o 
        WHERE o.user.email = :email 
        AND o.status = :status
    """)
    fun findByUserEmailAndStatus(
        @Param("email") email: String,
        @Param("status") status: OrderStatus
    ): List<Order>
}