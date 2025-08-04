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
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.CustomerType
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.CustomerTypeRepository
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class CustomerTypeServiceTest {

    @Mock
    private lateinit var customerTypeRepository: CustomerTypeRepository

    @InjectMocks
    private lateinit var customerTypeService: CustomerTypeService

    private val sampleCustomerType = CustomerType(
        id = 1,
        name = "Individual",
        createdAt = LocalDateTime.now(),
        updatedAt = null
    )

    private val sampleCustomerTypeList = listOf(
        sampleCustomerType,
        CustomerType(
            id = 2,
            name = "Corporate",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
    )

    @Test
    fun `create should save and return customer type`() {
        // Given
        whenever(customerTypeRepository.save(any())).thenReturn(sampleCustomerType)

        // When
        val result = customerTypeService.create(sampleCustomerType)

        // Then
        assertEquals(sampleCustomerType, result)
        verify(customerTypeRepository).save(sampleCustomerType)
    }

    @Test
    fun `findById should return customer type when exists`() {
        // Given
        whenever(customerTypeRepository.findById(1)).thenReturn(Optional.of(sampleCustomerType))

        // When
        val result = customerTypeService.findById(1)

        // Then
        assertEquals(sampleCustomerType, result)
        verify(customerTypeRepository).findById(1)
    }

    @Test
    fun `findById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(customerTypeRepository.findById(1)).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            customerTypeService.findById(1)
        }
        assertEquals("CustomerType with identifier '1' not found", exception.message)
        verify(customerTypeRepository).findById(1)
    }

    @Test
    fun `findByName should return customer type when exists`() {
        // Given
        whenever(customerTypeRepository.findByName("Individual")).thenReturn(Optional.of(sampleCustomerType))

        // When
        val result = customerTypeService.findByName("Individual")

        // Then
        assertEquals(sampleCustomerType, result)
        verify(customerTypeRepository).findByName("Individual")
    }

    @Test
    fun `findByName should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(customerTypeRepository.findByName("NonExistent")).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            customerTypeService.findByName("NonExistent")
        }
        assertEquals("CustomerType with identifier 'NonExistent' not found", exception.message)
        verify(customerTypeRepository).findByName("NonExistent")
    }

    @Test
    fun `findAll should return all customer types`() {
        // Given
        whenever(customerTypeRepository.findAll()).thenReturn(sampleCustomerTypeList)

        // When
        val result = customerTypeService.findAll()

        // Then
        assertEquals(sampleCustomerTypeList, result)
        verify(customerTypeRepository).findAll()
    }

    @Test
    fun `findAll with pageable should return paged customer types`() {
        // Given
        val pageable: Pageable = PageRequest.of(0, 10)
        val page = PageImpl(sampleCustomerTypeList, pageable, sampleCustomerTypeList.size.toLong())
        whenever(customerTypeRepository.findAll(pageable)).thenReturn(page)

        // When
        val result = customerTypeService.findAll(pageable)

        // Then
        assertEquals(page, result)
        assertEquals(sampleCustomerTypeList, result.content)
        verify(customerTypeRepository).findAll(pageable)
    }

    @Test
    fun `searchByName should return matching customer types`() {
        // Given
        val searchTerm = "Ind"
        val matchingTypes = listOf(sampleCustomerType)
        whenever(customerTypeRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(matchingTypes)

        // When
        val result = customerTypeService.searchByName(searchTerm)

        // Then
        assertEquals(matchingTypes, result)
        verify(customerTypeRepository).findByNameContainingIgnoreCase(searchTerm)
    }

    @Test
    fun `update should update and return customer type when exists`() {
        // Given
        val updatedCustomerType = sampleCustomerType.copy(name = "Updated Individual")
        whenever(customerTypeRepository.findById(1)).thenReturn(Optional.of(sampleCustomerType))
        whenever(customerTypeRepository.save(any())).thenReturn(updatedCustomerType)

        // When
        val result = customerTypeService.update(1, updatedCustomerType)

        // Then
        assertEquals(updatedCustomerType, result)
        verify(customerTypeRepository).findById(1)
        verify(customerTypeRepository).save(any())
    }

    @Test
    fun `update should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(customerTypeRepository.findById(1)).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            customerTypeService.update(1, sampleCustomerType)
        }
        assertEquals("CustomerType with identifier '1' not found", exception.message)
        verify(customerTypeRepository).findById(1)
        verify(customerTypeRepository, never()).save(any())
    }

    @Test
    fun `deleteById should delete customer type when exists`() {
        // Given
        whenever(customerTypeRepository.existsById(1)).thenReturn(true)

        // When
        customerTypeService.deleteById(1)

        // Then
        verify(customerTypeRepository).existsById(1)
        verify(customerTypeRepository).deleteById(1)
    }

    @Test
    fun `deleteById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(customerTypeRepository.existsById(1)).thenReturn(false)

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            customerTypeService.deleteById(1)
        }
        assertEquals("CustomerType with identifier '1' not found", exception.message)
        verify(customerTypeRepository).existsById(1)
        verify(customerTypeRepository, never()).deleteById(1)
    }

    @Test
    fun `existsById should return true when customer type exists`() {
        // Given
        whenever(customerTypeRepository.existsById(1)).thenReturn(true)

        // When
        val result = customerTypeService.existsById(1)

        // Then
        assertTrue(result)
        verify(customerTypeRepository).existsById(1)
    }

    @Test
    fun `existsById should return false when customer type does not exist`() {
        // Given
        whenever(customerTypeRepository.existsById(1)).thenReturn(false)

        // When
        val result = customerTypeService.existsById(1)

        // Then
        assertFalse(result)
        verify(customerTypeRepository).existsById(1)
    }

    @Test
    fun `existsByName should return true when customer type exists`() {
        // Given
        whenever(customerTypeRepository.findByName("Individual")).thenReturn(Optional.of(sampleCustomerType))

        // When
        val result = customerTypeService.existsByName("Individual")

        // Then
        assertTrue(result)
        verify(customerTypeRepository).findByName("Individual")
    }

    @Test
    fun `existsByName should return false when customer type does not exist`() {
        // Given
        whenever(customerTypeRepository.findByName("NonExistent")).thenReturn(Optional.empty())

        // When
        val result = customerTypeService.existsByName("NonExistent")

        // Then
        assertFalse(result)
        verify(customerTypeRepository).findByName("NonExistent")
    }

    @Test
    fun `count should return total count`() {
        // Given
        whenever(customerTypeRepository.count()).thenReturn(10L)

        // When
        val result = customerTypeService.count()

        // Then
        assertEquals(10L, result)
        verify(customerTypeRepository).count()
    }
}