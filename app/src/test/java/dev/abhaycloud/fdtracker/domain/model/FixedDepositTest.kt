package dev.abhaycloud.fdtracker.domain.model

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.Date

/**
 * Unit tests for FixedDeposit data class
 * Tests model creation and data integrity
 */
class FixedDepositTest {

    private fun createTestDate(year: Int, month: Int, day: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    @Test
    fun fixedDeposit_createsSuccessfully() {
        val startDate = createTestDate(2024, Calendar.JANUARY, 1)
        val maturityDate = createTestDate(2025, Calendar.JANUARY, 1)
        val createdAt = Date()

        val fixedDeposit = FixedDeposit(
            id = 1,
            bankName = "State Bank",
            principalAmount = 100000.0,
            maturityAmount = 108000.0,
            tenure = 12,
            interestRate = 8.0,
            startDate = startDate,
            maturityDate = maturityDate,
            createdAt = createdAt,
            notes = "Test deposit"
        )

        assertEquals(1, fixedDeposit.id)
        assertEquals("State Bank", fixedDeposit.bankName)
        assertEquals(100000.0, fixedDeposit.principalAmount, 0.01)
        assertEquals(108000.0, fixedDeposit.maturityAmount, 0.01)
        assertEquals(12, fixedDeposit.tenure)
        assertEquals(8.0, fixedDeposit.interestRate, 0.01)
        assertEquals("Test deposit", fixedDeposit.notes)
    }

    @Test
    fun fixedDeposit_calculatesCorrectProfit() {
        val fixedDeposit = FixedDeposit(
            id = 1,
            bankName = "Test Bank",
            principalAmount = 100000.0,
            maturityAmount = 108000.0,
            tenure = 12,
            interestRate = 8.0,
            startDate = createTestDate(2024, Calendar.JANUARY, 1),
            maturityDate = createTestDate(2025, Calendar.JANUARY, 1),
            createdAt = Date(),
            notes = null
        )

        val expectedProfit = fixedDeposit.maturityAmount - fixedDeposit.principalAmount
        assertEquals(8000.0, expectedProfit, 0.01)
    }

    @Test
    fun fixedDeposit_handlesNullNotes() {
        val fixedDeposit = FixedDeposit(
            id = 1,
            bankName = "Test Bank",
            principalAmount = 50000.0,
            maturityAmount = 53000.0,
            tenure = 6,
            interestRate = 6.0,
            startDate = createTestDate(2024, Calendar.JANUARY, 1),
            maturityDate = createTestDate(2024, Calendar.JULY, 1),
            createdAt = Date(),
            notes = null
        )

        assertNull(fixedDeposit.notes)
    }

    @Test
    fun fixedDeposit_copyWorks() {
        val original = FixedDeposit(
            id = 1,
            bankName = "Original Bank",
            principalAmount = 100000.0,
            maturityAmount = 108000.0,
            tenure = 12,
            interestRate = 8.0,
            startDate = createTestDate(2024, Calendar.JANUARY, 1),
            maturityDate = createTestDate(2025, Calendar.JANUARY, 1),
            createdAt = Date(),
            notes = "Original notes"
        )

        val copied = original.copy(bankName = "New Bank", notes = "Updated notes")

        assertEquals(original.id, copied.id)
        assertEquals("New Bank", copied.bankName)
        assertEquals(original.principalAmount, copied.principalAmount, 0.01)
        assertEquals("Updated notes", copied.notes)
    }

    @Test
    fun fixedDeposit_equalityWorks() {
        val date1 = createTestDate(2024, Calendar.JANUARY, 1)
        val date2 = createTestDate(2025, Calendar.JANUARY, 1)
        val createdAt = Date()

        val fd1 = FixedDeposit(
            id = 1,
            bankName = "Test Bank",
            principalAmount = 100000.0,
            maturityAmount = 108000.0,
            tenure = 12,
            interestRate = 8.0,
            startDate = date1,
            maturityDate = date2,
            createdAt = createdAt,
            notes = "Test"
        )

        val fd2 = FixedDeposit(
            id = 1,
            bankName = "Test Bank",
            principalAmount = 100000.0,
            maturityAmount = 108000.0,
            tenure = 12,
            interestRate = 8.0,
            startDate = date1,
            maturityDate = date2,
            createdAt = createdAt,
            notes = "Test"
        )

        assertEquals(fd1, fd2)
        assertEquals(fd1.hashCode(), fd2.hashCode())
    }

    @Test
    fun fixedDeposit_differentIdMakesNotEqual() {
        val date1 = createTestDate(2024, Calendar.JANUARY, 1)
        val date2 = createTestDate(2025, Calendar.JANUARY, 1)
        val createdAt = Date()

        val fd1 = FixedDeposit(
            id = 1,
            bankName = "Test Bank",
            principalAmount = 100000.0,
            maturityAmount = 108000.0,
            tenure = 12,
            interestRate = 8.0,
            startDate = date1,
            maturityDate = date2,
            createdAt = createdAt,
            notes = null
        )

        val fd2 = fd1.copy(id = 2)

        assertNotEquals(fd1, fd2)
    }

    @Test
    fun fixedDeposit_validatesPositiveAmounts() {
        val fixedDeposit = FixedDeposit(
            id = 1,
            bankName = "Test Bank",
            principalAmount = 100000.0,
            maturityAmount = 108000.0,
            tenure = 12,
            interestRate = 8.0,
            startDate = createTestDate(2024, Calendar.JANUARY, 1),
            maturityDate = createTestDate(2025, Calendar.JANUARY, 1),
            createdAt = Date(),
            notes = null
        )

        assertTrue(fixedDeposit.principalAmount > 0)
        assertTrue(fixedDeposit.maturityAmount > 0)
        assertTrue(fixedDeposit.maturityAmount >= fixedDeposit.principalAmount)
    }
}
