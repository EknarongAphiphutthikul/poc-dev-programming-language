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
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountStatus
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.AccountStatusRepository
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class AccountStatusServiceTest {

    @Mock
    private lateinit var accountStatusRepository: AccountStatusRepository

    @InjectMocks
    private lateinit var accountStatusService: AccountStatusService

    private val sampleAccountStatus = AccountStatus(
        id = 1,
        name = "Active",
        createdAt = LocalDateTime.now(),
        updatedAt = null
    )

    private val sampleAccountStatusList = listOf(
        sampleAccountStatus,
        AccountStatus(
            id = 2,
            name = "Inactive",
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
    )

    @Test
    fun `create should save and return account status`() {
        // Given
        whenever(accountStatusRepository.save(any())).thenReturn(sampleAccountStatus)

        // When
        val result = accountStatusService.create(sampleAccountStatus)

        // Then
        assertEquals(sampleAccountStatus, result)
        verify(accountStatusRepository).save(sampleAccountStatus)
    }

    @Test
    fun `findById should return account status when exists`() {
        // Given
        whenever(accountStatusRepository.findById(1)).thenReturn(Optional.of(sampleAccountStatus))

        // When
        val result = accountStatusService.findById(1)

        // Then
        assertEquals(sampleAccountStatus, result)
        verify(accountStatusRepository).findById(1)
    }

    @Test
    fun `findById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountStatusRepository.findById(1)).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountStatusService.findById(1)
        }
        assertEquals("AccountStatus with identifier '1' not found", exception.message)
        verify(accountStatusRepository).findById(1)
    }

    @Test
    fun `findByName should return account status when exists`() {
        // Given
        whenever(accountStatusRepository.findByName("Active")).thenReturn(Optional.of(sampleAccountStatus))

        // When
        val result = accountStatusService.findByName("Active")

        // Then
        assertEquals(sampleAccountStatus, result)
        verify(accountStatusRepository).findByName("Active")
    }

    @Test
    fun `findByName should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountStatusRepository.findByName("NonExistent")).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountStatusService.findByName("NonExistent")
        }
        assertEquals("AccountStatus with identifier 'NonExistent' not found", exception.message)
        verify(accountStatusRepository).findByName("NonExistent")
    }

    @Test
    fun `findAll should return all account statuses`() {
        // Given
        whenever(accountStatusRepository.findAll()).thenReturn(sampleAccountStatusList)

        // When
        val result = accountStatusService.findAll()

        // Then
        assertEquals(sampleAccountStatusList, result)
        verify(accountStatusRepository).findAll()
    }

    @Test
    fun `findAll with pageable should return paged account statuses`() {
        // Given
        val pageable: Pageable = PageRequest.of(0, 10)
        val page = PageImpl(sampleAccountStatusList, pageable, sampleAccountStatusList.size.toLong())
        whenever(accountStatusRepository.findAll(pageable)).thenReturn(page)

        // When
        val result = accountStatusService.findAll(pageable)

        // Then
        assertEquals(page, result)
        assertEquals(sampleAccountStatusList, result.content)
        verify(accountStatusRepository).findAll(pageable)
    }

    @Test
    fun `searchByName should return matching account statuses`() {
        // Given
        val searchTerm = "Act"
        val matchingStatuses = listOf(sampleAccountStatus)
        whenever(accountStatusRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(matchingStatuses)

        // When
        val result = accountStatusService.searchByName(searchTerm)

        // Then
        assertEquals(matchingStatuses, result)
        verify(accountStatusRepository).findByNameContainingIgnoreCase(searchTerm)
    }

    @Test
    fun `update should update and return account status when exists`() {
        // Given
        val updatedAccountStatus = sampleAccountStatus.copy(name = "Updated Active")
        whenever(accountStatusRepository.findById(1)).thenReturn(Optional.of(sampleAccountStatus))
        whenever(accountStatusRepository.save(any())).thenReturn(updatedAccountStatus)

        // When
        val result = accountStatusService.update(1, updatedAccountStatus)

        // Then
        assertEquals(updatedAccountStatus, result)
        verify(accountStatusRepository).findById(1)
        verify(accountStatusRepository).save(any())
    }

    @Test
    fun `update should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountStatusRepository.findById(1)).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountStatusService.update(1, sampleAccountStatus)
        }
        assertEquals("AccountStatus with identifier '1' not found", exception.message)
        verify(accountStatusRepository).findById(1)
        verify(accountStatusRepository, never()).save(any())
    }

    @Test
    fun `deleteById should delete account status when exists`() {
        // Given
        whenever(accountStatusRepository.existsById(1)).thenReturn(true)

        // When
        accountStatusService.deleteById(1)

        // Then
        verify(accountStatusRepository).existsById(1)
        verify(accountStatusRepository).deleteById(1)
    }

    @Test
    fun `deleteById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountStatusRepository.existsById(1)).thenReturn(false)

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountStatusService.deleteById(1)
        }
        assertEquals("AccountStatus with identifier '1' not found", exception.message)
        verify(accountStatusRepository).existsById(1)
        verify(accountStatusRepository, never()).deleteById(1)
    }

    @Test
    fun `existsById should return true when account status exists`() {
        // Given
        whenever(accountStatusRepository.existsById(1)).thenReturn(true)

        // When
        val result = accountStatusService.existsById(1)

        // Then
        assertTrue(result)
        verify(accountStatusRepository).existsById(1)
    }

    @Test
    fun `existsById should return false when account status does not exist`() {
        // Given
        whenever(accountStatusRepository.existsById(1)).thenReturn(false)

        // When
        val result = accountStatusService.existsById(1)

        // Then
        assertFalse(result)
        verify(accountStatusRepository).existsById(1)
    }

    @Test
    fun `existsByName should return true when account status exists`() {
        // Given
        whenever(accountStatusRepository.findByName("Active")).thenReturn(Optional.of(sampleAccountStatus))

        // When
        val result = accountStatusService.existsByName("Active")

        // Then
        assertTrue(result)
        verify(accountStatusRepository).findByName("Active")
    }

    @Test
    fun `existsByName should return false when account status does not exist`() {
        // Given
        whenever(accountStatusRepository.findByName("NonExistent")).thenReturn(Optional.empty())

        // When
        val result = accountStatusService.existsByName("NonExistent")

        // Then
        assertFalse(result)
        verify(accountStatusRepository).findByName("NonExistent")
    }

    @Test
    fun `count should return total count`() {
        // Given
        whenever(accountStatusRepository.count()).thenReturn(10L)

        // When
        val result = accountStatusService.count()

        // Then
        assertEquals(10L, result)
        verify(accountStatusRepository).count()
    }
}