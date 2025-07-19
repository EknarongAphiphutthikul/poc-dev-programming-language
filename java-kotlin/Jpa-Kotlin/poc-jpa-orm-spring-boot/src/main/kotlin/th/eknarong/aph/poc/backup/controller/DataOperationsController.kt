package th.eknarong.aph.poc.backup.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import th.eknarong.aph.poc.backup.dto.*
import th.eknarong.aph.poc.pocjpaormspringboot.entity.OrderStatus
import th.eknarong.aph.poc.backup.service.OrderService
import th.eknarong.aph.poc.backup.service.ProductService
import th.eknarong.aph.poc.backup.service.UserService

@RestController
@RequestMapping("/api/data")
class DataOperationsController(
    private val userService: UserService,
    private val productService: ProductService,
    private val orderService: OrderService
) {
    
    // === USER OPERATIONS ===
    
    @PostMapping("/users")
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        val user = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }
    
    @GetMapping("/users/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserResponse> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(user)
    }
    
    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<List<UserResponse>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }
    
    @PutMapping("/users/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val user = userService.updateUser(id, request)
        return ResponseEntity.ok(user)
    }
    
    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
    
    // === USER PROFILE OPERATIONS ===
    
    @PostMapping("/users/{userId}/profile")
    fun createUserProfile(
        @PathVariable userId: Long,
        @Valid @RequestBody request: CreateUserProfileRequest
    ): ResponseEntity<UserResponse> {
        val user = userService.createUserProfile(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }
    
    @PutMapping("/users/{userId}/profile")
    fun updateUserProfile(
        @PathVariable userId: Long,
        @Valid @RequestBody request: CreateUserProfileRequest
    ): ResponseEntity<UserResponse> {
        val user = userService.updateUserProfile(userId, request)
        return ResponseEntity.ok(user)
    }
    
    // === PRODUCT OPERATIONS ===
    
    @PostMapping("/products")
    fun createProduct(@Valid @RequestBody request: CreateProductRequest): ResponseEntity<ProductResponse> {
        val product = productService.createProduct(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(product)
    }
    
    @PostMapping("/products/bulk")
    fun createMultipleProducts(
        @Valid @RequestBody requests: List<CreateProductRequest>
    ): ResponseEntity<List<ProductResponse>> {
        val products = productService.createMultipleProducts(requests)
        return ResponseEntity.status(HttpStatus.CREATED).body(products)
    }
    
    @GetMapping("/products/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = productService.getProductById(id)
        return ResponseEntity.ok(product)
    }
    
    @GetMapping("/products")
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.getAllProducts()
        return ResponseEntity.ok(products)
    }
    
    @PutMapping("/products/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateProductRequest
    ): ResponseEntity<ProductResponse> {
        val product = productService.updateProduct(id, request)
        return ResponseEntity.ok(product)
    }
    
    @DeleteMapping("/products/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
    
    // === PRODUCT STOCK OPERATIONS ===
    
    @PutMapping("/products/{id}/stock")
    fun updateProductStock(
        @PathVariable id: Long,
        @RequestParam stockQuantity: Int
    ): ResponseEntity<ProductResponse> {
        val product = productService.updateProductStock(id, stockQuantity)
        return ResponseEntity.ok(product)
    }
    
    @PostMapping("/products/{id}/stock/increase")
    fun increaseProductStock(
        @PathVariable id: Long,
        @RequestParam quantity: Int
    ): ResponseEntity<ProductResponse> {
        val product = productService.increaseProductStock(id, quantity)
        return ResponseEntity.ok(product)
    }
    
    @PostMapping("/products/{id}/stock/decrease")
    fun decreaseProductStock(
        @PathVariable id: Long,
        @RequestParam quantity: Int
    ): ResponseEntity<ProductResponse> {
        val product = productService.decreaseProductStock(id, quantity)
        return ResponseEntity.ok(product)
    }
    
    // === ORDER OPERATIONS ===
    
    @PostMapping("/orders")
    fun createOrder(@Valid @RequestBody request: CreateOrderRequest): ResponseEntity<OrderResponse> {
        val order = orderService.createOrder(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(order)
    }
    
    @GetMapping("/orders/{id}")
    fun getOrderById(@PathVariable id: Long): ResponseEntity<OrderResponse> {
        val order = orderService.getOrderById(id)
        return ResponseEntity.ok(order)
    }
    
    @GetMapping("/orders")
    fun getAllOrders(): ResponseEntity<List<OrderResponse>> {
        val orders = orderService.getAllOrders()
        return ResponseEntity.ok(orders)
    }
    
    @GetMapping("/orders/user/{userId}")
    fun getOrdersByUser(@PathVariable userId: Long): ResponseEntity<List<OrderResponse>> {
        val orders = orderService.getOrdersByUser(userId)
        return ResponseEntity.ok(orders)
    }
    
    @PutMapping("/orders/{id}")
    fun updateOrder(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateOrderRequest
    ): ResponseEntity<OrderResponse> {
        val order = orderService.updateOrder(id, request)
        return ResponseEntity.ok(order)
    }
    
    @PutMapping("/orders/{id}/status")
    fun updateOrderStatus(
        @PathVariable id: Long,
        @RequestParam status: OrderStatus
    ): ResponseEntity<OrderResponse> {
        val order = orderService.updateOrderStatus(id, status)
        return ResponseEntity.ok(order)
    }
    
    @PostMapping("/orders/{id}/cancel")
    fun cancelOrder(@PathVariable id: Long): ResponseEntity<OrderResponse> {
        val order = orderService.cancelOrder(id)
        return ResponseEntity.ok(order)
    }
    
    @DeleteMapping("/orders/{id}")
    fun deleteOrder(@PathVariable id: Long): ResponseEntity<Void> {
        orderService.deleteOrder(id)
        return ResponseEntity.noContent().build()
    }
    
    // === ORDER PRODUCT OPERATIONS ===
    
    @PostMapping("/orders/{orderId}/products/{productId}")
    fun addProductToOrder(
        @PathVariable orderId: Long,
        @PathVariable productId: Long
    ): ResponseEntity<OrderResponse> {
        val order = orderService.addProductToOrder(orderId, productId)
        return ResponseEntity.ok(order)
    }
    
    @DeleteMapping("/orders/{orderId}/products/{productId}")
    fun removeProductFromOrder(
        @PathVariable orderId: Long,
        @PathVariable productId: Long
    ): ResponseEntity<OrderResponse> {
        val order = orderService.removeProductFromOrder(orderId, productId)
        return ResponseEntity.ok(order)
    }
    
    // === BATCH OPERATIONS EXAMPLE ===
    
    @PostMapping("/demo/create-sample-data")
    fun createSampleData(): ResponseEntity<Map<String, Any>> {
        // Create sample users
        val user1 = userService.createUser(CreateUserRequest("Demo User 1", "demo1@example.com"))
        val user2 = userService.createUser(CreateUserRequest("Demo User 2", "demo2@example.com"))
        
        // Create profiles for users
        userService.createUserProfile(user1.id, CreateUserProfileRequest(
            phoneNumber = "+1-555-0001",
            birthDate = null,
            bio = "Demo user profile 1",
            profilePictureUrl = null
        ))
        
        userService.createUserProfile(user2.id, CreateUserProfileRequest(
            phoneNumber = "+1-555-0002",
            birthDate = null,
            bio = "Demo user profile 2",
            profilePictureUrl = null
        ))
        
        // Create sample products
        val products = productService.createMultipleProducts(listOf(
            CreateProductRequest("Demo Product 1", "Sample product 1", 99.99.toBigDecimal(), 50),
            CreateProductRequest("Demo Product 2", "Sample product 2", 149.99.toBigDecimal(), 30),
            CreateProductRequest("Demo Product 3", "Sample product 3", 79.99.toBigDecimal(), 100)
        ))
        
        // Create sample orders
        val order1 = orderService.createOrder(CreateOrderRequest(
            userId = user1.id,
            productIds = listOf(products[0].id, products[1].id),
            totalAmount = 249.98.toBigDecimal(),
            status = OrderStatus.PENDING
        ))
        
        val order2 = orderService.createOrder(CreateOrderRequest(
            userId = user2.id,
            productIds = listOf(products[2].id),
            totalAmount = 79.99.toBigDecimal(),
            status = OrderStatus.PROCESSING
        ))
        
        return ResponseEntity.ok(mapOf(
            "message" to "Sample data created successfully",
            "users" to listOf(user1, user2),
            "products" to products,
            "orders" to listOf(order1, order2)
        ))
    }
}