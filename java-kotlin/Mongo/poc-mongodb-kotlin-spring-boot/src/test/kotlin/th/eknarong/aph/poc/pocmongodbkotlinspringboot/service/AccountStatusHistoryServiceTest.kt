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
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountStatusHistory
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.AccountStatusHistoryRepository
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class AccountStatusHistoryServiceTest {

    @Mock
    private lateinit var accountStatusHistoryRepository: AccountStatusHistoryRepository

    @InjectMocks
    private lateinit var accountStatusHistoryService: AccountStatusHistoryService

    private val sampleAccountStatusHistory = AccountStatusHistory(
        id = "history-1",
        accountId = "account-1",
        statusId = 1,
        createdAt = LocalDateTime.now(),
        createdBy = "admin",
        updatedAt = null,
        updatedBy = null
    )

    private val sampleAccountStatusHistoryList = listOf(
        sampleAccountStatusHistory,
        AccountStatusHistory(
            id = "history-2",
            accountId = "account-2",
            statusId = 2,
            createdAt = LocalDateTime.now(),
            createdBy = "admin",
            updatedAt = null,
            updatedBy = null
        )
    )

    @Test
    fun `create should save and return account status history`() {
        // Given
        whenever(accountStatusHistoryRepository.save(any())).thenReturn(sampleAccountStatusHistory)

        // When
        val result = accountStatusHistoryService.create(sampleAccountStatusHistory)

        // Then
        assertEquals(sampleAccountStatusHistory, result)
        verify(accountStatusHistoryRepository).save(sampleAccountStatusHistory)
    }

    @Test
    fun `findById should return account status history when exists`() {
        // Given
        whenever(accountStatusHistoryRepository.findById("history-1")).thenReturn(Optional.of(sampleAccountStatusHistory))

        // When
        val result = accountStatusHistoryService.findById("history-1")

        // Then
        assertEquals(sampleAccountStatusHistory, result)
        verify(accountStatusHistoryRepository).findById("history-1")
    }

    @Test
    fun `findById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountStatusHistoryRepository.findById("history-1")).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountStatusHistoryService.findById("history-1")
        }
        assertEquals("AccountStatusHistory with identifier 'history-1' not found", exception.message)
        verify(accountStatusHistoryRepository).findById("history-1")
    }

    @Test
    fun `findAll should return all account status histories`() {
        // Given
        whenever(accountStatusHistoryRepository.findAll()).thenReturn(sampleAccountStatusHistoryList)

        // When
        val result = accountStatusHistoryService.findAll()

        // Then
        assertEquals(sampleAccountStatusHistoryList, result)
        verify(accountStatusHistoryRepository).findAll()
    }

    @Test
    fun `findAll with pageable should return paged account status histories`() {
        // Given
        val pageable: Pageable = PageRequest.of(0, 10)
        val page = PageImpl(sampleAccountStatusHistoryList, pageable, sampleAccountStatusHistoryList.size.toLong())
        whenever(accountStatusHistoryRepository.findAll(pageable)).thenReturn(page)

        // When
        val result = accountStatusHistoryService.findAll(pageable)

        // Then
        assertEquals(page, result)
        assertEquals(sampleAccountStatusHistoryList, result.content)
        verify(accountStatusHistoryRepository).findAll(pageable)
    }

    @Test
    fun `findByAccountId should return histories for account`() {
        // Given
        val accountId = "account-1"
        val accountHistories = listOf(sampleAccountStatusHistory)
        whenever(accountStatusHistoryRepository.findByAccountId(accountId)).thenReturn(accountHistories)

        // When
        val result = accountStatusHistoryService.findByAccountId(accountId)

        // Then
        assertEquals(accountHistories, result)
        verify(accountStatusHistoryRepository).findByAccountId(accountId)
    }

    @Test
    fun `findByStatusId should return histories with specified status`() {
        // Given
        val statusId = 1
        val statusHistories = listOf(sampleAccountStatusHistory)
        whenever(accountStatusHistoryRepository.findByStatusId(statusId)).thenReturn(statusHistories)

        // When
        val result = accountStatusHistoryService.findByStatusId(statusId)

        // Then
        assertEquals(statusHistories, result)
        verify(accountStatusHistoryRepository).findByStatusId(statusId)
    }

    @Test
    fun `update should update and return account status history when exists`() {
        // Given
        val updatedHistory = sampleAccountStatusHistory.copy(statusId = 2)
        whenever(accountStatusHistoryRepository.findById("history-1")).thenReturn(Optional.of(sampleAccountStatusHistory))
        whenever(accountStatusHistoryRepository.save(any())).thenReturn(updatedHistory)

        // When
        val result = accountStatusHistoryService.update("history-1", updatedHistory, "updater")

        // Then
        assertEquals(updatedHistory, result)
        verify(accountStatusHistoryRepository).findById("history-1")
        verify(accountStatusHistoryRepository).save(any())
    }

    @Test
    fun `update should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountStatusHistoryRepository.findById("history-1")).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountStatusHistoryService.update("history-1", sampleAccountStatusHistory, "updater")
        }
        assertEquals("AccountStatusHistory with identifier 'history-1' not found", exception.message)
        verify(accountStatusHistoryRepository).findById("history-1")
        verify(accountStatusHistoryRepository, never()).save(any())
    }

    @Test
    fun `deleteById should delete account status history when exists`() {
        // Given
        whenever(accountStatusHistoryRepository.existsById("history-1")).thenReturn(true)

        // When
        accountStatusHistoryService.deleteById("history-1")

        // Then
        verify(accountStatusHistoryRepository).existsById("history-1")
        verify(accountStatusHistoryRepository).deleteById("history-1")
    }

    @Test
    fun `deleteById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountStatusHistoryRepository.existsById("history-1")).thenReturn(false)

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountStatusHistoryService.deleteById("history-1")
        }
        assertEquals("AccountStatusHistory with identifier 'history-1' not found", exception.message)
        verify(accountStatusHistoryRepository).existsById("history-1")
        verify(accountStatusHistoryRepository, never()).deleteById("history-1")
    }

    @Test
    fun `existsById should return true when account status history exists`() {
        // Given
        whenever(accountStatusHistoryRepository.existsById("history-1")).thenReturn(true)

        // When
        val result = accountStatusHistoryService.existsById("history-1")

        // Then
        assertTrue(result)
        verify(accountStatusHistoryRepository).existsById("history-1")
    }

    @Test
    fun `existsById should return false when account status history does not exist`() {
        // Given
        whenever(accountStatusHistoryRepository.existsById("history-1")).thenReturn(false)

        // When
        val result = accountStatusHistoryService.existsById("history-1")

        // Then
        assertFalse(result)
        verify(accountStatusHistoryRepository).existsById("history-1")
    }

    @Test
    fun `count should return total count`() {
        // Given
        whenever(accountStatusHistoryRepository.count()).thenReturn(50L)

        // When
        val result = accountStatusHistoryService.count()

        // Then
        assertEquals(50L, result)
        verify(accountStatusHistoryRepository).count()
    }
}