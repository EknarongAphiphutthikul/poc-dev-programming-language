package th.eknarong.aph.poc.pocmongodbkotlinspringboot.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountCategory
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.AccountCategoryRepository
import java.time.LocalDateTime

/**
 * Service class for managing AccountCategory entities
 */
@Service
class AccountCategoryService(
    private val accountCategoryRepository: AccountCategoryRepository
) {

    /**
     * Create a new account category
     *
     * @param accountCategory the account category to create
     * @return the created account category
     */
    fun create(accountCategory: AccountCategory): AccountCategory {
        return accountCategoryRepository.save(accountCategory)
    }

    /**
     * Find account category by ID
     *
     * @param id the account category ID
     * @return the account category
     * @throws EntityNotFoundException if account category not found
     */
    fun findById(id: Int): AccountCategory {
        return accountCategoryRepository.findById(id)
            .orElseThrow { EntityNotFoundException("AccountCategory", id) }
    }

    /**
     * Find account category by name
     *
     * @param name the account category name
     * @return the account category
     * @throws EntityNotFoundException if account category not found
     */
    fun findByName(name: String): AccountCategory {
        return accountCategoryRepository.findByName(name)
            .orElseThrow { EntityNotFoundException("AccountCategory", name) }
    }

    /**
     * Find all account categories
     *
     * @return list of all account categories
     */
    fun findAll(): List<AccountCategory> {
        return accountCategoryRepository.findAll()
    }

    /**
     * Find all account categories with pagination
     *
     * @param pageable pagination information
     * @return page of account categories
     */
    fun findAll(pageable: Pageable): Page<AccountCategory> {
        return accountCategoryRepository.findAll(pageable)
    }

    /**
     * Search account categories by name (case insensitive)
     *
     * @param name the search term
     * @return list of matching account categories
     */
    fun searchByName(name: String): List<AccountCategory> {
        return accountCategoryRepository.findByNameContainingIgnoreCase(name)
    }

    /**
     * Update an existing account category
     *
     * @param id the account category ID
     * @param accountCategory the updated account category data
     * @return the updated account category
     * @throws EntityNotFoundException if account category not found
     */
    fun update(id: Int, accountCategory: AccountCategory): AccountCategory {
        val existingAccountCategory = findById(id)
        val updatedAccountCategory = accountCategory.copy(
            id = existingAccountCategory.id,
            createdAt = existingAccountCategory.createdAt,
            updatedAt = LocalDateTime.now()
        )
        return accountCategoryRepository.save(updatedAccountCategory)
    }

    /**
     * Delete account category by ID
     *
     * @param id the account category ID
     * @throws EntityNotFoundException if account category not found
     */
    fun deleteById(id: Int) {
        if (!accountCategoryRepository.existsById(id)) {
            throw EntityNotFoundException("AccountCategory", id)
        }
        accountCategoryRepository.deleteById(id)
    }

    /**
     * Check if account category exists by ID
     *
     * @param id the account category ID
     * @return true if exists, false otherwise
     */
    fun existsById(id: Int): Boolean {
        return accountCategoryRepository.existsById(id)
    }

    /**
     * Check if account category exists by name
     *
     * @param name the account category name
     * @return true if exists, false otherwise
     */
    fun existsByName(name: String): Boolean {
        return accountCategoryRepository.findByName(name).isPresent
    }

    /**
     * Get count of all account categories
     *
     * @return total count
     */
    fun count(): Long {
        return accountCategoryRepository.count()
    }
}