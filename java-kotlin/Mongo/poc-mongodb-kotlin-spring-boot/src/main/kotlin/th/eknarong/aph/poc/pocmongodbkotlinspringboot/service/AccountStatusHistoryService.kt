package th.eknarong.aph.poc.pocmongodbkotlinspringboot.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountStatusHistory
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.AccountStatusHistoryRepository
import java.time.LocalDateTime

/**
 * Service class for managing AccountStatusHistory entities
 */
@Service
class AccountStatusHistoryService(
    private val accountStatusHistoryRepository: AccountStatusHistoryRepository
) {

    /**
     * Create a new account status history record
     *
     * @param accountStatusHistory the account status history to create
     * @return the created account status history
     */
    fun create(accountStatusHistory: AccountStatusHistory): AccountStatusHistory {
        return accountStatusHistoryRepository.save(accountStatusHistory)
    }

    /**
     * Create status history entry for account status change
     *
     * @param accountId the account ID
     * @param statusId the new status ID
     * @param createdBy the user making the change
     * @return the created account status history
     */
    fun createStatusChange(accountId: String, statusId: Int, createdBy: String): AccountStatusHistory {
        val statusHistory = AccountStatusHistory(
            accountId = accountId,
            statusId = statusId,
            createdBy = createdBy
        )
        return accountStatusHistoryRepository.save(statusHistory)
    }

    /**
     * Find account status history by ID
     *
     * @param id the account status history ID
     * @return the account status history
     * @throws EntityNotFoundException if account status history not found
     */
    fun findById(id: String): AccountStatusHistory {
        return accountStatusHistoryRepository.findById(id)
            .orElseThrow { EntityNotFoundException("AccountStatusHistory", id) }
    }

    /**
     * Find all account status history records
     *
     * @return list of all account status history records
     */
    fun findAll(): List<AccountStatusHistory> {
        return accountStatusHistoryRepository.findAll()
    }

    /**
     * Find all account status history records with pagination
     *
     * @param pageable pagination information
     * @return page of account status history records
     */
    fun findAll(pageable: Pageable): Page<AccountStatusHistory> {
        return accountStatusHistoryRepository.findAll(pageable)
    }

    /**
     * Find account status history by account ID
     *
     * @param accountId the account ID
     * @return list of account status history records
     */
    fun findByAccountId(accountId: String): List<AccountStatusHistory> {
        return accountStatusHistoryRepository.findByAccountId(accountId)
    }

    /**
     * Find account status history by account ID with pagination
     *
     * @param accountId the account ID
     * @param pageable pagination information
     * @return page of account status history records
     */
    fun findByAccountId(accountId: String, pageable: Pageable): Page<AccountStatusHistory> {
        return accountStatusHistoryRepository.findByAccountId(accountId, pageable)
    }

    /**
     * Find account status history by account ID ordered by creation date descending
     *
     * @param accountId the account ID
     * @return list of account status history records ordered by creation date desc
     */
    fun findByAccountIdOrderByCreatedAtDesc(accountId: String): List<AccountStatusHistory> {
        return accountStatusHistoryRepository.findByAccountIdOrderByCreatedAtDesc(accountId)
    }

    /**
     * Find account status history by status ID
     *
     * @param statusId the status ID
     * @return list of account status history records
     */
    fun findByStatusId(statusId: Int): List<AccountStatusHistory> {
        return accountStatusHistoryRepository.findByStatusId(statusId)
    }

    /**
     * Find account status history by account ID and status ID
     *
     * @param accountId the account ID
     * @param statusId the status ID
     * @return list of account status history records
     */
    fun findByAccountIdAndStatusId(accountId: String, statusId: Int): List<AccountStatusHistory> {
        return accountStatusHistoryRepository.findByAccountIdAndStatusId(accountId, statusId)
    }

    /**
     * Get latest status history for an account
     *
     * @param accountId the account ID
     * @return the latest account status history record
     * @throws EntityNotFoundException if no history found for the account
     */
    fun findLatestByAccountId(accountId: String): AccountStatusHistory {
        val history = accountStatusHistoryRepository.findByAccountIdOrderByCreatedAtDesc(accountId)
        return history.firstOrNull() 
            ?: throw EntityNotFoundException("AccountStatusHistory", "accountId: $accountId")
    }

    /**
     * Update an existing account status history record
     *
     * @param id the account status history ID
     * @param accountStatusHistory the updated account status history data
     * @param updatedBy the user updating the record
     * @return the updated account status history
     * @throws EntityNotFoundException if account status history not found
     */
    fun update(id: String, accountStatusHistory: AccountStatusHistory, updatedBy: String): AccountStatusHistory {
        val existingRecord = findById(id)
        val updatedRecord = accountStatusHistory.copy(
            id = existingRecord.id,
            createdAt = existingRecord.createdAt,
            createdBy = existingRecord.createdBy,
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
        return accountStatusHistoryRepository.save(updatedRecord)
    }

    /**
     * Delete account status history by ID
     *
     * @param id the account status history ID
     * @throws EntityNotFoundException if account status history not found
     */
    fun deleteById(id: String) {
        if (!accountStatusHistoryRepository.existsById(id)) {
            throw EntityNotFoundException("AccountStatusHistory", id)
        }
        accountStatusHistoryRepository.deleteById(id)
    }

    /**
     * Delete all account status history records for an account
     *
     * @param accountId the account ID
     */
    fun deleteByAccountId(accountId: String) {
        val historyRecords = accountStatusHistoryRepository.findByAccountId(accountId)
        accountStatusHistoryRepository.deleteAll(historyRecords)
    }

    /**
     * Check if account status history exists by ID
     *
     * @param id the account status history ID
     * @return true if exists, false otherwise
     */
    fun existsById(id: String): Boolean {
        return accountStatusHistoryRepository.existsById(id)
    }

    /**
     * Check if account has any status history
     *
     * @param accountId the account ID
     * @return true if history exists, false otherwise
     */
    fun existsByAccountId(accountId: String): Boolean {
        return accountStatusHistoryRepository.findByAccountId(accountId).isNotEmpty()
    }

    /**
     * Get count of all account status history records
     *
     * @return total count
     */
    fun count(): Long {
        return accountStatusHistoryRepository.count()
    }

    /**
     * Get count of account status history records by account ID
     *
     * @param accountId the account ID
     * @return count of history records
     */
    fun countByAccountId(accountId: String): Long {
        return accountStatusHistoryRepository.findByAccountId(accountId).size.toLong()
    }

    /**
     * Get count of account status history records by status ID
     *
     * @param statusId the status ID
     * @return count of history records
     */
    fun countByStatusId(statusId: Int): Long {
        return accountStatusHistoryRepository.findByStatusId(statusId).size.toLong()
    }
}