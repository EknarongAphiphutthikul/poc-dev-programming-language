package th.eknarong.aph.poc.pocjpaormspringboot.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import th.eknarong.aph.poc.pocjpaormspringboot.entity.Product
import java.math.BigDecimal

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
    fun findByNameContaining(@Param("name") name: String): List<Product>
    
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    fun findByPriceBetween(
        @Param("minPrice") minPrice: BigDecimal,
        @Param("maxPrice") maxPrice: BigDecimal
    ): List<Product>
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > :minStock")
    fun findByStockQuantityGreaterThan(@Param("minStock") minStock: Int): List<Product>
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0")
    fun findOutOfStockProducts(): List<Product>
    
    @Query("""
        SELECT p FROM Product p 
        JOIN p.orders o 
        WHERE o.id = :orderId
    """)
    fun findProductsInOrder(@Param("orderId") orderId: Long): List<Product>
    
    @Query("""
        SELECT p FROM Product p 
        JOIN p.orders o 
        WHERE o.user.id = :userId
    """)
    fun findProductsOrderedByUser(@Param("userId") userId: Long): List<Product>
    
    @Query("""
        SELECT p FROM Product p 
        WHERE p.id IN (
            SELECT pr.id FROM Product pr 
            JOIN pr.orders o 
            GROUP BY pr.id 
            HAVING COUNT(o) > :minOrders
        )
    """)
    fun findPopularProducts(@Param("minOrders") minOrders: Long): List<Product>
    
    @Query("SELECT AVG(p.price) FROM Product p")
    fun getAveragePrice(): BigDecimal?
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.price > :price")
    fun countProductsAbovePrice(@Param("price") price: BigDecimal): Long
    
    @Query("SELECT p FROM Product p ORDER BY p.price DESC")
    fun findAllOrderByPriceDesc(): List<Product>
}