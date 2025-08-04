package th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountAuthority
import java.time.LocalDateTime

@Repository
interface AccountAuthorityRepository : MongoRepository<AccountAuthority, String> {
    fun findByCustomerId(customerId: String): List<AccountAuthority>
    
    fun findByAccountNumber(accountNumber: String): List<AccountAuthority>
    
    fun findByPosition(position: String): List<AccountAuthority>
    
    fun findByCustomerIdAndAccountNumber(customerId: String, accountNumber: String): List<AccountAuthority>
    
    @Query("{'effectiveDateFrom': {\$lte: ?0}, 'effectiveDateTo': {\$gte: ?0}}")
    fun findActiveAuthoritiesAt(date: LocalDateTime): List<AccountAuthority>
    
    @Query("{'customerId': ?0, 'effectiveDateFrom': {\$lte: ?1}, 'effectiveDateTo': {\$gte: ?1}}")
    fun findActiveAuthoritiesByCustomerAt(customerId: String, date: LocalDateTime): List<AccountAuthority>
}