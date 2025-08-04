package th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "customer_types")
data class CustomerType(
    @Id
    val id: Int,
    
    @Indexed(unique = true)
    val name: String,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val updatedAt: LocalDateTime? = null
)