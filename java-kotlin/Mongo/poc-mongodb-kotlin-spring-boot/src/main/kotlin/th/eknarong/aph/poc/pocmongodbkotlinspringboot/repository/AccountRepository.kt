package th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.Account
import java.util.*

@Repository
interface AccountRepository : MongoRepository<Account, String> {
    fun findByCustomerId(customerId: String): List<Account>
    
    fun findByAccountNumber(accountNumber: String): Optional<Account>
    
    fun findByStatusId(statusId: Int): List<Account>
    
    fun findByAccountCategoryId(accountCategoryId: Int): List<Account>
    
    fun findByProductCode(productCode: String): List<Account>
    
    fun findByParentAccountId(parentAccountId: String): List<Account>
    
    fun findByCustomerIdAndStatusId(customerId: String, statusId: Int): List<Account>
    
    @Query("{'accountNumber': {\$regex: ?0, \$options: 'i'}}")
    fun findByAccountNumberContainingIgnoreCase(accountNumber: String): List<Account>
    
    fun findByCustomerId(customerId: String, pageable: Pageable): Page<Account>
}