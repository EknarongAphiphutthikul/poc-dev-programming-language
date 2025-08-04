package th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "account_authorities")
data class AccountAuthority(
    @Id
    val id: String? = null,
    
    val customerId: String,
    
    val accountNumber: String,
    
    val position: String,
    
    val effectiveDateFrom: LocalDateTime? = null,
    
    val effectiveDateTo: LocalDateTime? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val createdBy: String,
    
    val updatedAt: LocalDateTime? = null,
    
    val updatedBy: String? = null
)