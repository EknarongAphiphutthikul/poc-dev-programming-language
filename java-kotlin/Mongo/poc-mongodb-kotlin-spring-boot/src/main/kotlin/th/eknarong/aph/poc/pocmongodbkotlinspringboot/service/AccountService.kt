package th.eknarong.aph.poc.pocmongodbkotlinspringboot.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.Account
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.AccountRepository
import java.time.LocalDateTime

/**
 * Service class for managing Account entities
 */
@Service
class AccountService(
    private val accountRepository: AccountRepository
) {

    /**
     * Create a new account
     *
     * @param account the account to create
     * @return the created account
     */
    fun create(account: Account): Account {
        return accountRepository.save(account)
    }

    /**
     * Find account by ID
     *
     * @param id the account ID
     * @return the account
     * @throws EntityNotFoundException if account not found
     */
    fun findById(id: String): Account {
        return accountRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Account", id) }
    }

    /**
     * Find account by account number
     *
     * @param accountNumber the account number
     * @return the account
     * @throws EntityNotFoundException if account not found
     */
    fun findByAccountNumber(accountNumber: String): Account {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow { EntityNotFoundException("Account", "accountNumber: $accountNumber") }
    }

    /**
     * Find all accounts
     *
     * @return list of all accounts
     */
    fun findAll(): List<Account> {
        return accountRepository.findAll()
    }

    /**
     * Find all accounts with pagination
     *
     * @param pageable pagination information
     * @return page of accounts
     */
    fun findAll(pageable: Pageable): Page<Account> {
        return accountRepository.findAll(pageable)
    }

    /**
     * Find accounts by customer ID
     *
     * @param customerId the customer ID
     * @return list of accounts
     */
    fun findByCustomerId(customerId: String): List<Account> {
        return accountRepository.findByCustomerId(customerId)
    }

    /**
     * Find accounts by customer ID with pagination
     *
     * @param customerId the customer ID
     * @param pageable pagination information
     * @return page of accounts
     */
    fun findByCustomerId(customerId: String, pageable: Pageable): Page<Account> {
        return accountRepository.findByCustomerId(customerId, pageable)
    }

    /**
     * Find accounts by status ID
     *
     * @param statusId the status ID
     * @return list of accounts
     */
    fun findByStatusId(statusId: Int): List<Account> {
        return accountRepository.findByStatusId(statusId)
    }

    /**
     * Find accounts by account category ID
     *
     * @param accountCategoryId the account category ID
     * @return list of accounts
     */
    fun findByAccountCategoryId(accountCategoryId: Int): List<Account> {
        return accountRepository.findByAccountCategoryId(accountCategoryId)
    }

    /**
     * Find accounts by product code
     *
     * @param productCode the product code
     * @return list of accounts
     */
    fun findByProductCode(productCode: String): List<Account> {
        return accountRepository.findByProductCode(productCode)
    }

    /**
     * Find accounts by parent account ID
     *
     * @param parentAccountId the parent account ID
     * @return list of sub-accounts
     */
    fun findByParentAccountId(parentAccountId: String): List<Account> {
        return accountRepository.findByParentAccountId(parentAccountId)
    }

    /**
     * Find accounts by customer ID and status ID
     *
     * @param customerId the customer ID
     * @param statusId the status ID
     * @return list of accounts
     */
    fun findByCustomerIdAndStatusId(customerId: String, statusId: Int): List<Account> {
        return accountRepository.findByCustomerIdAndStatusId(customerId, statusId)
    }

    /**
     * Search accounts by account number (case insensitive)
     *
     * @param accountNumber the search term
     * @return list of matching accounts
     */
    fun searchByAccountNumber(accountNumber: String): List<Account> {
        return accountRepository.findByAccountNumberContainingIgnoreCase(accountNumber)
    }

    /**
     * Update an existing account
     *
     * @param id the account ID
     * @param account the updated account data
     * @param updatedBy the user updating the account
     * @return the updated account
     * @throws EntityNotFoundException if account not found
     */
    fun update(id: String, account: Account, updatedBy: String): Account {
        val existingAccount = findById(id)
        val updatedAccount = account.copy(
            id = existingAccount.id,
            createdAt = existingAccount.createdAt,
            createdBy = existingAccount.createdBy,
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
        return accountRepository.save(updatedAccount)
    }

    /**
     * Update account status
     *
     * @param id the account ID
     * @param statusId the new status ID
     * @param updatedBy the user updating the account
     * @return the updated account
     * @throws EntityNotFoundException if account not found
     */
    fun updateStatus(id: String, statusId: Int, updatedBy: String): Account {
        val existingAccount = findById(id)
        val updatedAccount = existingAccount.copy(
            statusId = statusId,
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
        return accountRepository.save(updatedAccount)
    }

    /**
     * Close account
     *
     * @param id the account ID
     * @param closureStatusId the closure status ID
     * @param updatedBy the user closing the account
     * @return the updated account
     * @throws EntityNotFoundException if account not found
     */
    fun closeAccount(id: String, closureStatusId: Int, updatedBy: String): Account {
        val existingAccount = findById(id)
        val updatedAccount = existingAccount.copy(
            statusId = closureStatusId,
            closureDate = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
        return accountRepository.save(updatedAccount)
    }

    /**
     * Delete account by ID
     *
     * @param id the account ID
     * @throws EntityNotFoundException if account not found
     */
    fun deleteById(id: String) {
        if (!accountRepository.existsById(id)) {
            throw EntityNotFoundException("Account", id)
        }
        accountRepository.deleteById(id)
    }

    /**
     * Check if account exists by ID
     *
     * @param id the account ID
     * @return true if exists, false otherwise
     */
    fun existsById(id: String): Boolean {
        return accountRepository.existsById(id)
    }

    /**
     * Check if account exists by account number
     *
     * @param accountNumber the account number
     * @return true if exists, false otherwise
     */
    fun existsByAccountNumber(accountNumber: String): Boolean {
        return accountRepository.findByAccountNumber(accountNumber).isPresent
    }

    /**
     * Get count of all accounts
     *
     * @return total count
     */
    fun count(): Long {
        return accountRepository.count()
    }

    /**
     * Get count of accounts by customer ID
     *
     * @param customerId the customer ID
     * @return count of accounts
     */
    fun countByCustomerId(customerId: String): Long {
        return accountRepository.findByCustomerId(customerId).size.toLong()
    }

    /**
     * Get count of accounts by status ID
     *
     * @param statusId the status ID
     * @return count of accounts
     */
    fun countByStatusId(statusId: Int): Long {
        return accountRepository.findByStatusId(statusId).size.toLong()
    }

    /**
     * Get count of accounts by account category ID
     *
     * @param accountCategoryId the account category ID
     * @return count of accounts
     */
    fun countByAccountCategoryId(accountCategoryId: Int): Long {
        return accountRepository.findByAccountCategoryId(accountCategoryId).size.toLong()
    }

    /**
     * Get active accounts for a customer
     *
     * @param customerId the customer ID
     * @param activeStatusId the active status ID (typically 1)
     * @return list of active accounts
     */
    fun findActiveAccountsByCustomerId(customerId: String, activeStatusId: Int = 1): List<Account> {
        return accountRepository.findByCustomerIdAndStatusId(customerId, activeStatusId)
    }
}