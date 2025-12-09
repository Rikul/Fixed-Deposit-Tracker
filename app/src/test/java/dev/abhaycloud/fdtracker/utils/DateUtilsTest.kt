package dev.abhaycloud.fdtracker.utils

import dev.abhaycloud.fdtracker.utils.DateUtils.getDifferenceBetweenDays
import dev.abhaycloud.fdtracker.utils.DateUtils.isAfter
import dev.abhaycloud.fdtracker.utils.DateUtils.isBefore
import dev.abhaycloud.fdtracker.utils.DateUtils.toDateString
import dev.abhaycloud.fdtracker.utils.DateUtils.toLocalDate
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.Date

/**
 * Unit tests for DateUtils
 * Tests date formatting, comparison, and calculation functions
 */
class DateUtilsTest {

    @Test
    fun toDateString_formatsCorrectly() {
        // Create a specific date: January 15, 2024
        val calendar = Calendar.getInstance()
        calendar.set(2024, Calendar.JANUARY, 15, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val timestamp = calendar.timeInMillis

        val dateString = timestamp.toDateString()
        
        // Should format as "January 15, 2024"
        assertTrue(dateString.contains("January"))
        assertTrue(dateString.contains("15"))
        assertTrue(dateString.contains("2024"))
    }

    @Test
    fun getDifferenceBetweenDays_calculatesCorrectly() {
        val calendar = Calendar.getInstance()
        
        // Set first date: January 1, 2024
        calendar.set(2024, Calendar.JANUARY, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val date1 = calendar.timeInMillis
        
        // Set second date: January 11, 2024 (10 days later)
        calendar.set(2024, Calendar.JANUARY, 11, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val date2 = calendar.timeInMillis
        
        val difference = date2.getDifferenceBetweenDays(date1)
        
        assertEquals(10, difference)
    }


    @Test
    fun isBefore_returnsTrue_whenFirstDateIsEarlier() {
        val calendar = Calendar.getInstance()
        
        // Set first date: January 1, 2024
        calendar.set(2024, Calendar.JANUARY, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val earlierDate = calendar.timeInMillis
        
        // Set second date: January 15, 2024
        calendar.set(2024, Calendar.JANUARY, 15, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val laterDate = calendar.timeInMillis
        
        assertTrue(earlierDate isBefore laterDate)
    }

    @Test
    fun isBefore_returnsFalse_whenFirstDateIsLater() {
        val calendar = Calendar.getInstance()
        
        // Set first date: January 15, 2024
        calendar.set(2024, Calendar.JANUARY, 15, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val laterDate = calendar.timeInMillis
        
        // Set second date: January 1, 2024
        calendar.set(2024, Calendar.JANUARY, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val earlierDate = calendar.timeInMillis
        
        assertFalse(laterDate isBefore earlierDate)
    }

    @Test
    fun isAfter_returnsTrue_whenFirstDateIsLater() {
        val calendar = Calendar.getInstance()
        
        // Set first date: January 15, 2024
        calendar.set(2024, Calendar.JANUARY, 15, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val laterDate = calendar.timeInMillis
        
        // Set second date: January 1, 2024
        calendar.set(2024, Calendar.JANUARY, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val earlierDate = calendar.timeInMillis
        
        assertTrue(laterDate isAfter earlierDate)
    }

    @Test
    fun isAfter_returnsFalse_whenFirstDateIsEarlier() {
        val calendar = Calendar.getInstance()
        
        // Set first date: January 1, 2024
        calendar.set(2024, Calendar.JANUARY, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val earlierDate = calendar.timeInMillis
        
        // Set second date: January 15, 2024
        calendar.set(2024, Calendar.JANUARY, 15, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val laterDate = calendar.timeInMillis
        
        assertFalse(earlierDate isAfter laterDate)
    }

    @Test
    fun toLocalDate_convertsCorrectly() {
        // Create a date: January 15, 2024
        val calendar = Calendar.getInstance()
        calendar.set(2024, Calendar.JANUARY, 15, 12, 30, 0)
        val date = calendar.time
        
        val localDate = date.toLocalDate()
        
        assertEquals(2024, localDate.year)
        assertEquals(1, localDate.monthValue) // January is 1 in LocalDate
        assertEquals(15, localDate.dayOfMonth)
    }

    @Test
    fun getDifferenceBetweenDays_handlesZeroDifference() {
        val calendar = Calendar.getInstance()
        calendar.set(2024, Calendar.JANUARY, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val date = calendar.timeInMillis
        
        val difference = date.getDifferenceBetweenDays(date)
        
        assertEquals(0, difference)
    }
}
