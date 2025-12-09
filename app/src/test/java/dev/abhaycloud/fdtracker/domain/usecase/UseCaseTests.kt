package dev.abhaycloud.fdtracker.domain.usecase

import dev.abhaycloud.fdtracker.domain.model.FixedDeposit
import dev.abhaycloud.fdtracker.domain.repository.FixedDepositRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.Calendar
import java.util.Date

/**
 * Unit tests for Use Cases
 * Uses mock repository to test business logic in isolation
 */
class UseCaseTests {

    private lateinit var mockRepository: FixedDepositRepository

    private fun createTestDate(year: Int, month: Int, day: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun createTestFixedDeposit(
        id: Int = 1,
        bankName: String = "Test Bank",
        principalAmount: Double = 100000.0,
        maturityAmount: Double = 108000.0
    ): FixedDeposit {
        return FixedDeposit(
            id = id,
            bankName = bankName,
            principalAmount = principalAmount,
            maturityAmount = maturityAmount,
            tenure = 12,
            interestRate = 8.0,
            startDate = createTestDate(2024, Calendar.JANUARY, 1),
            maturityDate = createTestDate(2025, Calendar.JANUARY, 1),
            createdAt = Date(),
            notes = "Test notes"
        )
    }

    @Before
    fun setup() {
        mockRepository = mock(FixedDepositRepository::class.java)
    }

    // GetTotalInvestedAmountUseCase Tests
    @Test
    fun getTotalInvestedAmountUseCase_returnsCorrectTotal() = runTest {
        val expectedTotal = 250000.0
        `when`(mockRepository.getTotalInvestedAmount()).thenReturn(flowOf(expectedTotal))

        val useCase = GetTotalInvestedAmountUseCase(mockRepository)
        val result = useCase.execute().first()

        assertEquals(expectedTotal, result, 0.01)
        verify(mockRepository).getTotalInvestedAmount()
    }

    @Test
    fun getTotalInvestedAmountUseCase_returnsZero_whenNoDeposits() = runTest {
        `when`(mockRepository.getTotalInvestedAmount()).thenReturn(flowOf(0.0))

        val useCase = GetTotalInvestedAmountUseCase(mockRepository)
        val result = useCase.execute().first()

        assertEquals(0.0, result, 0.01)
    }

    // GetTotalMaturityAmountUseCase Tests
    @Test
    fun getTotalMaturityAmountUseCase_returnsCorrectTotal() = runTest {
        val expectedTotal = 270000.0
        `when`(mockRepository.getTotalMaturityAmount()).thenReturn(flowOf(expectedTotal))

        val useCase = GetTotalMaturityAmountUseCase(mockRepository)
        val result = useCase.execute().first()

        assertEquals(expectedTotal, result, 0.01)
        verify(mockRepository).getTotalMaturityAmount()
    }

    // GetAllFixedDepositUseCase Tests
    @Test
    fun getAllFixedDepositUseCase_returnsAllDeposits() = runTest {
        val deposits = listOf(
            createTestFixedDeposit(id = 1, bankName = "Bank A"),
            createTestFixedDeposit(id = 2, bankName = "Bank B"),
            createTestFixedDeposit(id = 3, bankName = "Bank C")
        )
        `when`(mockRepository.getAllFixedDeposits()).thenReturn(flowOf(deposits))

        val useCase = GetAllFixedDepositUseCase(mockRepository)
        val result = useCase.execute().first()

        assertEquals(3, result.size)
        assertTrue(result.any { it.bankName == "Bank A" })
        assertTrue(result.any { it.bankName == "Bank B" })
        assertTrue(result.any { it.bankName == "Bank C" })
    }

    @Test
    fun getAllFixedDepositUseCase_returnsEmptyList_whenNoDeposits() = runTest {
        `when`(mockRepository.getAllFixedDeposits()).thenReturn(flowOf(emptyList()))

        val useCase = GetAllFixedDepositUseCase(mockRepository)
        val result = useCase.execute().first()

        assertTrue(result.isEmpty())
    }

    // GetFixedDepositByIDUseCase Tests
    @Test
    fun getFixedDepositByIDUseCase_returnsCorrectDeposit() = runTest {
        val deposit = createTestFixedDeposit(id = 5)
        `when`(mockRepository.getFixedDepositById(5)).thenReturn(flowOf(deposit))

        val useCase = GetFixedDepositByIDUseCase(mockRepository)
        val result = useCase.execute(5).first()

        assertNotNull(result)
        assertEquals(5, result?.id)
        assertEquals("Test Bank", result?.bankName)
    }

    @Test
    fun getFixedDepositByIDUseCase_returnsNull_whenDepositNotFound() = runTest {
        `when`(mockRepository.getFixedDepositById(999)).thenReturn(flowOf(null))

        val useCase = GetFixedDepositByIDUseCase(mockRepository)
        val result = useCase.execute(999).first()

        assertNull(result)
    }

    // AddFixedDepositUseCase Tests
    @Test
    fun addFixedDepositUseCase_addsDepositSuccessfully() = runTest {
        val deposit = createTestFixedDeposit()
        val expectedId = 1L
        `when`(mockRepository.addFixedDeposit(deposit)).thenReturn(expectedId)

        val useCase = AddFixedDepositUseCase(mockRepository)
        val result = useCase.execute(deposit)

        assertEquals(expectedId, result)
        verify(mockRepository).addFixedDeposit(deposit)
    }

    // UpdateFixedDepositUseCase Tests
    @Test
    fun updateFixedDepositUseCase_updatesDepositSuccessfully() = runTest {
        val deposit = createTestFixedDeposit(id = 1, bankName = "Updated Bank")

        val useCase = UpdateFixedDepositUseCase(mockRepository)
        useCase.execute(deposit)

        verify(mockRepository).updateFixedDeposit(deposit)
    }

    // DeleteFixedDepositUseCase Tests
    @Test
    fun deleteFixedDepositUseCase_deletesDepositSuccessfully() = runTest {
        val depositId = 5

        val useCase = DeleteFixedDepositUseCase(mockRepository)
        useCase.execute(depositId)

        verify(mockRepository).deleteFixedDeposit(depositId)
    }

    // DeleteAllFixedDepositsUseCase Tests
    @Test
    fun deleteAllFixedDepositsUseCase_deletesAllDepositsSuccessfully() = runTest {
        val useCase = DeleteAllFixedDepositsUseCase(mockRepository)
        useCase.execute()

        verify(mockRepository).deleteAllFixedDeposits()
    }

    // RescheduleAlarmUseCase Tests
    @Test
    fun rescheduleAlarmUseCase_reschedulesAlarmsSuccessfully() = runTest {
        val useCase = RescheduleAlarmUseCase(mockRepository)
        useCase.execute()

        verify(mockRepository).rescheduleAlarms()
    }

    // Edge case: Multiple deposits with same bank
    @Test
    fun getAllFixedDepositUseCase_handlesDuplicateBankNames() = runTest {
        val deposits = listOf(
            createTestFixedDeposit(id = 1, bankName = "Same Bank", principalAmount = 50000.0),
            createTestFixedDeposit(id = 2, bankName = "Same Bank", principalAmount = 75000.0),
            createTestFixedDeposit(id = 3, bankName = "Same Bank", principalAmount = 100000.0)
        )
        `when`(mockRepository.getAllFixedDeposits()).thenReturn(flowOf(deposits))

        val useCase = GetAllFixedDepositUseCase(mockRepository)
        val result = useCase.execute().first()

        assertEquals(3, result.size)
        assertTrue(result.all { it.bankName == "Same Bank" })
        // Verify all have different amounts
        assertEquals(setOf(50000.0, 75000.0, 100000.0), result.map { it.principalAmount }.toSet())
    }

    // Edge case: Very large amounts
    @Test
    fun getTotalInvestedAmountUseCase_handlesLargeAmounts() = runTest {
        val largeAmount = 999999999.99
        `when`(mockRepository.getTotalInvestedAmount()).thenReturn(flowOf(largeAmount))

        val useCase = GetTotalInvestedAmountUseCase(mockRepository)
        val result = useCase.execute().first()

        assertEquals(largeAmount, result, 0.01)
    }

    // Edge case: Deposits with zero amounts
    @Test
    fun addFixedDepositUseCase_handlesZeroAmounts() = runTest {
        val deposit = createTestFixedDeposit(principalAmount = 0.0, maturityAmount = 0.0)
        val expectedId = 1L
        `when`(mockRepository.addFixedDeposit(deposit)).thenReturn(expectedId)

        val useCase = AddFixedDepositUseCase(mockRepository)
        val result = useCase.execute(deposit)

        assertEquals(expectedId, result)
        verify(mockRepository).addFixedDeposit(deposit)
    }
}