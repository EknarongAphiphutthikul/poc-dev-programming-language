package th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "accounts")
data class Account(
    @Id
    val id: String,
    
    val customerId: String,
    
    val productId: String,
    
    val productCode: String,
    
    val productCategory: String,
    
    val accountNumber: String? = null,
    
    val parentAccountId: String? = null,
    
    val accountRefKey: String? = null,
    
    val accountCategoryId: Int,
    
    val statusId: Int,
    
    val interestType: String,
    
    val openedDate: LocalDateTime? = null,
    
    val closureDate: LocalDateTime? = null,
    
    val attributes: Map<String, Any>? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val createdBy: String,
    
    val updatedAt: LocalDateTime? = null,
    
    val updatedBy: String? = null
)