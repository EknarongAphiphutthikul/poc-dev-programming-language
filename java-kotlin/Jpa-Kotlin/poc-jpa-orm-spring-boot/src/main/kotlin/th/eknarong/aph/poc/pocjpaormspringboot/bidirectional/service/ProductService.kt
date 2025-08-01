package th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.entity.Product
import th.eknarong.aph.poc.pocjpaormspringboot.bidirectional.repository.ProductRepository
import th.eknarong.aph.poc.pocjpaormspringboot.model.CreateProductRequest
import th.eknarong.aph.poc.pocjpaormspringboot.model.ProductResponse
import th.eknarong.aph.poc.pocjpaormspringboot.model.UpdateProductRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// TODO : Check the correctness because the code below is generated by using AI.

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository
) {
    
    fun createProduct(request: CreateProductRequest): ProductResponse {
        val product = Product(
                name = request.name,
                description = request.description,
                price = request.price,
                stockQuantity = request.stockQuantity,
                createdAt = LocalDateTime.now()
        )
        
        val savedProduct = productRepository.save(product)
        return mapToProductResponse(savedProduct)
    }
    
    fun updateProduct(id: Long, request: UpdateProductRequest): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Product with id $id not found") }

        product.name = request.name ?: product.name
        product.description = request.description ?: product.description
        product.price = request.price ?: product.price
        product.stockQuantity = request.stockQuantity ?: product.stockQuantity
        
        val savedProduct = productRepository.save(product)
        return mapToProductResponse(savedProduct)
    }
    
    fun updateProductStock(id: Long, newStockQuantity: Int): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Product with id $id not found") }
        
        if (newStockQuantity < 0) {
            throw IllegalArgumentException("Stock quantity cannot be negative")
        }

        product.stockQuantity = newStockQuantity
        val savedProduct = productRepository.save(product)
        return mapToProductResponse(savedProduct)
    }
    
    fun decreaseProductStock(id: Long, quantity: Int): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Product with id $id not found") }
        
        if (quantity <= 0) {
            throw IllegalArgumentException("Quantity must be positive")
        }
        
        val newStockQuantity = product.stockQuantity!! - quantity
        if (newStockQuantity < 0) {
            throw IllegalArgumentException("Insufficient stock. Available: ${product.stockQuantity}, Requested: $quantity")
        }

        product.stockQuantity = newStockQuantity
        val savedProduct = productRepository.save(product)
        return mapToProductResponse(savedProduct)
    }
    
    fun increaseProductStock(id: Long, quantity: Int): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Product with id $id not found") }
        
        if (quantity <= 0) {
            throw IllegalArgumentException("Quantity must be positive")
        }

        product.stockQuantity = product.stockQuantity!! + quantity
        val savedProduct = productRepository.save(product)
        return mapToProductResponse(savedProduct)
    }
    
    fun deleteProduct(id: Long) {
        val product = productRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Product with id $id not found") }
        
        productRepository.delete(product)
    }
    
    fun getProductById(id: Long): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Product with id $id not found") }
        
        return mapToProductResponse(product)
    }
    
    fun getAllProducts(): List<ProductResponse> {
        return productRepository.findAll().map { mapToProductResponse(it) }
    }
    
    fun createMultipleProducts(requests: List<CreateProductRequest>): List<ProductResponse> {
        val products = requests.map { request ->
            Product(
                name = request.name,
                description = request.description,
                price = request.price,
                stockQuantity = request.stockQuantity,
                createdAt = LocalDateTime.now()
            )
        }
        
        val savedProducts = productRepository.saveAll(products)
        return savedProducts.map { mapToProductResponse(it) }
    }
    
    private fun mapToProductResponse(product: Product): ProductResponse {
        return ProductResponse(
            id = product.id!!,
            name = product.name!!,
            description = product.description!!,
            price = product.price!!,
            stockQuantity = product.stockQuantity!!,
            createdAt = product.createdAt!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }
}