package th.eknarong.aph.poc.pocmongodbkotlinspringboot.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.Customer
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.CustomerRepository
import java.time.LocalDateTime

/**
 * Service class for managing Customer entities
 */
@Service
class CustomerService(
    private val customerRepository: CustomerRepository
) {

    /**
     * Create a new customer
     *
     * @param customer the customer to create
     * @return the created customer
     */
    fun create(customer: Customer): Customer {
        return customerRepository.save(customer)
    }

    /**
     * Find customer by ID
     *
     * @param id the customer ID
     * @return the customer
     * @throws EntityNotFoundException if customer not found
     */
    fun findById(id: String): Customer {
        return customerRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Customer", id) }
    }

    /**
     * Find customer by CIF ID
     *
     * @param cifId the customer CIF ID
     * @return the customer
     * @throws EntityNotFoundException if customer not found
     */
    fun findByCifId(cifId: String): Customer {
        return customerRepository.findByCifId(cifId)
            .orElseThrow { EntityNotFoundException("Customer", "cifId: $cifId") }
    }

    /**
     * Find customer by CIF ID and customer type ID
     *
     * @param cifId the customer CIF ID
     * @param customerTypeId the customer type ID
     * @return the customer
     * @throws EntityNotFoundException if customer not found
     */
    fun findByCifIdAndCustomerTypeId(cifId: String, customerTypeId: Int): Customer {
        return customerRepository.findByCifIdAndCustomerTypeId(cifId, customerTypeId)
            .orElseThrow { EntityNotFoundException("Customer", "cifId: $cifId, customerTypeId: $customerTypeId") }
    }

    /**
     * Find all customers
     *
     * @return list of all customers
     */
    fun findAll(): List<Customer> {
        return customerRepository.findAll()
    }

    /**
     * Find all customers with pagination
     *
     * @param pageable pagination information
     * @return page of customers
     */
    fun findAll(pageable: Pageable): Page<Customer> {
        return customerRepository.findAll(pageable)
    }

    /**
     * Find customers by customer type ID
     *
     * @param customerTypeId the customer type ID
     * @return list of customers
     */
    fun findByCustomerTypeId(customerTypeId: Int): List<Customer> {
        return customerRepository.findByCustomerTypeId(customerTypeId)
    }

    /**
     * Find customers by created by user
     *
     * @param createdBy the user who created the customers
     * @return list of customers
     */
    fun findByCreatedBy(createdBy: String): List<Customer> {
        return customerRepository.findByCreatedBy(createdBy)
    }

    /**
     * Search customers by CIF ID (case insensitive)
     *
     * @param cifId the search term
     * @return list of matching customers
     */
    fun searchByCifId(cifId: String): List<Customer> {
        return customerRepository.findByCifIdContainingIgnoreCase(cifId)
    }

    /**
     * Update an existing customer
     *
     * @param id the customer ID
     * @param customer the updated customer data
     * @param updatedBy the user updating the customer
     * @return the updated customer
     * @throws EntityNotFoundException if customer not found
     */
    fun update(id: String, customer: Customer, updatedBy: String): Customer {
        val existingCustomer = findById(id)
        val updatedCustomer = customer.copy(
            id = existingCustomer.id,
            createdAt = existingCustomer.createdAt,
            createdBy = existingCustomer.createdBy,
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
        return customerRepository.save(updatedCustomer)
    }

    /**
     * Delete customer by ID
     *
     * @param id the customer ID
     * @throws EntityNotFoundException if customer not found
     */
    fun deleteById(id: String) {
        if (!customerRepository.existsById(id)) {
            throw EntityNotFoundException("Customer", id)
        }
        customerRepository.deleteById(id)
    }

    /**
     * Check if customer exists by ID
     *
     * @param id the customer ID
     * @return true if exists, false otherwise
     */
    fun existsById(id: String): Boolean {
        return customerRepository.existsById(id)
    }

    /**
     * Check if customer exists by CIF ID
     *
     * @param cifId the customer CIF ID
     * @return true if exists, false otherwise
     */
    fun existsByCifId(cifId: String): Boolean {
        return customerRepository.findByCifId(cifId).isPresent
    }

    /**
     * Check if customer exists by CIF ID and customer type ID
     *
     * @param cifId the customer CIF ID
     * @param customerTypeId the customer type ID
     * @return true if exists, false otherwise
     */
    fun existsByCifIdAndCustomerTypeId(cifId: String, customerTypeId: Int): Boolean {
        return customerRepository.findByCifIdAndCustomerTypeId(cifId, customerTypeId).isPresent
    }

    /**
     * Get count of all customers
     *
     * @return total count
     */
    fun count(): Long {
        return customerRepository.count()
    }

    /**
     * Get count of customers by customer type ID
     *
     * @param customerTypeId the customer type ID
     * @return count of customers
     */
    fun countByCustomerTypeId(customerTypeId: Int): Long {
        return customerRepository.findByCustomerTypeId(customerTypeId).size.toLong()
    }
}