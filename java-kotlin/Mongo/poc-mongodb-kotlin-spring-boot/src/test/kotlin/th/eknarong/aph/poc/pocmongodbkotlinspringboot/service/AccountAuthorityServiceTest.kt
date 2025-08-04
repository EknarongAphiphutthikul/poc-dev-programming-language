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
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.AccountAuthority
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.AccountAuthorityRepository
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class AccountAuthorityServiceTest {

    @Mock
    private lateinit var accountAuthorityRepository: AccountAuthorityRepository

    @InjectMocks
    private lateinit var accountAuthorityService: AccountAuthorityService

    private val sampleAccountAuthority = AccountAuthority(
        id = "authority-1",
        customerId = "customer-1",
        accountNumber = "ACC123456",
        position = "Primary",
        effectiveDateFrom = LocalDateTime.now(),
        effectiveDateTo = null,
        createdAt = LocalDateTime.now(),
        createdBy = "admin",
        updatedAt = null,
        updatedBy = null
    )

    private val sampleAccountAuthorityList = listOf(
        sampleAccountAuthority,
        AccountAuthority(
            id = "authority-2",
            customerId = "customer-2",
            accountNumber = "ACC123457",
            position = "Secondary",
            effectiveDateFrom = LocalDateTime.now(),
            effectiveDateTo = null,
            createdAt = LocalDateTime.now(),
            createdBy = "admin",
            updatedAt = null,
            updatedBy = null
        )
    )

    @Test
    fun `create should save and return account authority`() {
        // Given
        whenever(accountAuthorityRepository.save(any())).thenReturn(sampleAccountAuthority)

        // When
        val result = accountAuthorityService.create(sampleAccountAuthority)

        // Then
        assertEquals(sampleAccountAuthority, result)
        verify(accountAuthorityRepository).save(sampleAccountAuthority)
    }

    @Test
    fun `findById should return account authority when exists`() {
        // Given
        whenever(accountAuthorityRepository.findById("authority-1")).thenReturn(Optional.of(sampleAccountAuthority))

        // When
        val result = accountAuthorityService.findById("authority-1")

        // Then
        assertEquals(sampleAccountAuthority, result)
        verify(accountAuthorityRepository).findById("authority-1")
    }

    @Test
    fun `findById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountAuthorityRepository.findById("authority-1")).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountAuthorityService.findById("authority-1")
        }
        assertEquals("AccountAuthority with identifier 'authority-1' not found", exception.message)
        verify(accountAuthorityRepository).findById("authority-1")
    }

    @Test
    fun `findAll should return all account authorities`() {
        // Given
        whenever(accountAuthorityRepository.findAll()).thenReturn(sampleAccountAuthorityList)

        // When
        val result = accountAuthorityService.findAll()

        // Then
        assertEquals(sampleAccountAuthorityList, result)
        verify(accountAuthorityRepository).findAll()
    }

    @Test
    fun `findAll with pageable should return paged account authorities`() {
        // Given
        val pageable: Pageable = PageRequest.of(0, 10)
        val page = PageImpl(sampleAccountAuthorityList, pageable, sampleAccountAuthorityList.size.toLong())
        whenever(accountAuthorityRepository.findAll(pageable)).thenReturn(page)

        // When
        val result = accountAuthorityService.findAll(pageable)

        // Then
        assertEquals(page, result)
        assertEquals(sampleAccountAuthorityList, result.content)
        verify(accountAuthorityRepository).findAll(pageable)
    }

    @Test
    fun `findByCustomerId should return authorities for customer`() {
        // Given
        val customerId = "customer-1"
        val customerAuthorities = listOf(sampleAccountAuthority)
        whenever(accountAuthorityRepository.findByCustomerId(customerId)).thenReturn(customerAuthorities)

        // When
        val result = accountAuthorityService.findByCustomerId(customerId)

        // Then
        assertEquals(customerAuthorities, result)
        verify(accountAuthorityRepository).findByCustomerId(customerId)
    }

    @Test
    fun `findByAccountNumber should return authorities for account`() {
        // Given
        val accountNumber = "ACC123456"
        val accountAuthorities = listOf(sampleAccountAuthority)
        whenever(accountAuthorityRepository.findByAccountNumber(accountNumber)).thenReturn(accountAuthorities)

        // When
        val result = accountAuthorityService.findByAccountNumber(accountNumber)

        // Then
        assertEquals(accountAuthorities, result)
        verify(accountAuthorityRepository).findByAccountNumber(accountNumber)
    }

    @Test
    fun `update should update and return account authority when exists`() {
        // Given
        val updatedAuthority = sampleAccountAuthority.copy(position = "Updated Primary")
        whenever(accountAuthorityRepository.findById("authority-1")).thenReturn(Optional.of(sampleAccountAuthority))
        whenever(accountAuthorityRepository.save(any())).thenReturn(updatedAuthority)

        // When
        val result = accountAuthorityService.update("authority-1", updatedAuthority, "updater")

        // Then
        assertEquals(updatedAuthority, result)
        verify(accountAuthorityRepository).findById("authority-1")
        verify(accountAuthorityRepository).save(any())
    }

    @Test
    fun `update should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountAuthorityRepository.findById("authority-1")).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountAuthorityService.update("authority-1", sampleAccountAuthority, "updater")
        }
        assertEquals("AccountAuthority with identifier 'authority-1' not found", exception.message)
        verify(accountAuthorityRepository).findById("authority-1")
        verify(accountAuthorityRepository, never()).save(any())
    }

    @Test
    fun `deleteById should delete account authority when exists`() {
        // Given
        whenever(accountAuthorityRepository.existsById("authority-1")).thenReturn(true)

        // When
        accountAuthorityService.deleteById("authority-1")

        // Then
        verify(accountAuthorityRepository).existsById("authority-1")
        verify(accountAuthorityRepository).deleteById("authority-1")
    }

    @Test
    fun `deleteById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountAuthorityRepository.existsById("authority-1")).thenReturn(false)

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountAuthorityService.deleteById("authority-1")
        }
        assertEquals("AccountAuthority with identifier 'authority-1' not found", exception.message)
        verify(accountAuthorityRepository).existsById("authority-1")
        verify(accountAuthorityRepository, never()).deleteById("authority-1")
    }

    @Test
    fun `existsById should return true when account authority exists`() {
        // Given
        whenever(accountAuthorityRepository.existsById("authority-1")).thenReturn(true)

        // When
        val result = accountAuthorityService.existsById("authority-1")

        // Then
        assertTrue(result)
        verify(accountAuthorityRepository).existsById("authority-1")
    }

    @Test
    fun `existsById should return false when account authority does not exist`() {
        // Given
        whenever(accountAuthorityRepository.existsById("authority-1")).thenReturn(false)

        // When
        val result = accountAuthorityService.existsById("authority-1")

        // Then
        assertFalse(result)
        verify(accountAuthorityRepository).existsById("authority-1")
    }

    @Test
    fun `count should return total count`() {
        // Given
        whenever(accountAuthorityRepository.count()).thenReturn(25L)

        // When
        val result = accountAuthorityService.count()

        // Then
        assertEquals(25L, result)
        verify(accountAuthorityRepository).count()
    }
}