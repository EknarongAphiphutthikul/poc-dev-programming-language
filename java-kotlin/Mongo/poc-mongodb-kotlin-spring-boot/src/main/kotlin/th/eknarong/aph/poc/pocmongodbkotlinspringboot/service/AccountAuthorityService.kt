package th.eknarong.aph.poc.pocmongodbkotlinspringboot.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountAuthority
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.AccountAuthorityRepository
import java.time.LocalDateTime

/**
 * Service class for managing AccountAuthority entities
 */
@Service
class AccountAuthorityService(
    private val accountAuthorityRepository: AccountAuthorityRepository
) {

    /**
     * Create a new account authority
     *
     * @param accountAuthority the account authority to create
     * @return the created account authority
     */
    fun create(accountAuthority: AccountAuthority): AccountAuthority {
        return accountAuthorityRepository.save(accountAuthority)
    }

    /**
     * Find account authority by ID
     *
     * @param id the account authority ID
     * @return the account authority
     * @throws EntityNotFoundException if account authority not found
     */
    fun findById(id: String): AccountAuthority {
        return accountAuthorityRepository.findById(id)
            .orElseThrow { EntityNotFoundException("AccountAuthority", id) }
    }

    /**
     * Find all account authorities
     *
     * @return list of all account authorities
     */
    fun findAll(): List<AccountAuthority> {
        return accountAuthorityRepository.findAll()
    }

    /**
     * Find all account authorities with pagination
     *
     * @param pageable pagination information
     * @return page of account authorities
     */
    fun findAll(pageable: Pageable): Page<AccountAuthority> {
        return accountAuthorityRepository.findAll(pageable)
    }

    /**
     * Find account authorities by customer ID
     *
     * @param customerId the customer ID
     * @return list of account authorities
     */
    fun findByCustomerId(customerId: String): List<AccountAuthority> {
        return accountAuthorityRepository.findByCustomerId(customerId)
    }

    /**
     * Find account authorities by account number
     *
     * @param accountNumber the account number
     * @return list of account authorities
     */
    fun findByAccountNumber(accountNumber: String): List<AccountAuthority> {
        return accountAuthorityRepository.findByAccountNumber(accountNumber)
    }

    /**
     * Find account authorities by position
     *
     * @param position the position
     * @return list of account authorities
     */
    fun findByPosition(position: String): List<AccountAuthority> {
        return accountAuthorityRepository.findByPosition(position)
    }

    /**
     * Find account authorities by customer ID and account number
     *
     * @param customerId the customer ID
     * @param accountNumber the account number
     * @return list of account authorities
     */
    fun findByCustomerIdAndAccountNumber(customerId: String, accountNumber: String): List<AccountAuthority> {
        return accountAuthorityRepository.findByCustomerIdAndAccountNumber(customerId, accountNumber)
    }

    /**
     * Find active account authorities at a specific date
     *
     * @param date the date to check (defaults to current date)
     * @return list of active account authorities
     */
    fun findActiveAuthoritiesAt(date: LocalDateTime = LocalDateTime.now()): List<AccountAuthority> {
        return accountAuthorityRepository.findActiveAuthoritiesAt(date)
    }

    /**
     * Find active account authorities for a customer at a specific date
     *
     * @param customerId the customer ID
     * @param date the date to check (defaults to current date)
     * @return list of active account authorities for the customer
     */
    fun findActiveAuthoritiesByCustomerAt(customerId: String, date: LocalDateTime = LocalDateTime.now()): List<AccountAuthority> {
        return accountAuthorityRepository.findActiveAuthoritiesByCustomerAt(customerId, date)
    }

    /**
     * Find current active authorities for a customer
     *
     * @param customerId the customer ID
     * @return list of currently active account authorities
     */
    fun findCurrentActiveAuthoritiesByCustomer(customerId: String): List<AccountAuthority> {
        return findActiveAuthoritiesByCustomerAt(customerId, LocalDateTime.now())
    }

    /**
     * Find current active authorities for an account
     *
     * @param accountNumber the account number
     * @return list of currently active account authorities
     */
    fun findCurrentActiveAuthoritiesByAccount(accountNumber: String): List<AccountAuthority> {
        val authorities = findByAccountNumber(accountNumber)
        val now = LocalDateTime.now()
        return authorities.filter { authority ->
            (authority.effectiveDateFrom == null || authority.effectiveDateFrom!!.isBefore(now) || authority.effectiveDateFrom!!.isEqual(now)) &&
            (authority.effectiveDateTo == null || authority.effectiveDateTo!!.isAfter(now) || authority.effectiveDateTo!!.isEqual(now))
        }
    }

    /**
     * Update an existing account authority
     *
     * @param id the account authority ID
     * @param accountAuthority the updated account authority data
     * @param updatedBy the user updating the authority
     * @return the updated account authority
     * @throws EntityNotFoundException if account authority not found
     */
    fun update(id: String, accountAuthority: AccountAuthority, updatedBy: String): AccountAuthority {
        val existingAuthority = findById(id)
        val updatedAuthority = accountAuthority.copy(
            id = existingAuthority.id,
            createdAt = existingAuthority.createdAt,
            createdBy = existingAuthority.createdBy,
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
        return accountAuthorityRepository.save(updatedAuthority)
    }

    /**
     * Extend account authority effective date
     *
     * @param id the account authority ID
     * @param newEffectiveDateTo the new effective date to
     * @param updatedBy the user updating the authority
     * @return the updated account authority
     * @throws EntityNotFoundException if account authority not found
     */
    fun extendAuthority(id: String, newEffectiveDateTo: LocalDateTime, updatedBy: String): AccountAuthority {
        val existingAuthority = findById(id)
        val updatedAuthority = existingAuthority.copy(
            effectiveDateTo = newEffectiveDateTo,
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
        return accountAuthorityRepository.save(updatedAuthority)
    }

    /**
     * Revoke account authority by setting effective date to current date
     *
     * @param id the account authority ID
     * @param updatedBy the user revoking the authority
     * @return the updated account authority
     * @throws EntityNotFoundException if account authority not found
     */
    fun revokeAuthority(id: String, updatedBy: String): AccountAuthority {
        val existingAuthority = findById(id)
        val updatedAuthority = existingAuthority.copy(
            effectiveDateTo = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
        return accountAuthorityRepository.save(updatedAuthority)
    }

    /**
     * Delete account authority by ID
     *
     * @param id the account authority ID
     * @throws EntityNotFoundException if account authority not found
     */
    fun deleteById(id: String) {
        if (!accountAuthorityRepository.existsById(id)) {
            throw EntityNotFoundException("AccountAuthority", id)
        }
        accountAuthorityRepository.deleteById(id)
    }

    /**
     * Delete all account authorities for a customer
     *
     * @param customerId the customer ID
     */
    fun deleteByCustomerId(customerId: String) {
        val authorities = accountAuthorityRepository.findByCustomerId(customerId)
        accountAuthorityRepository.deleteAll(authorities)
    }

    /**
     * Delete all account authorities for an account
     *
     * @param accountNumber the account number
     */
    fun deleteByAccountNumber(accountNumber: String) {
        val authorities = accountAuthorityRepository.findByAccountNumber(accountNumber)
        accountAuthorityRepository.deleteAll(authorities)
    }

    /**
     * Check if account authority exists by ID
     *
     * @param id the account authority ID
     * @return true if exists, false otherwise
     */
    fun existsById(id: String): Boolean {
        return accountAuthorityRepository.existsById(id)
    }

    /**
     * Check if customer has any authorities
     *
     * @param customerId the customer ID
     * @return true if authorities exist, false otherwise
     */
    fun existsByCustomerId(customerId: String): Boolean {
        return accountAuthorityRepository.findByCustomerId(customerId).isNotEmpty()
    }

    /**
     * Check if account has any authorities
     *
     * @param accountNumber the account number
     * @return true if authorities exist, false otherwise
     */
    fun existsByAccountNumber(accountNumber: String): Boolean {
        return accountAuthorityRepository.findByAccountNumber(accountNumber).isNotEmpty()
    }

    /**
     * Check if customer has active authority for specific account
     *
     * @param customerId the customer ID
     * @param accountNumber the account number
     * @param date the date to check (defaults to current date)
     * @return true if customer has active authority, false otherwise
     */
    fun hasActiveAuthority(customerId: String, accountNumber: String, date: LocalDateTime = LocalDateTime.now()): Boolean {
        return findByCustomerIdAndAccountNumber(customerId, accountNumber).any { authority ->
            (authority.effectiveDateFrom == null || authority.effectiveDateFrom!!.isBefore(date) || authority.effectiveDateFrom!!.isEqual(date)) &&
            (authority.effectiveDateTo == null || authority.effectiveDateTo!!.isAfter(date) || authority.effectiveDateTo!!.isEqual(date))
        }
    }

    /**
     * Get count of all account authorities
     *
     * @return total count
     */
    fun count(): Long {
        return accountAuthorityRepository.count()
    }

    /**
     * Get count of account authorities by customer ID
     *
     * @param customerId the customer ID
     * @return count of authorities
     */
    fun countByCustomerId(customerId: String): Long {
        return accountAuthorityRepository.findByCustomerId(customerId).size.toLong()
    }

    /**
     * Get count of account authorities by account number
     *
     * @param accountNumber the account number
     * @return count of authorities
     */
    fun countByAccountNumber(accountNumber: String): Long {
        return accountAuthorityRepository.findByAccountNumber(accountNumber).size.toLong()
    }

    /**
     * Get count of active authorities at a specific date
     *
     * @param date the date to check (defaults to current date)
     * @return count of active authorities
     */
    fun countActiveAuthoritiesAt(date: LocalDateTime = LocalDateTime.now()): Long {
        return findActiveAuthoritiesAt(date).size.toLong()
    }
}