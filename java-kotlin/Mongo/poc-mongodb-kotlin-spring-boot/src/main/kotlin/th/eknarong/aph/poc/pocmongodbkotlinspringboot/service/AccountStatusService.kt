package th.eknarong.aph.poc.pocmongodbkotlinspringboot.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountStatus
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.AccountStatusRepository
import java.time.LocalDateTime

/**
 * Service class for managing AccountStatus entities
 */
@Service
class AccountStatusService(
    private val accountStatusRepository: AccountStatusRepository
) {

    /**
     * Create a new account status
     *
     * @param accountStatus the account status to create
     * @return the created account status
     */
    fun create(accountStatus: AccountStatus): AccountStatus {
        return accountStatusRepository.save(accountStatus)
    }

    /**
     * Find account status by ID
     *
     * @param id the account status ID
     * @return the account status
     * @throws EntityNotFoundException if account status not found
     */
    fun findById(id: Int): AccountStatus {
        return accountStatusRepository.findById(id)
            .orElseThrow { EntityNotFoundException("AccountStatus", id) }
    }

    /**
     * Find account status by name
     *
     * @param name the account status name
     * @return the account status
     * @throws EntityNotFoundException if account status not found
     */
    fun findByName(name: String): AccountStatus {
        return accountStatusRepository.findByName(name)
            .orElseThrow { EntityNotFoundException("AccountStatus", name) }
    }

    /**
     * Find all account statuses
     *
     * @return list of all account statuses
     */
    fun findAll(): List<AccountStatus> {
        return accountStatusRepository.findAll()
    }

    /**
     * Find all account statuses with pagination
     *
     * @param pageable pagination information
     * @return page of account statuses
     */
    fun findAll(pageable: Pageable): Page<AccountStatus> {
        return accountStatusRepository.findAll(pageable)
    }

    /**
     * Search account statuses by name (case insensitive)
     *
     * @param name the search term
     * @return list of matching account statuses
     */
    fun searchByName(name: String): List<AccountStatus> {
        return accountStatusRepository.findByNameContainingIgnoreCase(name)
    }

    /**
     * Update an existing account status
     *
     * @param id the account status ID
     * @param accountStatus the updated account status data
     * @return the updated account status
     * @throws EntityNotFoundException if account status not found
     */
    fun update(id: Int, accountStatus: AccountStatus): AccountStatus {
        val existingAccountStatus = findById(id)
        val updatedAccountStatus = accountStatus.copy(
            id = existingAccountStatus.id,
            createdAt = existingAccountStatus.createdAt,
            updatedAt = LocalDateTime.now()
        )
        return accountStatusRepository.save(updatedAccountStatus)
    }

    /**
     * Delete account status by ID
     *
     * @param id the account status ID
     * @throws EntityNotFoundException if account status not found
     */
    fun deleteById(id: Int) {
        if (!accountStatusRepository.existsById(id)) {
            throw EntityNotFoundException("AccountStatus", id)
        }
        accountStatusRepository.deleteById(id)
    }

    /**
     * Check if account status exists by ID
     *
     * @param id the account status ID
     * @return true if exists, false otherwise
     */
    fun existsById(id: Int): Boolean {
        return accountStatusRepository.existsById(id)
    }

    /**
     * Check if account status exists by name
     *
     * @param name the account status name
     * @return true if exists, false otherwise
     */
    fun existsByName(name: String): Boolean {
        return accountStatusRepository.findByName(name).isPresent
    }

    /**
     * Get count of all account statuses
     *
     * @return total count
     */
    fun count(): Long {
        return accountStatusRepository.count()
    }
}