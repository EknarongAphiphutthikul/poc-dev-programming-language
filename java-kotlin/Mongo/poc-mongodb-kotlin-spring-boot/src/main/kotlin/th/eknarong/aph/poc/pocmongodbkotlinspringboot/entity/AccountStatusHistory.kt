package th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "account_status_histories")
data class AccountStatusHistory(
    @Id
    val id: String? = null,
    
    val accountId: String,
    
    val statusId: Int,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val createdBy: String,
    
    val updatedAt: LocalDateTime? = null,
    
    val updatedBy: String? = null
)