package th.eknarong.aph.poc.pocmongodbkotlinspringboot.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.CustomerType
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.CustomerTypeRepository
import java.time.LocalDateTime

/**
 * Service class for managing CustomerType entities
 */
@Service
class CustomerTypeService(
    private val customerTypeRepository: CustomerTypeRepository
) {

    /**
     * Create a new customer type
     *
     * @param customerType the customer type to create
     * @return the created customer type
     */
    fun create(customerType: CustomerType): CustomerType {
        return customerTypeRepository.save(customerType)
    }

    /**
     * Find customer type by ID
     *
     * @param id the customer type ID
     * @return the customer type
     * @throws EntityNotFoundException if customer type not found
     */
    fun findById(id: Int): CustomerType {
        return customerTypeRepository.findById(id)
            .orElseThrow { EntityNotFoundException("CustomerType", id) }
    }

    /**
     * Find customer type by name
     *
     * @param name the customer type name
     * @return the customer type
     * @throws EntityNotFoundException if customer type not found
     */
    fun findByName(name: String): CustomerType {
        return customerTypeRepository.findByName(name)
            .orElseThrow { EntityNotFoundException("CustomerType", name) }
    }

    /**
     * Find all customer types
     *
     * @return list of all customer types
     */
    fun findAll(): List<CustomerType> {
        return customerTypeRepository.findAll()
    }

    /**
     * Find all customer types with pagination
     *
     * @param pageable pagination information
     * @return page of customer types
     */
    fun findAll(pageable: Pageable): Page<CustomerType> {
        return customerTypeRepository.findAll(pageable)
    }

    /**
     * Search customer types by name (case insensitive)
     *
     * @param name the search term
     * @return list of matching customer types
     */
    fun searchByName(name: String): List<CustomerType> {
        return customerTypeRepository.findByNameContainingIgnoreCase(name)
    }

    /**
     * Update an existing customer type
     *
     * @param id the customer type ID
     * @param customerType the updated customer type data
     * @return the updated customer type
     * @throws EntityNotFoundException if customer type not found
     */
    fun update(id: Int, customerType: CustomerType): CustomerType {
        val existingCustomerType = findById(id)
        val updatedCustomerType = customerType.copy(
            id = existingCustomerType.id,
            createdAt = existingCustomerType.createdAt,
            updatedAt = LocalDateTime.now()
        )
        return customerTypeRepository.save(updatedCustomerType)
    }

    /**
     * Delete customer type by ID
     *
     * @param id the customer type ID
     * @throws EntityNotFoundException if customer type not found
     */
    fun deleteById(id: Int) {
        if (!customerTypeRepository.existsById(id)) {
            throw EntityNotFoundException("CustomerType", id)
        }
        customerTypeRepository.deleteById(id)
    }

    /**
     * Check if customer type exists by ID
     *
     * @param id the customer type ID
     * @return true if exists, false otherwise
     */
    fun existsById(id: Int): Boolean {
        return customerTypeRepository.existsById(id)
    }

    /**
     * Check if customer type exists by name
     *
     * @param name the customer type name
     * @return true if exists, false otherwise
     */
    fun existsByName(name: String): Boolean {
        return customerTypeRepository.findByName(name).isPresent
    }

    /**
     * Get count of all customer types
     *
     * @return total count
     */
    fun count(): Long {
        return customerTypeRepository.count()
    }
}