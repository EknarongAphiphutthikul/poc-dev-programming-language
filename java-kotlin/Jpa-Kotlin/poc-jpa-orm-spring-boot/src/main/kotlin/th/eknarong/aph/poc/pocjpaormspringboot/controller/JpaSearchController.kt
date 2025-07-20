package th.eknarong.aph.poc.pocjpaormspringboot.controller

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity.Order
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity.OrderStatus
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity.Product
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity.User
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity.UserProfile
import th.eknarong.aph.poc.pocjpaormspringboot.service.OrderBidirectionalSearchService
import th.eknarong.aph.poc.pocjpaormspringboot.service.ProductBidirectionalSearchService
import th.eknarong.aph.poc.pocjpaormspringboot.service.UserBidirectionalSearchService
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.repository.OrderRepository
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.repository.ProductRepository
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.repository.UserProfileRepository
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.repository.UserRepository
import th.eknarong.aph.poc.pocjpaormspringboot.service.DailyOrderSummary
import th.eknarong.aph.poc.pocjpaormspringboot.service.OrderStatusCount
import th.eknarong.aph.poc.pocjpaormspringboot.service.ProductSalesStats
import th.eknarong.aph.poc.pocjpaormspringboot.service.UserOrderStats
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/jpa-examples")
class JpaSearchController(
        private val userRepository: UserRepository,
        private val orderRepository: OrderRepository,
        private val productRepository: ProductRepository,
        private val userProfileRepository: UserProfileRepository,
        private val userSearchService: UserBidirectionalSearchService,
        private val productSearchService: ProductBidirectionalSearchService,
        private val orderSearchService: OrderBidirectionalSearchService
) {
    
    // === JPQL Examples ===
    
    @GetMapping("/users/by-email")
    fun getUserByEmail(@RequestParam email: String): User? {
        return userRepository.findByEmail(email)
    }
    
    @GetMapping("/users/by-name")
    fun getUsersByName(@RequestParam name: String): List<User> {
        return userRepository.findByNameContaining(name)
    }
    
    @GetMapping("/users/{id}/with-profile")
    fun getUserWithProfile(@PathVariable id: Long): User? {
        return userRepository.findByIdWithProfile(id)
    }
    
    @GetMapping("/users/{id}/with-orders")
    fun getUserWithOrders(@PathVariable id: Long): User? {
        return userRepository.findByIdWithOrders(id)
    }
    
    @GetMapping("/users/with-orders-above")
    fun getUsersWithOrdersAbove(@RequestParam amount: BigDecimal): List<User> {
        return userRepository.findUsersWithOrdersAboveAmount(amount)
    }
    
    @GetMapping("/orders/by-user/{userId}")
    fun getOrdersByUser(@PathVariable userId: Long): List<Order> {
        return orderRepository.findByUserId(userId)
    }
    
    @GetMapping("/orders/by-status")
    fun getOrdersByStatus(@RequestParam status: OrderStatus): List<Order> {
        return orderRepository.findByStatus(status)
    }
    
    @GetMapping("/orders/{id}/with-products")
    fun getOrderWithProducts(@PathVariable id: Long): Order? {
        return orderRepository.findByIdWithProducts(id)
    }
    
    @GetMapping("/orders/containing-product/{productId}")
    fun getOrdersContainingProduct(@PathVariable productId: Long): List<Order> {
        return orderRepository.findOrdersContainingProduct(productId)
    }
    
    @GetMapping("/orders/total-amount-by-user/{userId}")
    fun getTotalAmountByUser(@PathVariable userId: Long): BigDecimal? {
        return orderRepository.getTotalAmountByUserId(userId)
    }
    
    @GetMapping("/products/by-name")
    fun getProductsByName(@RequestParam name: String): List<Product> {
        return productRepository.findByNameContaining(name)
    }
    
    @GetMapping("/products/by-price-range")
    fun getProductsByPriceRange(
        @RequestParam minPrice: BigDecimal,
        @RequestParam maxPrice: BigDecimal
    ): List<Product> {
        return productRepository.findByPriceBetween(minPrice, maxPrice)
    }
    
    @GetMapping("/products/out-of-stock")
    fun getOutOfStockProducts(): List<Product> {
        return productRepository.findOutOfStockProducts()
    }
    
    @GetMapping("/products/popular")
    fun getPopularProducts(@RequestParam minOrders: Long = 2): List<Product> {
        return productRepository.findPopularProducts(minOrders)
    }
    
    @GetMapping("/products/average-price")
    fun getAverageProductPrice(): BigDecimal? {
        return productRepository.getAveragePrice()
    }
    
    // === JPA Criteria API Examples ===
    
    @GetMapping("/users/search")
    fun searchUsers(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) hasProfile: Boolean?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
        createdAfter: LocalDateTime?,
        @RequestParam(required = false) hasOrdersAbove: BigDecimal?
    ): List<User> {
        return userSearchService.searchUsers(name, email, hasProfile, createdAfter, hasOrdersAbove)
    }
    
    @GetMapping("/users/with-order-statistics")
    fun getUsersWithOrderStatistics(): List<UserOrderStats> {
        return userSearchService.getUsersWithOrderStatistics()
    }
    
    @GetMapping("/users/with-orders-in-status")
    fun getUsersWithOrdersInStatus(@RequestParam status: OrderStatus): List<User> {
        return userSearchService.findUsersWithOrdersInStatus(status)
    }
    
    @GetMapping("/products/search")
    fun searchProducts(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false) minStock: Int?,
        @RequestParam(required = false, defaultValue = "false") orderByPopularity: Boolean
    ): List<Product> {
        return productSearchService.searchProducts(name, minPrice, maxPrice, minStock, orderByPopularity)
    }
    
    @GetMapping("/products/ordered-by-user/{userId}")
    fun getProductsOrderedByUser(@PathVariable userId: Long): List<Product> {
        return productSearchService.getProductsOrderedByUser(userId)
    }
    
    @GetMapping("/products/sales-report")
    fun getProductSalesReport(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
        startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
        endDate: LocalDateTime?
    ): List<ProductSalesStats> {
        return productSearchService.getProductSalesReport(startDate, endDate)
    }
    
    @GetMapping("/products/needing-restock")
    fun getProductsNeedingRestock(@RequestParam(defaultValue = "10") threshold: Int): List<Product> {
        return productSearchService.findProductsNeedingRestock(threshold)
    }
    
    @GetMapping("/orders/search")
    fun searchOrders(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) status: OrderStatus?,
        @RequestParam(required = false) minAmount: BigDecimal?,
        @RequestParam(required = false) maxAmount: BigDecimal?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
        startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
        endDate: LocalDateTime?,
        @RequestParam(required = false) userEmail: String?
    ): List<Order> {
        return orderSearchService.searchOrders(userId, status, minAmount, maxAmount, startDate, endDate, userEmail)
    }
    
    @GetMapping("/orders/with-multiple-products")
    fun getOrdersWithMultipleProducts(@RequestParam(defaultValue = "2") minProductCount: Int): List<Order> {
        return orderSearchService.getOrdersWithMultipleProducts(minProductCount)
    }
    
    @GetMapping("/orders/daily-summary")
    fun getDailyOrderSummary(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime
    ): List<DailyOrderSummary> {
        return orderSearchService.getDailyOrderSummary(startDate, endDate)
    }
    
    @GetMapping("/orders/status-counts")
    fun getOrderStatusCounts(): List<OrderStatusCount> {
        return orderSearchService.getOrdersByStatusCount()
    }
    
    // === Additional Repository Functions ===
    
    @GetMapping("/users/created-after")
    fun getUsersCreatedAfter(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime
    ): List<User> {
        return userRepository.findUsersCreatedAfter(startDate)
    }
    
    @GetMapping("/users/count-with-profile")
    fun countUsersWithProfile(): Long {
        return userRepository.countUsersWithProfile()
    }
    
    @GetMapping("/orders/by-amount-range")
    fun getOrdersByTotalAmountRange(
        @RequestParam minAmount: BigDecimal,
        @RequestParam maxAmount: BigDecimal
    ): List<Order> {
        return orderRepository.findByTotalAmountBetween(minAmount, maxAmount)
    }
    
    @GetMapping("/orders/{id}/with-user")
    fun getOrderWithUser(@PathVariable id: Long): Order? {
        return orderRepository.findByIdWithUser(id)
    }
    
    @GetMapping("/orders/between-dates")
    fun getOrdersBetweenDates(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime
    ): List<Order> {
        return orderRepository.findOrdersBetweenDates(startDate, endDate)
    }
    
    @GetMapping("/orders/by-user-email-and-status")
    fun getOrdersByUserEmailAndStatus(
        @RequestParam email: String,
        @RequestParam status: OrderStatus
    ): List<Order> {
        return orderRepository.findByUserEmailAndStatus(email, status)
    }
    
    @GetMapping("/products/with-stock-above")
    fun getProductsWithStockAbove(@RequestParam minStock: Int): List<Product> {
        return productRepository.findByStockQuantityGreaterThan(minStock)
    }
    
    @GetMapping("/products/in-order/{orderId}")
    fun getProductsInOrder(@PathVariable orderId: Long): List<Product> {
        return productRepository.findProductsInOrder(orderId)
    }
    
    @GetMapping("/products/count-above-price")
    fun countProductsAbovePrice(@RequestParam price: BigDecimal): Long {
        return productRepository.countProductsAbovePrice(price)
    }
    
    @GetMapping("/products/all-by-price-desc")
    fun getAllProductsByPriceDesc(): List<Product> {
        return productRepository.findAllOrderByPriceDesc()
    }
    
    @GetMapping("/orders/containing-product-criteria/{productId}")
    fun getOrdersContainingProductCriteria(@PathVariable productId: Long): List<Order> {
        return orderSearchService.getOrdersContainingProduct(productId)
    }

    @GetMapping("/products/with-user-id/{userId}")
    fun getProductsWithUserId(@PathVariable userId: Long): List<Product> {
        return productRepository.findProductsOrderedByUser(userId)
    }
    
    // === UserProfile Repository Functions ===
    
    @GetMapping("/user-profiles/by-user/{userId}")
    fun getUserProfileByUserId(@PathVariable userId: Long): UserProfile? {
        return userProfileRepository.findByUserId(userId)
    }
    
    @GetMapping("/user-profiles/by-phone")
    fun getUserProfileByPhoneNumber(@RequestParam phoneNumber: String): UserProfile? {
        return userProfileRepository.findByPhoneNumber(phoneNumber)
    }
    
    @GetMapping("/user-profiles/by-birth-date-range")
    fun getUserProfilesByBirthDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): List<UserProfile> {
        return userProfileRepository.findByBirthDateBetween(startDate, endDate)
    }
    
    @GetMapping("/user-profiles/with-bio")
    fun getUserProfilesWithBio(): List<UserProfile> {
        return userProfileRepository.findProfilesWithBio()
    }
    
    @GetMapping("/user-profiles/{id}/with-user")
    fun getUserProfileWithUser(@PathVariable id: Long): UserProfile? {
        return userProfileRepository.findByIdWithUser(id)
    }
    
    @GetMapping("/user-profiles/count-with-picture")
    fun countUserProfilesWithPicture(): Long {
        return userProfileRepository.countProfilesWithPicture()
    }
    
    // === Relationship Examples ===
    
    @GetMapping("/relationships/demo")
    fun relationshipDemo(): Map<String, Any?> {
        val user = userRepository.findByEmail("john.doe@example.com")
        val userWithProfile = userRepository.findByIdWithProfile(user?.id ?: 1)
        val userWithOrders = userRepository.findByIdWithOrders(user?.id ?: 1)
        
        return mapOf(
            "user" to user,
            "userWithProfile" to userWithProfile,
            "userWithOrders" to userWithOrders,
            "relationshipTypes" to mapOf(
                "User -> UserProfile" to "One-to-One",
                "User -> Orders" to "One-to-Many",
                "Order -> User" to "Many-to-One",
                "Order -> Products" to "Many-to-Many"
            )
        )
    }
}