package th.eknarong.aph.poc.pocmongodbkotlinspringboot.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.Customer
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.CustomerRepository
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class CustomerServiceTest {

    @Mock
    private lateinit var customerRepository: CustomerRepository

    @InjectMocks
    private lateinit var customerService: CustomerService

    private val sampleCustomer = Customer(
        id = "customer-1",
        cifId = "CIF001",
        refKey = "REF001",
        customerTypeId = 1,
        createdAt = LocalDateTime.now(),
        createdBy = "admin",
        updatedAt = null,
        updatedBy = null
    )

    private val sampleCustomerList = listOf(
        sampleCustomer,
        Customer(
            id = "customer-2",
            cifId = "CIF002",
            refKey = "REF002",
            customerTypeId = 2,
            createdAt = LocalDateTime.now(),
            createdBy = "admin",
            updatedAt = null,
            updatedBy = null
        )
    )

    @Test
    fun `create should save and return customer`() {
        // Given
        whenever(customerRepository.save(any())).thenReturn(sampleCustomer)

        // When
        val result = customerService.create(sampleCustomer)

        // Then
        assertEquals(sampleCustomer, result)
        verify(customerRepository).save(sampleCustomer)
    }

    @Test
    fun `findById should return customer when exists`() {
        // Given
        whenever(customerRepository.findById("customer-1")).thenReturn(Optional.of(sampleCustomer))

        // When
        val result = customerService.findById("customer-1")

        // Then
        assertEquals(sampleCustomer, result)
        verify(customerRepository).findById("customer-1")
    }

    @Test
    fun `findById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(customerRepository.findById("customer-1")).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            customerService.findById("customer-1")
        }
        assertEquals("Customer with identifier 'customer-1' not found", exception.message)
        verify(customerRepository).findById("customer-1")
    }

    @Test
    fun `findByCifId should return customer when exists`() {
        // Given
        whenever(customerRepository.findByCifId("CIF001")).thenReturn(Optional.of(sampleCustomer))

        // When
        val result = customerService.findByCifId("CIF001")

        // Then
        assertEquals(sampleCustomer, result)
        verify(customerRepository).findByCifId("CIF001")
    }

    @Test
    fun `findByCifId should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(customerRepository.findByCifId("CIF999")).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            customerService.findByCifId("CIF999")
        }
        assertEquals("Customer with identifier 'cifId: CIF999' not found", exception.message)
        verify(customerRepository).findByCifId("CIF999")
    }

    @Test
    fun `findByCifIdAndCustomerTypeId should return customer when exists`() {
        // Given
        whenever(customerRepository.findByCifIdAndCustomerTypeId("CIF001", 1))
            .thenReturn(Optional.of(sampleCustomer))

        // When
        val result = customerService.findByCifIdAndCustomerTypeId("CIF001", 1)

        // Then
        assertEquals(sampleCustomer, result)
        verify(customerRepository).findByCifIdAndCustomerTypeId("CIF001", 1)
    }

    @Test
    fun `findByCifIdAndCustomerTypeId should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(customerRepository.findByCifIdAndCustomerTypeId("CIF999", 1))
            .thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            customerService.findByCifIdAndCustomerTypeId("CIF999", 1)
        }
        assertEquals("Customer with identifier 'cifId: CIF999, customerTypeId: 1' not found", exception.message)
        verify(customerRepository).findByCifIdAndCustomerTypeId("CIF999", 1)
    }

    @Test
    fun `findAll should return all customers`() {
        // Given
        whenever(customerRepository.findAll()).thenReturn(sampleCustomerList)

        // When
        val result = customerService.findAll()

        // Then
        assertEquals(sampleCustomerList, result)
        verify(customerRepository).findAll()
    }

    @Test
    fun `findAll with pageable should return paged customers`() {
        // Given
        val pageable: Pageable = PageRequest.of(0, 10)
        val page = PageImpl(sampleCustomerList, pageable, sampleCustomerList.size.toLong())
        whenever(customerRepository.findAll(pageable)).thenReturn(page)

        // When
        val result = customerService.findAll(pageable)

        // Then
        assertEquals(page, result)
        assertEquals(sampleCustomerList, result.content)
        verify(customerRepository).findAll(pageable)
    }

    @Test
    fun `findByCustomerTypeId should return customers with specified type`() {
        // Given
        val customerTypeId = 1
        val customersWithType = listOf(sampleCustomer)
        whenever(customerRepository.findByCustomerTypeId(customerTypeId)).thenReturn(customersWithType)

        // When
        val result = customerService.findByCustomerTypeId(customerTypeId)

        // Then
        assertEquals(customersWithType, result)
        verify(customerRepository).findByCustomerTypeId(customerTypeId)
    }

    @Test
    fun `findByCreatedBy should return customers created by user`() {
        // Given
        val createdBy = "admin"
        whenever(customerRepository.findByCreatedBy(createdBy)).thenReturn(sampleCustomerList)

        // When
        val result = customerService.findByCreatedBy(createdBy)

        // Then
        assertEquals(sampleCustomerList, result)
        verify(customerRepository).findByCreatedBy(createdBy)
    }

    @Test
    fun `searchByCifId should return matching customers`() {
        // Given
        val searchTerm = "CIF"
        whenever(customerRepository.findByCifIdContainingIgnoreCase(searchTerm)).thenReturn(sampleCustomerList)

        // When
        val result = customerService.searchByCifId(searchTerm)

        // Then
        assertEquals(sampleCustomerList, result)
        verify(customerRepository).findByCifIdContainingIgnoreCase(searchTerm)
    }

    @Test
    fun `update should update and return customer when exists`() {
        // Given
        val updatedCustomer = sampleCustomer.copy(cifId = "CIF001-UPDATED")
        whenever(customerRepository.findById("customer-1")).thenReturn(Optional.of(sampleCustomer))
        whenever(customerRepository.save(any())).thenReturn(updatedCustomer)

        // When
        val result = customerService.update("customer-1", updatedCustomer, "updater")

        // Then
        assertEquals(updatedCustomer, result)
        verify(customerRepository).findById("customer-1")
        verify(customerRepository).save(any())
    }

    @Test
    fun `update should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(customerRepository.findById("customer-1")).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            customerService.update("customer-1", sampleCustomer, "updater")
        }
        assertEquals("Customer with identifier 'customer-1' not found", exception.message)
        verify(customerRepository).findById("customer-1")
        verify(customerRepository, never()).save(any())
    }

    @Test
    fun `update should preserve original metadata and update fields`() {
        // Given
        val originalCustomer = sampleCustomer
        val updateData = sampleCustomer.copy(cifId = "CIF001-UPDATED")
        whenever(customerRepository.findById("customer-1")).thenReturn(Optional.of(originalCustomer))
        whenever(customerRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument(0) as Customer
        }

        // When
        val result = customerService.update("customer-1", updateData, "updater")

        // Then
        assertEquals(originalCustomer.id, result.id)
        assertEquals(originalCustomer.createdAt, result.createdAt)
        assertEquals(originalCustomer.createdBy, result.createdBy)
        assertEquals("updater", result.updatedBy)
        assertEquals("CIF001-UPDATED", result.cifId)
        verify(customerRepository).save(any())
    }

    @Test
    fun `deleteById should delete customer when exists`() {
        // Given
        whenever(customerRepository.existsById("customer-1")).thenReturn(true)

        // When
        customerService.deleteById("customer-1")

        // Then
        verify(customerRepository).existsById("customer-1")
        verify(customerRepository).deleteById("customer-1")
    }

    @Test
    fun `deleteById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(customerRepository.existsById("customer-1")).thenReturn(false)

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            customerService.deleteById("customer-1")
        }
        assertEquals("Customer with identifier 'customer-1' not found", exception.message)
        verify(customerRepository).existsById("customer-1")
        verify(customerRepository, never()).deleteById("customer-1")
    }

    @Test
    fun `existsById should return true when customer exists`() {
        // Given
        whenever(customerRepository.existsById("customer-1")).thenReturn(true)

        // When
        val result = customerService.existsById("customer-1")

        // Then
        assertTrue(result)
        verify(customerRepository).existsById("customer-1")
    }

    @Test
    fun `existsById should return false when customer does not exist`() {
        // Given
        whenever(customerRepository.existsById("customer-1")).thenReturn(false)

        // When
        val result = customerService.existsById("customer-1")

        // Then
        assertFalse(result)
        verify(customerRepository).existsById("customer-1")
    }

    @Test
    fun `existsByCifId should return true when customer exists`() {
        // Given
        whenever(customerRepository.findByCifId("CIF001")).thenReturn(Optional.of(sampleCustomer))

        // When
        val result = customerService.existsByCifId("CIF001")

        // Then
        assertTrue(result)
        verify(customerRepository).findByCifId("CIF001")
    }

    @Test
    fun `existsByCifId should return false when customer does not exist`() {
        // Given
        whenever(customerRepository.findByCifId("CIF999")).thenReturn(Optional.empty())

        // When
        val result = customerService.existsByCifId("CIF999")

        // Then
        assertFalse(result)
        verify(customerRepository).findByCifId("CIF999")
    }

    @Test
    fun `existsByCifIdAndCustomerTypeId should return true when customer exists`() {
        // Given
        whenever(customerRepository.findByCifIdAndCustomerTypeId("CIF001", 1))
            .thenReturn(Optional.of(sampleCustomer))

        // When
        val result = customerService.existsByCifIdAndCustomerTypeId("CIF001", 1)

        // Then
        assertTrue(result)
        verify(customerRepository).findByCifIdAndCustomerTypeId("CIF001", 1)
    }

    @Test
    fun `existsByCifIdAndCustomerTypeId should return false when customer does not exist`() {
        // Given
        whenever(customerRepository.findByCifIdAndCustomerTypeId("CIF999", 1))
            .thenReturn(Optional.empty())

        // When
        val result = customerService.existsByCifIdAndCustomerTypeId("CIF999", 1)

        // Then
        assertFalse(result)
        verify(customerRepository).findByCifIdAndCustomerTypeId("CIF999", 1)
    }

    @Test
    fun `count should return total count`() {
        // Given
        whenever(customerRepository.count()).thenReturn(10L)

        // When
        val result = customerService.count()

        // Then
        assertEquals(10L, result)
        verify(customerRepository).count()
    }

    @Test
    fun `countByCustomerTypeId should return count for customer type`() {
        // Given
        val customerTypeId = 1
        val customersWithType = listOf(sampleCustomer, sampleCustomer.copy(id = "customer-3"))
        whenever(customerRepository.findByCustomerTypeId(customerTypeId)).thenReturn(customersWithType)

        // When
        val result = customerService.countByCustomerTypeId(customerTypeId)

        // Then
        assertEquals(2L, result)
        verify(customerRepository).findByCustomerTypeId(customerTypeId)
    }
}