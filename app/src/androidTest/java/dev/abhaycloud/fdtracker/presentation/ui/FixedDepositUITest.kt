package dev.abhaycloud.fdtracker.presentation.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.abhaycloud.fdtracker.domain.model.FixedDeposit
import dev.abhaycloud.fdtracker.presentation.ui.components.FixedDepositItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar
import java.util.Date
import androidx.compose.ui.Modifier

/**
 * Instrumented UI tests for Compose components
 * Tests UI rendering and interactions
 */
@RunWith(AndroidJUnit4::class)
class FixedDepositUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

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

    @Test
    fun fixedDepositItem_displaysCorrectBankName() {
        val deposit = createTestFixedDeposit(bankName = "HDFC Bank")

        composeTestRule.setContent {
            FixedDepositItem(
                fixedDeposit = deposit,
                onClick = {},
                modifier = Modifier
            )
        }

        composeTestRule
            .onNodeWithText("HDFC Bank")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun fixedDepositItem_displaysPrincipalAmount() {
        val deposit = createTestFixedDeposit(principalAmount = 125000.0)

        composeTestRule.setContent {
            FixedDepositItem(
                fixedDeposit = deposit,
                onClick = {},
                modifier = Modifier
            )
        }

        // Check if amount is displayed (format may vary)
        composeTestRule
            .onNodeWithText("1,25,000", substring = true)
            .assertExists()
    }

    @Test
    fun fixedDepositItem_displaysMaturityAmount() {
        val deposit = createTestFixedDeposit(maturityAmount = 135000.0)

        composeTestRule.setContent {
            FixedDepositItem(
                fixedDeposit = deposit,
                onClick = {},
                modifier = Modifier
            )
        }


        composeTestRule.onRoot().printToLog("UI_TEST")

        composeTestRule
            .onNodeWithText("1,35,000", substring = true)
            .assertExists()
    }

    @Test
    fun fixedDepositItem_displaysInterestRate() {
        val deposit = createTestFixedDeposit().copy(interestRate = 7.5)

        composeTestRule.setContent {
            FixedDepositItem(
                fixedDeposit = deposit,
                onClick = {},
                modifier = Modifier
            )
        }

        composeTestRule
            .onNodeWithText("7.5", substring = true)
            .assertExists()
    }

    @Test
    fun fixedDepositItem_clickTriggersCallback() {
        val deposit = createTestFixedDeposit()
        var clickCount = 0

        composeTestRule.setContent {
            FixedDepositItem(
                fixedDeposit = deposit,
                onClick = { clickCount++ },
                modifier = Modifier
            )
        }

        composeTestRule
            .onNodeWithText("Test Bank")
            .performClick()

        assert(clickCount == 1)
    }

    @Test
    fun fixedDepositItem_displaysMultipleDeposits() {
        val deposits = listOf(
            createTestFixedDeposit(id = 1, bankName = "Bank A"),
            createTestFixedDeposit(id = 2, bankName = "Bank B"),
            createTestFixedDeposit(id = 3, bankName = "Bank C")
        )

        composeTestRule.setContent {
            androidx.compose.foundation.lazy.LazyColumn {
                items(deposits.size) { index ->
                    FixedDepositItem(
                        fixedDeposit = deposits[index],
                        onClick = {},
                        modifier = Modifier
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Bank A").assertExists()
        composeTestRule.onNodeWithText("Bank B").assertExists()
        composeTestRule.onNodeWithText("Bank C").assertExists()
    }

    @Test
    fun fixedDepositItem_withLongBankName_displays() {
        val deposit = createTestFixedDeposit(
            bankName = "Very Long Bank Name That Should Still Display Properly"
        )

        composeTestRule.setContent {
            FixedDepositItem(
                fixedDeposit = deposit,
                onClick = {},
                modifier = Modifier
            )
        }

        composeTestRule
            .onNodeWithText("Very Long Bank Name That Should Still Display Properly")
            .assertExists()
    }

    @Test
    fun fixedDepositItem_withZeroAmounts_displays() {
        val deposit = createTestFixedDeposit(
            principalAmount = 0.0,
            maturityAmount = 0.0
        )

        composeTestRule.setContent {
            FixedDepositItem(
                fixedDeposit = deposit,
                onClick = {},
                modifier = Modifier
            )
        }

        // Should still display the component without crashing
        composeTestRule
            .onNodeWithText("Test Bank")
            .assertExists()
    }

    @Test
    fun fixedDepositItem_withVeryLargeAmounts_displays() {
        val deposit = createTestFixedDeposit(
            principalAmount = 99999999.99,
            maturityAmount = 108999999.99
        )

        composeTestRule.setContent {
            FixedDepositItem(
                fixedDeposit = deposit,
                onClick = {},
                modifier = Modifier
            )
        }

        // Should handle large numbers without crashing
        composeTestRule
            .onNodeWithText("Test Bank")
            .assertExists()
    }

}
