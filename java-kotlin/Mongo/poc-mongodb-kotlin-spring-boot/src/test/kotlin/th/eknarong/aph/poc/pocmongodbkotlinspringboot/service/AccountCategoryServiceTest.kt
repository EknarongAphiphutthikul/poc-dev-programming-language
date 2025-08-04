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
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountCategory
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.AccountCategoryRepository
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class AccountCategoryServiceTest {

    @Mock
    private lateinit var accountCategoryRepository: AccountCategoryRepository

    @InjectMocks
    private lateinit var accountCategoryService: AccountCategoryService

    private val sampleAccountCategory = AccountCategory(
        id = 1,
        name = "Savings",
        createdAt = LocalDateTime.now(),
        updatedAt = null
    )

    private val sampleAccountCategoryList = listOf(
        sampleAccountCategory,
        AccountCategory(
            id = 2,
            name = "Current",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
    )

    @Test
    fun `create should save and return account category`() {
        // Given
        whenever(accountCategoryRepository.save(any())).thenReturn(sampleAccountCategory)

        // When
        val result = accountCategoryService.create(sampleAccountCategory)

        // Then
        assertEquals(sampleAccountCategory, result)
        verify(accountCategoryRepository).save(sampleAccountCategory)
    }

    @Test
    fun `findById should return account category when exists`() {
        // Given
        whenever(accountCategoryRepository.findById(1)).thenReturn(Optional.of(sampleAccountCategory))

        // When
        val result = accountCategoryService.findById(1)

        // Then
        assertEquals(sampleAccountCategory, result)
        verify(accountCategoryRepository).findById(1)
    }

    @Test
    fun `findById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountCategoryRepository.findById(1)).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountCategoryService.findById(1)
        }
        assertEquals("AccountCategory with identifier '1' not found", exception.message)
        verify(accountCategoryRepository).findById(1)
    }

    @Test
    fun `findByName should return account category when exists`() {
        // Given
        whenever(accountCategoryRepository.findByName("Savings")).thenReturn(Optional.of(sampleAccountCategory))

        // When
        val result = accountCategoryService.findByName("Savings")

        // Then
        assertEquals(sampleAccountCategory, result)
        verify(accountCategoryRepository).findByName("Savings")
    }

    @Test
    fun `findByName should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountCategoryRepository.findByName("NonExistent")).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountCategoryService.findByName("NonExistent")
        }
        assertEquals("AccountCategory with identifier 'NonExistent' not found", exception.message)
        verify(accountCategoryRepository).findByName("NonExistent")
    }

    @Test
    fun `findAll should return all account categories`() {
        // Given
        whenever(accountCategoryRepository.findAll()).thenReturn(sampleAccountCategoryList)

        // When
        val result = accountCategoryService.findAll()

        // Then
        assertEquals(sampleAccountCategoryList, result)
        verify(accountCategoryRepository).findAll()
    }

    @Test
    fun `findAll with pageable should return paged account categories`() {
        // Given
        val pageable: Pageable = PageRequest.of(0, 10)
        val page = PageImpl(sampleAccountCategoryList, pageable, sampleAccountCategoryList.size.toLong())
        whenever(accountCategoryRepository.findAll(pageable)).thenReturn(page)

        // When
        val result = accountCategoryService.findAll(pageable)

        // Then
        assertEquals(page, result)
        assertEquals(sampleAccountCategoryList, result.content)
        verify(accountCategoryRepository).findAll(pageable)
    }

    @Test
    fun `searchByName should return matching account categories`() {
        // Given
        val searchTerm = "Sav"
        val matchingCategories = listOf(sampleAccountCategory)
        whenever(accountCategoryRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(matchingCategories)

        // When
        val result = accountCategoryService.searchByName(searchTerm)

        // Then
        assertEquals(matchingCategories, result)
        verify(accountCategoryRepository).findByNameContainingIgnoreCase(searchTerm)
    }

    @Test
    fun `update should update and return account category when exists`() {
        // Given
        val updatedAccountCategory = sampleAccountCategory.copy(name = "Updated Savings")
        whenever(accountCategoryRepository.findById(1)).thenReturn(Optional.of(sampleAccountCategory))
        whenever(accountCategoryRepository.save(any())).thenReturn(updatedAccountCategory)

        // When
        val result = accountCategoryService.update(1, updatedAccountCategory)

        // Then
        assertEquals(updatedAccountCategory, result)
        verify(accountCategoryRepository).findById(1)
        verify(accountCategoryRepository).save(any())
    }

    @Test
    fun `update should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountCategoryRepository.findById(1)).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountCategoryService.update(1, sampleAccountCategory)
        }
        assertEquals("AccountCategory with identifier '1' not found", exception.message)
        verify(accountCategoryRepository).findById(1)
        verify(accountCategoryRepository, never()).save(any())
    }

    @Test
    fun `deleteById should delete account category when exists`() {
        // Given
        whenever(accountCategoryRepository.existsById(1)).thenReturn(true)

        // When
        accountCategoryService.deleteById(1)

        // Then
        verify(accountCategoryRepository).existsById(1)
        verify(accountCategoryRepository).deleteById(1)
    }

    @Test
    fun `deleteById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountCategoryRepository.existsById(1)).thenReturn(false)

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountCategoryService.deleteById(1)
        }
        assertEquals("AccountCategory with identifier '1' not found", exception.message)
        verify(accountCategoryRepository).existsById(1)
        verify(accountCategoryRepository, never()).deleteById(1)
    }

    @Test
    fun `existsById should return true when account category exists`() {
        // Given
        whenever(accountCategoryRepository.existsById(1)).thenReturn(true)

        // When
        val result = accountCategoryService.existsById(1)

        // Then
        assertTrue(result)
        verify(accountCategoryRepository).existsById(1)
    }

    @Test
    fun `existsById should return false when account category does not exist`() {
        // Given
        whenever(accountCategoryRepository.existsById(1)).thenReturn(false)

        // When
        val result = accountCategoryService.existsById(1)

        // Then
        assertFalse(result)
        verify(accountCategoryRepository).existsById(1)
    }

    @Test
    fun `existsByName should return true when account category exists`() {
        // Given
        whenever(accountCategoryRepository.findByName("Savings")).thenReturn(Optional.of(sampleAccountCategory))

        // When
        val result = accountCategoryService.existsByName("Savings")

        // Then
        assertTrue(result)
        verify(accountCategoryRepository).findByName("Savings")
    }

    @Test
    fun `existsByName should return false when account category does not exist`() {
        // Given
        whenever(accountCategoryRepository.findByName("NonExistent")).thenReturn(Optional.empty())

        // When
        val result = accountCategoryService.existsByName("NonExistent")

        // Then
        assertFalse(result)
        verify(accountCategoryRepository).findByName("NonExistent")
    }

    @Test
    fun `count should return total count`() {
        // Given
        whenever(accountCategoryRepository.count()).thenReturn(10L)

        // When
        val result = accountCategoryService.count()

        // Then
        assertEquals(10L, result)
        verify(accountCategoryRepository).count()
    }
}