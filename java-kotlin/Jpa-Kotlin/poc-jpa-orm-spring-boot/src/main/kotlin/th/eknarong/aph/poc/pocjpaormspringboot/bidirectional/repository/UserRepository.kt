package th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity.User
import java.time.LocalDateTime

@Repository
interface UserRepository : JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    fun findByEmail(@Param("email") email: String): User?
    
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name%")
    fun findByNameContaining(@Param("name") name: String): List<User>
    
    @Query("SELECT u FROM User u WHERE u.createdAt >= :startDate")
    fun findUsersCreatedAfter(@Param("startDate") startDate: LocalDateTime): List<User>
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id = :id")
    fun findByIdWithProfile(@Param("id") id: Long): User?
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
    fun findByIdWithOrders(@Param("id") id: Long): User?
    
    @Query("""
        SELECT u FROM User u 
        WHERE u.id IN (
            SELECT o.user.id FROM Order o 
            WHERE o.totalAmount > :minAmount
        )
    """)
    fun findUsersWithOrdersAboveAmount(@Param("minAmount") minAmount: java.math.BigDecimal): List<User>
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.profile IS NOT NULL")
    fun countUsersWithProfile(): Long
}