package th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DBRef
import java.time.LocalDateTime

@Document(collection = "customers")
@CompoundIndex(def = "{'cifId': 1, 'customerTypeId': 1}", unique = true)
data class Customer(
    @Id
    val id: String,
    
    val cifId: String,
    
    val refKey: String? = null,
    
    val customerTypeId: Int,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val createdBy: String,
    
    val updatedAt: LocalDateTime? = null,
    
    val updatedBy: String? = null
)