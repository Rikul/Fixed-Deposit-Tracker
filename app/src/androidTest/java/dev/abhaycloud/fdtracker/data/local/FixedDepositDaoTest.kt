package dev.abhaycloud.fdtracker.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.abhaycloud.fdtracker.data.local.dao.FixedDepositDao
import dev.abhaycloud.fdtracker.data.local.database.FixedDepositDatabase
import dev.abhaycloud.fdtracker.data.local.entity.FixedDepositEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Calendar
import java.util.Date

/**
 * Instrumented tests for FixedDepositDao
 * Tests database operations on an actual Android device/emulator
 */
@RunWith(AndroidJUnit4::class)
class FixedDepositDaoTest {

    private lateinit var fixedDepositDao: FixedDepositDao
    private lateinit var database: FixedDepositDatabase

    private fun createTestDate(year: Int, month: Int, day: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun createTestEntity(
        id: Int = 0,
        bankName: String = "Test Bank",
        principalAmount: Double = 100000.0,
        maturityAmount: Double = 108000.0
    ): FixedDepositEntity {
        return FixedDepositEntity(
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
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            FixedDepositDatabase::class.java
        ).build()
        fixedDepositDao = database.fixedDepositDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveFixedDeposit() = runBlocking {
        val entity = createTestEntity()
        val id = fixedDepositDao.insertFixedDeposit(entity)

        val retrieved = fixedDepositDao.getFixedDepositById(id.toInt()).first()

        assertNotNull(retrieved)
        assertEquals("Test Bank", retrieved?.bankName)
        assertEquals(100000.0, retrieved?.principalAmount ?: 0.0, 0.01)
        assertEquals(108000.0, retrieved?.maturityAmount ?: 0.0, 0.01)
    }

    @Test
    fun getAllFixedDeposits_returnsEmptyList_whenNoData() = runBlocking {
        val deposits = fixedDepositDao.getAllFixedDeposits().first()
        assertTrue(deposits.isEmpty())
    }

    @Test
    fun getAllFixedDeposits_returnsAllDeposits() = runBlocking {
        // Insert multiple deposits
        val entity1 = createTestEntity(bankName = "Bank A", principalAmount = 50000.0)
        val entity2 = createTestEntity(bankName = "Bank B", principalAmount = 75000.0)
        val entity3 = createTestEntity(bankName = "Bank C", principalAmount = 100000.0)

        fixedDepositDao.insertFixedDeposit(entity1)
        fixedDepositDao.insertFixedDeposit(entity2)
        fixedDepositDao.insertFixedDeposit(entity3)

        val deposits = fixedDepositDao.getAllFixedDeposits().first()

        assertEquals(3, deposits.size)
        assertTrue(deposits.any { it.bankName == "Bank A" })
        assertTrue(deposits.any { it.bankName == "Bank B" })
        assertTrue(deposits.any { it.bankName == "Bank C" })
    }

    @Test
    fun updateFixedDeposit_updatesCorrectly() = runBlocking {
        val entity = createTestEntity()
        val id = fixedDepositDao.insertFixedDeposit(entity)

        val updated = entity.copy(
            id = id.toInt(),
            bankName = "Updated Bank",
            principalAmount = 150000.0
        )
        fixedDepositDao.updateFixedDeposit(updated)

        val retrieved = fixedDepositDao.getFixedDepositById(id.toInt()).first()

        assertEquals("Updated Bank", retrieved?.bankName)
        assertEquals(150000.0, retrieved?.principalAmount ?: 0.0, 0.01)
    }

    @Test
    fun deleteFixedDeposit_removesCorrectDeposit() = runBlocking {
        val entity = createTestEntity()
        val id = fixedDepositDao.insertFixedDeposit(entity)

        fixedDepositDao.deleteFixedDeposit(id.toInt())

        val retrieved = fixedDepositDao.getFixedDepositById(id.toInt()).first()
        assertNull(retrieved)
    }

    @Test
    fun deleteAll_removesAllDeposits() = runBlocking {
        // Insert multiple deposits
        fixedDepositDao.insertFixedDeposit(createTestEntity(bankName = "Bank A"))
        fixedDepositDao.insertFixedDeposit(createTestEntity(bankName = "Bank B"))
        fixedDepositDao.insertFixedDeposit(createTestEntity(bankName = "Bank C"))

        fixedDepositDao.deleteAll()

        val deposits = fixedDepositDao.getAllFixedDeposits().first()
        assertTrue(deposits.isEmpty())
    }

    @Test
    fun getTotalInvestedAmount_calculatesCorrectly() = runBlocking {
        fixedDepositDao.insertFixedDeposit(
            createTestEntity(principalAmount = 50000.0)
        )
        fixedDepositDao.insertFixedDeposit(
            createTestEntity(principalAmount = 75000.0)
        )
        fixedDepositDao.insertFixedDeposit(
            createTestEntity(principalAmount = 25000.0)
        )

        val total = fixedDepositDao.getTotalInvestedAmount().first()

        assertEquals(150000.0, total ?: 0.0, 0.01)
    }

    @Test
    fun getTotalInvestedAmount_returnsNull_whenNoData() = runBlocking {
        val total = fixedDepositDao.getTotalInvestedAmount().first()
        assertNull(total)
    }

    @Test
    fun getTotalMaturityAmount_calculatesCorrectly() = runBlocking {
        fixedDepositDao.insertFixedDeposit(
            createTestEntity(maturityAmount = 54000.0)
        )
        fixedDepositDao.insertFixedDeposit(
            createTestEntity(maturityAmount = 81000.0)
        )
        fixedDepositDao.insertFixedDeposit(
            createTestEntity(maturityAmount = 27000.0)
        )

        val total = fixedDepositDao.getTotalMaturityAmount().first()

        assertEquals(162000.0, total ?: 0.0, 0.01)
    }

    @Test
    fun getTotalMaturityAmount_returnsNull_whenNoData() = runBlocking {
        val total = fixedDepositDao.getTotalMaturityAmount().first()
        assertNull(total)
    }

    @Test
    fun insertMultipleDeposits_maintainsUniqueIds() = runBlocking {
        val id1 = fixedDepositDao.insertFixedDeposit(createTestEntity())
        val id2 = fixedDepositDao.insertFixedDeposit(createTestEntity())
        val id3 = fixedDepositDao.insertFixedDeposit(createTestEntity())

        assertNotEquals(id1, id2)
        assertNotEquals(id2, id3)
        assertNotEquals(id1, id3)
    }

    @Test
    fun getFixedDepositById_returnsNull_whenIdDoesNotExist() = runBlocking {
        val retrieved = fixedDepositDao.getFixedDepositById(999).first()
        assertNull(retrieved)
    }

    @Test
    fun updateNonExistentDeposit_doesNothing() = runBlocking {
        val entity = createTestEntity(id = 999)
        
        // This should not throw an exception
        fixedDepositDao.updateFixedDeposit(entity)

        val deposits = fixedDepositDao.getAllFixedDeposits().first()
        assertTrue(deposits.isEmpty())
    }

    @Test
    fun insertDeposit_withNullNotes_works() = runBlocking {
        val entity = createTestEntity().copy(notes = null)
        val id = fixedDepositDao.insertFixedDeposit(entity)

        val retrieved = fixedDepositDao.getFixedDepositById(id.toInt()).first()

        assertNotNull(retrieved)
        assertNull(retrieved?.notes)
    }

    @Test
    fun flowUpdates_whenDataChanges() = runBlocking {
        // Get initial empty list
        val initialDeposits = fixedDepositDao.getAllFixedDeposits().first()
        assertTrue(initialDeposits.isEmpty())

        // Insert a deposit
        fixedDepositDao.insertFixedDeposit(createTestEntity())

        // Get updated list
        val updatedDeposits = fixedDepositDao.getAllFixedDeposits().first()
        assertEquals(1, updatedDeposits.size)
    }
}
