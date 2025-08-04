package th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.Customer
import java.util.*

@Repository
interface CustomerRepository : MongoRepository<Customer, String> {
    fun findByCifId(cifId: String): Optional<Customer>
    
    fun findByCustomerTypeId(customerTypeId: Int): List<Customer>
    
    fun findByCifIdAndCustomerTypeId(cifId: String, customerTypeId: Int): Optional<Customer>
    
    @Query("{'cifId': {\$regex: ?0, \$options: 'i'}}")
    fun findByCifIdContainingIgnoreCase(cifId: String): List<Customer>
    
    fun findByCreatedBy(createdBy: String): List<Customer>
}