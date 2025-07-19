package th.eknarong.aph.poc.backup.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import th.eknarong.aph.poc.backup.dto.*
import th.eknarong.aph.poc.pocjpaormspringboot.entity.Order
import th.eknarong.aph.poc.pocjpaormspringboot.entity.OrderStatus
import th.eknarong.aph.poc.backup.repository.OrderRepository
import th.eknarong.aph.poc.backup.repository.ProductRepository
import th.eknarong.aph.poc.backup.repository.UserRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) {
    
    fun createOrder(request: CreateOrderRequest): OrderResponse {
        val user = userRepository.findById(request.userId)
            .orElseThrow { IllegalArgumentException("User with id ${request.userId} not found") }
        
        val products = productRepository.findAllById(request.productIds)
        if (products.size != request.productIds.size) {
            val foundIds = products.map { it.id }
            val missingIds = request.productIds.filter { !foundIds.contains(it) }
            throw IllegalArgumentException("Products not found: $missingIds")
        }
        
        // Check stock availability
        products.forEach { product ->
            if (product.stockQuantity <= 0) {
                throw IllegalArgumentException("Product '${product.name}' is out of stock")
            }
        }
        
        val orderNumber = generateOrderNumber()
        
        val order = Order(
            orderNumber = orderNumber,
            totalAmount = request.totalAmount,
            status = request.status,
            user = user,
            products = products,
            createdAt = LocalDateTime.now()
        )
        
        val savedOrder = orderRepository.save(order)
        
        // Update product stock
        products.forEach { product ->
            val updatedProduct = product.copy(stockQuantity = product.stockQuantity - 1)
            productRepository.save(updatedProduct)
        }
        
        return mapToOrderResponse(savedOrder)
    }
    
    fun updateOrder(id: Long, request: UpdateOrderRequest): OrderResponse {
        val order = orderRepository.findByIdWithProducts(id)
            ?: throw IllegalArgumentException("Order with id $id not found")
        
        // Handle product updates
        val updatedProducts = request.productIds?.let { productIds ->
            val products = productRepository.findAllById(productIds)
            if (products.size != productIds.size) {
                val foundIds = products.map { it.id }
                val missingIds = productIds.filter { !foundIds.contains(it) }
                throw IllegalArgumentException("Products not found: $missingIds")
            }
            products
        } ?: order.products
        
        val updatedOrder = order.copy(
            totalAmount = request.totalAmount ?: order.totalAmount,
            status = request.status ?: order.status,
            products = updatedProducts
        )
        
        val savedOrder = orderRepository.save(updatedOrder)
        return mapToOrderResponse(savedOrder)
    }
    
    fun updateOrderStatus(id: Long, status: OrderStatus): OrderResponse {
        val order = orderRepository.findByIdWithProducts(id)
            ?: throw IllegalArgumentException("Order with id $id not found")
        
        val updatedOrder = order.copy(status = status)
        val savedOrder = orderRepository.save(updatedOrder)
        return mapToOrderResponse(savedOrder)
    }
    
    fun cancelOrder(id: Long): OrderResponse {
        val order = orderRepository.findByIdWithProducts(id)
            ?: throw IllegalArgumentException("Order with id $id not found")
        
        if (order.status == OrderStatus.DELIVERED) {
            throw IllegalArgumentException("Cannot cancel delivered order")
        }
        
        if (order.status == OrderStatus.CANCELLED) {
            throw IllegalArgumentException("Order is already cancelled")
        }
        
        // Restore product stock
        order.products.forEach { product ->
            val updatedProduct = product.copy(stockQuantity = product.stockQuantity + 1)
            productRepository.save(updatedProduct)
        }
        
        val cancelledOrder = order.copy(status = OrderStatus.CANCELLED)
        val savedOrder = orderRepository.save(cancelledOrder)
        return mapToOrderResponse(savedOrder)
    }
    
    fun deleteOrder(id: Long) {
        val order = orderRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Order with id $id not found") }
        
        orderRepository.delete(order)
    }
    
    fun getOrderById(id: Long): OrderResponse {
        val order = orderRepository.findByIdWithProducts(id)
            ?: throw IllegalArgumentException("Order with id $id not found")
        
        return mapToOrderResponse(order)
    }
    
    fun getAllOrders(): List<OrderResponse> {
        return orderRepository.findAll().map { order ->
            val orderWithProducts = orderRepository.findByIdWithProducts(order.id!!)!!
            mapToOrderResponse(orderWithProducts)
        }
    }
    
    fun getOrdersByUser(userId: Long): List<OrderResponse> {
        val orders = orderRepository.findByUserId(userId)
        return orders.map { order ->
            val orderWithProducts = orderRepository.findByIdWithProducts(order.id!!)!!
            mapToOrderResponse(orderWithProducts)
        }
    }
    
    fun addProductToOrder(orderId: Long, productId: Long): OrderResponse {
        val order = orderRepository.findByIdWithProducts(orderId)
            ?: throw IllegalArgumentException("Order with id $orderId not found")
        
        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Product with id $productId not found") }
        
        if (order.products.any { it.id == productId }) {
            throw IllegalArgumentException("Product is already in the order")
        }
        
        if (product.stockQuantity <= 0) {
            throw IllegalArgumentException("Product '${product.name}' is out of stock")
        }
        
        val updatedOrder = order.copy(
            products = order.products + product,
            totalAmount = order.totalAmount + product.price
        )
        
        val savedOrder = orderRepository.save(updatedOrder)
        
        // Update product stock
        val updatedProduct = product.copy(stockQuantity = product.stockQuantity - 1)
        productRepository.save(updatedProduct)
        
        return mapToOrderResponse(savedOrder)
    }
    
    fun removeProductFromOrder(orderId: Long, productId: Long): OrderResponse {
        val order = orderRepository.findByIdWithProducts(orderId)
            ?: throw IllegalArgumentException("Order with id $orderId not found")
        
        val product = order.products.find { it.id == productId }
            ?: throw IllegalArgumentException("Product with id $productId not found in order")
        
        val updatedOrder = order.copy(
            products = order.products.filter { it.id != productId },
            totalAmount = order.totalAmount - product.price
        )
        
        val savedOrder = orderRepository.save(updatedOrder)
        
        // Restore product stock
        val updatedProduct = product.copy(stockQuantity = product.stockQuantity + 1)
        productRepository.save(updatedProduct)
        
        return mapToOrderResponse(savedOrder)
    }
    
    private fun generateOrderNumber(): String {
        val timestamp = System.currentTimeMillis()
        val random = Random().nextInt(1000)
        return "ORD-${timestamp}-${random.toString().padStart(3, '0')}"
    }
    
    private fun mapToOrderResponse(order: Order): OrderResponse {
        return OrderResponse(
            id = order.id!!,
            orderNumber = order.orderNumber,
            totalAmount = order.totalAmount,
            status = order.status,
            createdAt = order.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            user = UserSummary(
                id = order.user.id!!,
                name = order.user.name,
                email = order.user.email
            ),
            products = order.products.map { product ->
                ProductSummary(
                    id = product.id!!,
                    name = product.name,
                    price = product.price
                )
            }
        )
    }
}