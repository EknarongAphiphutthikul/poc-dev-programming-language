package th.eknarong.aph.poc.backup.controller

import org.springframework.web.bind.annotation.*
import th.eknarong.aph.poc.pocjpaormspringboot.entity.User
import th.eknarong.aph.poc.pocjpaormspringboot.entity.Product
import th.eknarong.aph.poc.pocjpaormspringboot.entity.Order
import th.eknarong.aph.poc.pocjpaormspringboot.entity.OrderStatus
import th.eknarong.aph.poc.backup.repository.UserRepository
import th.eknarong.aph.poc.backup.repository.ProductRepository
import th.eknarong.aph.poc.backup.repository.OrderRepository
import java.math.BigDecimal
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/sql-logging-test")
class SqlLoggingTestController(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository
) {
    
    @PostMapping("/create-user")
    fun createUserWithSqlLogging(@RequestParam name: String, @RequestParam email: String): Map<String, Any> {
        val user = User(
            name = name,
            email = email,
            createdAt = LocalDateTime.now()
        )
        
        val savedUser = userRepository.save(user)
        
        return mapOf(
            "message" to "User created successfully",
            "user" to savedUser,
            "sqlNote" to "Check console logs for SQL statements and parameter values"
        )
    }
    
    @PostMapping("/create-product")
    fun createProductWithSqlLogging(
        @RequestParam name: String,
        @RequestParam price: BigDecimal,
        @RequestParam stockQuantity: Int
    ): Map<String, Any> {
        val product = Product(
            name = name,
            description = "Test product created via SQL logging endpoint",
            price = price,
            stockQuantity = stockQuantity,
            createdAt = LocalDateTime.now()
        )
        
        val savedProduct = productRepository.save(product)
        
        return mapOf(
            "message" to "Product created successfully",
            "product" to savedProduct,
            "sqlNote" to "Check console logs for SQL statements and parameter values"
        )
    }
    
    @PostMapping("/create-order")
    fun createOrderWithSqlLogging(
        @RequestParam userId: Long,
        @RequestParam productId: Long,
        @RequestParam totalAmount: BigDecimal
    ): Map<String, Any> {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }
        
        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Product not found") }
        
        val order = Order(
            orderNumber = "TEST-${System.currentTimeMillis()}",
            totalAmount = totalAmount,
            status = OrderStatus.PENDING,
            user = user,
            products = listOf(product),
            createdAt = LocalDateTime.now()
        )
        
        val savedOrder = orderRepository.save(order)
        
        return mapOf(
            "message" to "Order created successfully",
            "order" to savedOrder,
            "sqlNote" to "Check console logs for SQL statements and parameter values"
        )
    }
    
    @GetMapping("/find-user-by-email")
    fun findUserByEmailWithSqlLogging(@RequestParam email: String): Map<String, Any?> {
        val user = userRepository.findByEmail(email)
        
        return mapOf(
            "message" to "User search completed",
            "user" to user,
            "sqlNote" to "Check console logs for JPQL execution and parameter binding"
        )
    }
    
    @GetMapping("/find-users-by-name")
    fun findUsersByNameWithSqlLogging(@RequestParam name: String): Map<String, Any> {
        val users = userRepository.findByNameContaining(name)
        
        return mapOf(
            "message" to "Users search completed",
            "users" to users,
            "count" to users.size,
            "sqlNote" to "Check console logs for JPQL execution with LIKE parameter"
        )
    }
    
    @GetMapping("/find-products-by-price-range")
    fun findProductsByPriceRangeWithSqlLogging(
        @RequestParam minPrice: BigDecimal,
        @RequestParam maxPrice: BigDecimal
    ): Map<String, Any> {
        val products = productRepository.findByPriceBetween(minPrice, maxPrice)
        
        return mapOf(
            "message" to "Products search completed",
            "products" to products,
            "count" to products.size,
            "sqlNote" to "Check console logs for JPQL execution with BETWEEN parameters"
        )
    }
    
    @GetMapping("/find-orders-by-user")
    fun findOrdersByUserWithSqlLogging(@RequestParam userId: Long): Map<String, Any> {
        val orders = orderRepository.findByUserId(userId)
        
        return mapOf(
            "message" to "Orders search completed",
            "orders" to orders,
            "count" to orders.size,
            "sqlNote" to "Check console logs for JPQL execution with JOIN operations"
        )
    }
    
    @PutMapping("/update-user/{id}")
    fun updateUserWithSqlLogging(
        @PathVariable id: Long,
        @RequestParam name: String,
        @RequestParam email: String
    ): Map<String, Any> {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found") }
        
        val updatedUser = user.copy(name = name, email = email)
        val savedUser = userRepository.save(updatedUser)
        
        return mapOf(
            "message" to "User updated successfully",
            "user" to savedUser,
            "sqlNote" to "Check console logs for UPDATE statement and parameter values"
        )
    }
    
    @DeleteMapping("/delete-user/{id}")
    fun deleteUserWithSqlLogging(@PathVariable id: Long): Map<String, Any> {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found") }
        
        userRepository.delete(user)
        
        return mapOf(
            "message" to "User deleted successfully",
            "deletedUserId" to id,
            "sqlNote" to "Check console logs for DELETE statement execution"
        )
    }
    
    @GetMapping("/transaction-test")
    fun transactionTest(): Map<String, Any> {
        val user = User(
            name = "Transaction Test User",
            email = "transaction@test.com",
            createdAt = LocalDateTime.now()
        )
        
        val savedUser = userRepository.save(user)
        
        val product = Product(
            name = "Transaction Test Product",
            description = "Product for testing transaction logging",
            price = BigDecimal("99.99"),
            stockQuantity = 10,
            createdAt = LocalDateTime.now()
        )
        
        val savedProduct = productRepository.save(product)
        
        return mapOf(
            "message" to "Transaction completed successfully",
            "user" to savedUser,
            "product" to savedProduct,
            "sqlNote" to "Check console logs for transaction boundaries and SQL statements"
        )
    }
}