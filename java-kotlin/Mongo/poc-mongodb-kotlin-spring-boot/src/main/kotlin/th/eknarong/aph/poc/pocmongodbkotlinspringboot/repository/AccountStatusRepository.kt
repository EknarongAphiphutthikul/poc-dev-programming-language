package th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountStatus
import java.util.*

@Repository
interface AccountStatusRepository : MongoRepository<AccountStatus, Int> {
    fun findByName(name: String): Optional<AccountStatus>
    
    @Query("{'name': {\$regex: ?0, \$options: 'i'}}")
    fun findByNameContainingIgnoreCase(name: String): List<AccountStatus>
}