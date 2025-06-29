package and.drew.nkhukumanagement.backupAndExport

import and.drew.nkhukumanagement.BaseFlockApplication
import and.drew.nkhukumanagement.BuildConfig
import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.data.Eggs
import and.drew.nkhukumanagement.data.EggsSummary
import and.drew.nkhukumanagement.data.Expense
import and.drew.nkhukumanagement.data.Feed
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.data.FlockHealth
import and.drew.nkhukumanagement.data.FlockRepository
import and.drew.nkhukumanagement.data.Income
import and.drew.nkhukumanagement.data.Vaccination
import and.drew.nkhukumanagement.data.Weight
import and.drew.nkhukumanagement.utils.formatConsumption
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ExportRoomAsPDFViewModel @Inject constructor(
    private val repository: FlockRepository,
    private val baseFlockApplication: BaseFlockApplication
) : ViewModel() {

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private  val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


    suspend fun exportRoomAsPDFAndShare(flock: Flock, unitPreference: String) {
        val pageWidth = 842 // A4 in points
        val pageHeight = 595
        val margin = 40
        val lineHeight = 20
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
        }

     try {
         _isExporting.value = true
         _errorMessage.value = null
         val pdfName =
             "${flock.batchName}_${LocalDateTime.now().year}" +
                     "_${LocalDateTime.now().monthValue}" +
                     "_${LocalDateTime.now().dayOfMonth}" +
                     "_${LocalTime.now().hour}" + "${LocalTime.now().minute}.pdf"

         val folder = File(baseFlockApplication.filesDir, "PoultryManagement")
         //check if folder exists
         if (!folder.exists()) {
             folder.mkdirs()
         }

         val pdfDocument = PdfDocument()
         var pageNumber = 1
         var yPosition = margin
         //var page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
         //var canvas = page.canvas

         suspend fun drawSectionSafely(section: suspend () -> Triple<String, List<String>, List<List<String>>>) {
             val (title, headers, rows) = section()
             if (title.isNotBlank() && rows.isNotEmpty()) {
                 var currentRowIndex = 0
                 val maxRowsPerPage = (pageHeight - 2 * margin - lineHeight * 3) / lineHeight // room for title + headers

                 while (currentRowIndex < rows.size) {
                     val page = pdfDocument.startPage(
                         PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
                     )
                     val canvas = page.canvas
                     val rowsForThisPage = rows.subList(
                         currentRowIndex,
                         minOf(currentRowIndex + maxRowsPerPage, rows.size)
                     )

                     drawSection(
                         canvas,
                         if (currentRowIndex == 0) title else "$title (cont'd)",
                         headers,
                         rowsForThisPage,
                         pageWidth,
                         pageHeight,
                         margin,
                         lineHeight,
                         margin,
                         paint
                     )
                     pdfDocument.finishPage(page)
                     currentRowIndex += maxRowsPerPage
                 }
             }
         }



         drawSectionSafely { exportFlockSummaryToPdfSection(flock) }
         drawSectionSafely { exportAccountSummaryToPdfSection(flock.uniqueId) }
         drawSectionSafely { exportHealthToPdfSection(flock.uniqueId) }
         drawSectionSafely { exportWeightToPdfSection(flock.uniqueId, unitPreference) }
         drawSectionSafely { exportFeedToPdfSection(flock.uniqueId, unitPreference) }
         drawSectionSafely { exportVaccinationToPdfSection(flock.uniqueId) }
         drawSectionSafely { exportExpenseToPdfSection(flock.uniqueId) }
         drawSectionSafely { exportIncomeToPdfSection(flock.uniqueId) }

         if (flock.flockType == "Layer") {
             drawSectionSafely { exportEggSummaryToPdfSection(flock.uniqueId) }
             drawSectionSafely { exportEggsToPdfSection(flock.uniqueId) }
         }


         val file = File(folder, pdfName)

         try {
             withContext(Dispatchers.IO) {
                 FileOutputStream(file).use { outputStream ->
                     pdfDocument.writeTo(FileOutputStream(file))
                     pdfDocument.close()
                 }
                 //Log.d("ExportRoomViewModel", "Workbook exported successfully")
             }
             // Share the file using an Intent
             val fileUri = FileProvider.getUriForFile(
                 baseFlockApplication.applicationContext,
                 BuildConfig.APPLICATION_ID + ".fileprovider",
                 file)

             val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                 setDataAndType(fileUri, "application/pdf")
                 addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
             }

             val shareIntent = Intent(Intent.ACTION_SEND).apply {
                 type = "application/pdf"
                 putExtra(Intent.EXTRA_STREAM, fileUri)
                 addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
             }

             val chooserIntent =
                 Intent.createChooser(
                     shareIntent,
                     "Open or Share File"
                 ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)


             // Add the viewIntent as an extra option
             chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(viewIntent))

             baseFlockApplication.startActivity(chooserIntent)
             _isExporting.value = false
         } catch (e: Exception) {
             _errorMessage.value = ("Failed to export pdf file.")
             _isExporting.value = false
             e.printStackTrace()
         }
     } catch (e: Exception) {
         _errorMessage.value = ("Failed to export pdf file.")
         _isExporting.value = false
         e.printStackTrace()
     }
}

    fun drawSection(
        canvas: Canvas,
        title: String,
        headers: List<String>,
        rows: List<List<String>>,
        pageWidth: Int,
        pageHeight: Int,
        margin: Int,
        lineHeight: Int,
        yStart: Int,
        paint: Paint
    ): Pair<Int, Canvas> {
        var yPosition = yStart
        val colWidth = (pageWidth - 2 * margin) / headers.size

        // Draw title
        paint.isFakeBoldText = true
        canvas.drawText(title, margin.toFloat(), yPosition.toFloat(), paint)
        yPosition += lineHeight

        // Draw headers
        headers.forEachIndexed { index, header ->
            canvas.drawText(header, (margin + index * colWidth).toFloat(), yPosition.toFloat(), paint)
        }
        yPosition += lineHeight
        paint.isFakeBoldText = false

        // Draw rows
        for (row in rows) {
            if (yPosition + lineHeight > pageHeight - margin) {
                break // Page overflow; should be handled by calling code
            }
            row.forEachIndexed { index, cell ->
                canvas.drawText(cell, (margin + index * colWidth).toFloat(), yPosition.toFloat(), paint)
            }
            yPosition += lineHeight
        }
        yPosition += lineHeight
        return Pair(yPosition, canvas)
    }

    suspend fun exportExpenseToPdfSection(
        uniqueId: String,
    ): Triple<String, List<String>, List<List<String>>> {
        val headers = listOf(
            "Date", "Name", "Supplier", "Cost/Item", "Quantity",
            "Total Expense", "Cumulative", "Notes"
        )

        val expensesList = repository.getAllExpensesForExport(uniqueId).firstOrNull() ?: return Triple("", headers, emptyList())
        if (expensesList.isEmpty()) return Triple("", headers, emptyList())

        val rows = expensesList.map { expense ->
            listOf(
                expense.date.toString(),
                expense.expenseName,
                expense.supplier,
                expense.costPerItem.toString(),
                expense.quantity.toString(),
                expense.totalExpense.toString(),
                expense.cumulativeTotalExpense.toString(),
                expense.notes
            )
        }

        return Triple("Expenses", headers, rows)
    }

    suspend fun exportIncomeToPdfSection(
        uniqueId: String,

    ): Triple<String, List<String>, List<List<String>>> {
        val headers = listOf(
            "Date", "Name", "Type", "Customer", "Price/Item",
            "Quantity", "Total Income", "Cumulative", "Notes"
        )

        val incomeList = repository.getAllIncomeForExport(uniqueId).firstOrNull() ?: return Triple("", headers, emptyList())
        if (incomeList.isEmpty()) return Triple("", headers, emptyList())

        val rows = incomeList.map { income ->
            listOf(
                income.date.toString(),
                income.incomeName,
                income.incomeType,
                income.customer,
                income.pricePerItem.toString(),
                income.quantity.toString(),
                income.totalIncome.toString(),
                income.cumulativeTotalIncome.toString(),
                income.notes
            )
        }

        return Triple("Income", headers, rows)
    }

    suspend fun exportFlockSummaryToPdfSection(
        flock: Flock
    ): Triple<String, List<String>, List<List<String>>> {
        val headers = listOf(
            "Batch Name", "Type", "Breed", "Date",
            "Quantity", "Cost/Bird", "Stock",
            "Donor", "Mortality", "Culls", "Active"
        )

        val row = listOf(
            flock.batchName,
            flock.flockType,
            flock.breed,
            flock.datePlaced.toString(),
            flock.numberOfChicksPlaced.toString(),
            flock.costPerBird.toString(),
            flock.stock.toString(),
            flock.donorFlock.toString(),
            flock.mortality.toString(),
            flock.culls.toString(),
            flock.active.toString()
        )

        return Triple("Flock", headers, listOf(row))
    }

    suspend fun exportVaccinationToPdfSection(
        uniqueId: String,
    ): Triple<String, List<String>, List<List<String>>> {
        val headers = listOf("Name", "Method", "Date", "Notes", "Administered")
        val vaccinationList = repository.getAllVaccinationsForExport(uniqueId).firstOrNull() ?: return Triple("", headers, emptyList())
        if (vaccinationList.isEmpty()) return Triple("", headers, emptyList())

        val rows = vaccinationList.map { v ->
            listOf(
                v.name,
                v.method,
                v.date.toString(),
                v.notes,
                v.hasVaccineBeenAdministered.toString()
            )
        }

        return Triple("Vaccinations", headers, rows)
    }

    suspend fun exportFeedToPdfSection(
        uniqueId: String,
        unitPreference: String,
    ): Triple<String, List<String>, List<List<String>>> {
        val unitPref = if (unitPreference == "Kilogram (Kg)") "kg" else if
                (unitPreference == "Pound (lb)") "lb" else if
                        (unitPreference == "Ounce (oz)") "oz" else "g"
        val headers = listOf(
            "Name", "Week", "Type", "Consumed", "Standard",
            "Actual/Bird", "Standard/Bird", "Date"
        )

        val feedList = repository.getAllFeedsForExport(uniqueId).firstOrNull() ?: return Triple("", headers, emptyList())
        if (feedList.isEmpty()) return Triple("", headers, emptyList())

        val rows = feedList.map { f ->
            listOf(
                f.name,
                f.week.toString(),
                f.type,
                formatConsumption(f.consumed, unitPreference),
                formatConsumption(f.standardConsumption, unitPreference),
                formatConsumption(f.actualConsumptionPerBird, unitPreference),
                formatConsumption(f.standardConsumptionPerBird, unitPreference),
                f.feedingDate.toString()
            )
        }

        return Triple("Feed (${unitPref})", headers, rows)
    }

    suspend fun exportHealthToPdfSection(
        uniqueId: String,
    ): Triple<String, List<String>, List<List<String>>> {
        val headers = listOf("Mortality", "Culls", "Date")
        val healthList = repository.getAllHealthForExport(uniqueId).firstOrNull() ?: return Triple("", headers, emptyList())
        if (healthList.isEmpty()) return Triple("", headers, emptyList())

        val rows = healthList.map { h ->
            listOf(
                h.mortality.toString(),
                h.culls.toString(),
                h.date.toString()
            )
        }

        return Triple("Health", headers, rows)
    }

    suspend fun exportEggsToPdfSection(
        uniqueId: String,
    ): Triple<String, List<String>, List<List<String>>> {
        val headers = listOf("Date", "UniqueId", "Good Eggs", "Bad Eggs")
        val eggList = repository.getAllEggsForExport(uniqueId).firstOrNull() ?: return Triple("", headers, emptyList())
        if (eggList.isEmpty()) return Triple("", headers, emptyList())

        val rows = eggList.map { e ->
            listOf(
                e.date.toString(),
                e.flockUniqueId,
                e.goodEggs.toString(),
                e.badEggs.toString()
            )
        }

        return Triple("Eggs", headers, rows)
    }

    suspend fun exportEggSummaryToPdfSection(
        uniqueId: String,

    ): Triple<String, List<String>, List<List<String>>> {
        val headers = listOf("Total Good Eggs", "Total Bad Eggs", "Date")
        val eggSummary = repository.getAllEggsSummaryForExport(uniqueId).firstOrNull() ?: return Triple("", headers, emptyList())

        val row = listOf(
            eggSummary.totalGoodEggs.toString(),
            eggSummary.totalBadEggs.toString(),
            eggSummary.date.toString()
        )

        return Triple("Egg Summary", headers, listOf(row))
    }


    suspend fun exportAccountSummaryToPdfSection(
        uniqueId: String,
    ): Triple<String, List<String>, List<List<String>>> {
        val headers = listOf("Batch Name", "Total Income", "Total Expenses", "Variance", "Active")
        val accountSummary = repository.getAllAccountsForExport(uniqueId).firstOrNull() ?: return Triple("", headers, emptyList())

        val row = listOf(
            accountSummary.batchName,
            accountSummary.totalIncome.toString(),
            accountSummary.totalExpenses.toString(),
            accountSummary.variance.toString(),
            accountSummary.flockActive.toString()
        )

        return Triple("Account Summary", headers, listOf(row))
    }

    suspend fun exportWeightToPdfSection(
        uniqueId: String,
        unitPreference: String,
    ): Triple<String, List<String>, List<List<String>>> {
        val unitPref = if (unitPreference == "Kilogram (Kg)") "kg" else if
                (unitPreference == "Pound (lb)") "lb" else if
                        (unitPreference == "Ounce (oz)") "oz" else "g"
        val headers = listOf("Week", "Expected Weight($unitPref)", "Actual Weight ($unitPref)", "Measured Date")
        val weightList = repository.getAllWeightsForExport(uniqueId).firstOrNull() ?: return Triple("", headers, emptyList())
        if (weightList.isEmpty()) return Triple("", headers, emptyList())

        val rows = weightList.map { w ->
            listOf(
                w.week.toString(),
                formatConsumption(w.expectedWeight, unitPreference),
                formatConsumption(w.weight, unitPreference),
                w.measuredDate.toString()
            )
        }

        return Triple("Weight", headers, rows)
    }

}