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
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.entity.Account
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.exception.EntityNotFoundException
import th.eknarong.aph.poc.pocmongodbkotlinspringboot.repository.AccountRepository
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class AccountServiceTest {

    @Mock
    private lateinit var accountRepository: AccountRepository

    @InjectMocks
    private lateinit var accountService: AccountService

    private val sampleAccount = Account(
        id = "account-1",
        customerId = "customer-1",
        productId = "product-1",
        productCode = "SAV001",
        productCategory = "Savings",
        accountNumber = "ACC123456",
        parentAccountId = null,
        accountRefKey = "REF001",
        accountCategoryId = 1,
        statusId = 1,
        interestType = "Simple",
        openedDate = LocalDateTime.now(),
        closureDate = null,
        attributes = mapOf("branch" to "Main"),
        createdAt = LocalDateTime.now(),
        createdBy = "admin",
        updatedAt = null,
        updatedBy = null
    )

    private val sampleAccountList = listOf(
        sampleAccount,
        Account(
            id = "account-2",
            customerId = "customer-2",
            productId = "product-2",
            productCode = "CUR001",
            productCategory = "Current",
            accountNumber = "ACC123457",
            parentAccountId = null,
            accountRefKey = "REF002",
            accountCategoryId = 2,
            statusId = 1,
            interestType = "Compound",
            openedDate = LocalDateTime.now(),
            closureDate = null,
            attributes = mapOf("branch" to "Branch"),
            createdAt = LocalDateTime.now(),
            createdBy = "admin",
            updatedAt = null,
            updatedBy = null
        )
    )

    @Test
    fun `create should save and return account`() {
        // Given
        whenever(accountRepository.save(any())).thenReturn(sampleAccount)

        // When
        val result = accountService.create(sampleAccount)

        // Then
        assertEquals(sampleAccount, result)
        verify(accountRepository).save(sampleAccount)
    }

    @Test
    fun `findById should return account when exists`() {
        // Given
        whenever(accountRepository.findById("account-1")).thenReturn(Optional.of(sampleAccount))

        // When
        val result = accountService.findById("account-1")

        // Then
        assertEquals(sampleAccount, result)
        verify(accountRepository).findById("account-1")
    }

    @Test
    fun `findById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountRepository.findById("account-1")).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountService.findById("account-1")
        }
        assertEquals("Account with identifier 'account-1' not found", exception.message)
        verify(accountRepository).findById("account-1")
    }

    @Test
    fun `findByAccountNumber should return account when exists`() {
        // Given
        whenever(accountRepository.findByAccountNumber("ACC123456")).thenReturn(Optional.of(sampleAccount))

        // When
        val result = accountService.findByAccountNumber("ACC123456")

        // Then
        assertEquals(sampleAccount, result)
        verify(accountRepository).findByAccountNumber("ACC123456")
    }

    @Test
    fun `findByAccountNumber should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountRepository.findByAccountNumber("ACC999999")).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountService.findByAccountNumber("ACC999999")
        }
        assertEquals("Account with identifier 'accountNumber: ACC999999' not found", exception.message)
        verify(accountRepository).findByAccountNumber("ACC999999")
    }

    @Test
    fun `findAll should return all accounts`() {
        // Given
        whenever(accountRepository.findAll()).thenReturn(sampleAccountList)

        // When
        val result = accountService.findAll()

        // Then
        assertEquals(sampleAccountList, result)
        verify(accountRepository).findAll()
    }

    @Test
    fun `findAll with pageable should return paged accounts`() {
        // Given
        val pageable: Pageable = PageRequest.of(0, 10)
        val page = PageImpl(sampleAccountList, pageable, sampleAccountList.size.toLong())
        whenever(accountRepository.findAll(pageable)).thenReturn(page)

        // When
        val result = accountService.findAll(pageable)

        // Then
        assertEquals(page, result)
        assertEquals(sampleAccountList, result.content)
        verify(accountRepository).findAll(pageable)
    }

    @Test
    fun `findByCustomerId should return accounts for customer`() {
        // Given
        val customerId = "customer-1"
        val customerAccounts = listOf(sampleAccount)
        whenever(accountRepository.findByCustomerId(customerId)).thenReturn(customerAccounts)

        // When
        val result = accountService.findByCustomerId(customerId)

        // Then
        assertEquals(customerAccounts, result)
        verify(accountRepository).findByCustomerId(customerId)
    }

    @Test
    fun `findByCustomerId with pageable should return paged accounts for customer`() {
        // Given
        val customerId = "customer-1"
        val pageable: Pageable = PageRequest.of(0, 10)
        val customerAccounts = listOf(sampleAccount)
        val page = PageImpl(customerAccounts, pageable, customerAccounts.size.toLong())
        whenever(accountRepository.findByCustomerId(customerId, pageable)).thenReturn(page)

        // When
        val result = accountService.findByCustomerId(customerId, pageable)

        // Then
        assertEquals(page, result)
        assertEquals(customerAccounts, result.content)
        verify(accountRepository).findByCustomerId(customerId, pageable)
    }

    @Test
    fun `findByStatusId should return accounts with specified status`() {
        // Given
        val statusId = 1
        val statusAccounts = sampleAccountList
        whenever(accountRepository.findByStatusId(statusId)).thenReturn(statusAccounts)

        // When
        val result = accountService.findByStatusId(statusId)

        // Then
        assertEquals(statusAccounts, result)
        verify(accountRepository).findByStatusId(statusId)
    }

    @Test
    fun `update should update and return account when exists`() {
        // Given
        val updatedAccount = sampleAccount.copy(accountNumber = "ACC123456-UPDATED")
        whenever(accountRepository.findById("account-1")).thenReturn(Optional.of(sampleAccount))
        whenever(accountRepository.save(any())).thenReturn(updatedAccount)

        // When
        val result = accountService.update("account-1", updatedAccount, "updater")

        // Then
        assertEquals(updatedAccount, result)
        verify(accountRepository).findById("account-1")
        verify(accountRepository).save(any())
    }

    @Test
    fun `update should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountRepository.findById("account-1")).thenReturn(Optional.empty())

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountService.update("account-1", sampleAccount, "updater")
        }
        assertEquals("Account with identifier 'account-1' not found", exception.message)
        verify(accountRepository).findById("account-1")
        verify(accountRepository, never()).save(any())
    }

    @Test
    fun `deleteById should delete account when exists`() {
        // Given
        whenever(accountRepository.existsById("account-1")).thenReturn(true)

        // When
        accountService.deleteById("account-1")

        // Then
        verify(accountRepository).existsById("account-1")
        verify(accountRepository).deleteById("account-1")
    }

    @Test
    fun `deleteById should throw EntityNotFoundException when not exists`() {
        // Given
        whenever(accountRepository.existsById("account-1")).thenReturn(false)

        // When & Then
        val exception = assertFailsWith<EntityNotFoundException> {
            accountService.deleteById("account-1")
        }
        assertEquals("Account with identifier 'account-1' not found", exception.message)
        verify(accountRepository).existsById("account-1")
        verify(accountRepository, never()).deleteById("account-1")
    }

    @Test
    fun `existsById should return true when account exists`() {
        // Given
        whenever(accountRepository.existsById("account-1")).thenReturn(true)

        // When
        val result = accountService.existsById("account-1")

        // Then
        assertTrue(result)
        verify(accountRepository).existsById("account-1")
    }

    @Test
    fun `existsById should return false when account does not exist`() {
        // Given
        whenever(accountRepository.existsById("account-1")).thenReturn(false)

        // When
        val result = accountService.existsById("account-1")

        // Then
        assertFalse(result)
        verify(accountRepository).existsById("account-1")
    }

    @Test
    fun `count should return total count`() {
        // Given
        whenever(accountRepository.count()).thenReturn(100L)

        // When
        val result = accountService.count()

        // Then
        assertEquals(100L, result)
        verify(accountRepository).count()
    }
}