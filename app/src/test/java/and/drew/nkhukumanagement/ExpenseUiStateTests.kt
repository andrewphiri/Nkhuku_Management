package and.drew.nkhukumanagement

import and.drew.nkhukumanagement.userinterface.accounts.calculateCumulativeExpense
import and.drew.nkhukumanagement.userinterface.accounts.calculateCumulativeExpenseUpdate
import and.drew.nkhukumanagement.userinterface.accounts.calculateTotalExpense
import junit.framework.TestCase
import org.junit.Test

class ExpenseUiStateTests {
    @Test
    fun calculateTotalExpense_test() {
        val totalExpense = calculateTotalExpense("100", "16")
        TestCase.assertEquals(1600.00, totalExpense)
    }

    @Test
    fun calculateCumulativeExpense_test() {
        val totalExpense = calculateCumulativeExpense("1000", "1600")
        TestCase.assertEquals(2600.00, totalExpense)
    }

    @Test
    fun calculateCumulativeExpenseUpdate_test() {
        val totalExpense = calculateCumulativeExpenseUpdate("1000", "1600", "100")
        TestCase.assertEquals(2500.00, totalExpense)
    }
}