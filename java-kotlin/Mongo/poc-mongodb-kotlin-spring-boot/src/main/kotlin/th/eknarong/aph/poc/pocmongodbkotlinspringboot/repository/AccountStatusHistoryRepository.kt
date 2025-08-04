package th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountStatusHistory

@Repository
interface AccountStatusHistoryRepository : MongoRepository<AccountStatusHistory, String> {
    fun findByAccountId(accountId: String): List<AccountStatusHistory>
    
    fun findByStatusId(statusId: Int): List<AccountStatusHistory>
    
    fun findByAccountIdAndStatusId(accountId: String, statusId: Int): List<AccountStatusHistory>
    
    fun findByAccountId(accountId: String, pageable: Pageable): Page<AccountStatusHistory>
    
    fun findByAccountIdOrderByCreatedAtDesc(accountId: String): List<AccountStatusHistory>
}