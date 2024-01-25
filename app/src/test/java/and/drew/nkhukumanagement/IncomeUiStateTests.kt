package and.drew.nkhukumanagement

import and.drew.nkhukumanagement.userinterface.accounts.calculateCumulativeIncome
import and.drew.nkhukumanagement.userinterface.accounts.calculateCumulativeIncomeUpdate
import and.drew.nkhukumanagement.userinterface.accounts.calculateTotalIncome
import junit.framework.TestCase.assertEquals
import org.junit.Test

class IncomeUiStateTests {
    @Test
    fun calculateTotalIncome_test() {
        val totalIncome = calculateTotalIncome("100", "16")
        assertEquals(1600.00, totalIncome)
    }

    @Test
    fun calculateCumulativeIncome_test() {
        val totalIncome = calculateCumulativeIncome("1000", "1600")
        assertEquals(2600.00, totalIncome)
    }

    @Test
    fun calculateCumulativeIncomeUpdate_test() {
        val totalIncome = calculateCumulativeIncomeUpdate("1000", "1600", "100")
        assertEquals(2500.00, totalIncome)
    }
}