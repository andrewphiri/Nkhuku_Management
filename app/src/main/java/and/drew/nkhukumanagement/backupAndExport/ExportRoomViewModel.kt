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
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Field
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ExportRoomViewModel @Inject constructor(
    private val repository: FlockRepository,
    private val baseFlockApplication: BaseFlockApplication
) : ViewModel(){

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private  val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

   suspend fun exportRoomAsExcelFileAndShare(flock: Flock) {

        try {
            _errorMessage.value = null
            _isExporting.value = true
            //Create a new workbook
            val workbook = XSSFWorkbook()
            val workbookName =
                "${flock.batchName}_${LocalDateTime.now().year}" +
                        "_${LocalDateTime.now().monthValue}" +
                        "_${LocalDateTime.now().dayOfMonth}" +
                        "_${LocalTime.now().hour}" + "${LocalTime.now().minute}.xlsx"
            //Log.d("ExportRoomViewModel", "Workbook name: $workbookName")
            val folder = File(baseFlockApplication.filesDir, "PoultryManagement")
            //check if folder exists
            if (!folder.exists()) {
                folder.mkdirs()
            }
            exportFlock(flock.uniqueId, workbook)
            exportHealth(flock.uniqueId, workbook)
            exportAccountSummary(flock.uniqueId, workbook)
            exportVaccinations(flock.uniqueId, workbook)
            exportExpense(flock.uniqueId, workbook)
            exportIncome(flock.uniqueId, workbook)
            exportFeedFields(flock.uniqueId, workbook)
            exportWeight(flock.uniqueId, workbook)

            if (flock.flockType == "Layer") {
                exportEggs(flock.uniqueId, workbook)
                exportEggSummary(flock.uniqueId, workbook)
            }

            // Write the workbook to a file in the specified folder
            val file = File(folder, workbookName)
            try {
                withContext(Dispatchers.IO) {
                    FileOutputStream(file).use { outputStream ->
                        workbook.write(outputStream)
                    }
                    //Log.d("ExportRoomViewModel", "Workbook exported successfully")
                }
                // Share the file using an Intent
                val fileUri = FileProvider.getUriForFile(
                    baseFlockApplication.applicationContext,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    file)

                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(fileUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
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
                _errorMessage.value = ("Failed to export Excel file.")
                _isExporting.value = false
                e.printStackTrace()
            }

        } catch (e: Exception) {
            _errorMessage.value = "Failed to export Excel file."
            _isExporting.value = false
            e.printStackTrace()
        }
    }

    suspend fun exportFlock(uniqueId: String, workbook: XSSFWorkbook)  {

        try {
            val sheet = workbook.createSheet("Flock Summary")

            val flockFields = arrayOf(
                "batchName",
                "flockType",
                "breed",
                "datePlaced",
                "numberOfChicksPlaced",
                "costPerBird",
                "stock",
                "donorFlock",
                "mortality",
                "culls",
                "active"
            )

            val allFlockFields = Flock::class.java.declaredFields
            val flockFieldsToExport = arrayOfNulls<Field>(flockFields.size)

            for (i in flockFields.indices) {
                for (field in allFlockFields) {
                    if (field.name == flockFields[i]) {
                        field.isAccessible = true // Make private fields accessible
                        flockFieldsToExport[i] = field
                        break
                    }
                }
            }

            //Add headers
            val headerRow = sheet.createRow(0)
            val headerStyle = createHeaderStyle(workbook)
            for (i in flockFieldsToExport.indices) {
                val cell = headerRow.createCell(i)
                cell.setCellValue(flockFieldsToExport[i]!!.name)
                cell.cellStyle = headerStyle
            }

            //Fetch flock data
            val flock = repository.getFlock(uniqueId)?.first()

            //Add flock data
            val rowNum = 1
            val row = sheet.createRow(rowNum)
            for (i in flockFieldsToExport.indices) {
                try {
                    val value = flockFieldsToExport[i]!!.get(flock)
                    row.createCell(i).setCellValue(value?.toString() ?: "")
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun exportHealth(uniqueId: String, workbook: XSSFWorkbook) {
        try {

            val sheet = workbook.createSheet("MortalityAndCulls")
            val healthFields = arrayOf(
                "mortality",
                "culls",
                "date"
            )
            val allHealthFields = FlockHealth::class.java.declaredFields
            val healthFieldsToExport = arrayOfNulls<Field>(healthFields.size)

            for (i in healthFields.indices) {
                for (field in allHealthFields) {
                    if (field.name == healthFields[i]) {
                        field.isAccessible = true // Make private fields accessible
                        healthFieldsToExport[i] = field
                        break

                    }
                }
            }

            //Add headers
            val header = sheet.createRow(0)
            val headerStyle = createHeaderStyle(workbook)
            for (i in healthFieldsToExport.indices) {
                val cell = header.createCell(i)
                cell.setCellValue(healthFieldsToExport[i]!!.name)
                cell.cellStyle = headerStyle
            }

            //Fetch health data
            val health = repository.getAllHealthForExport(uniqueId).first()

            //Add health data
            var rowNum = 1

            if (health != null) {
                for (healthData in health) {
                    val row = sheet.createRow(rowNum++)
                    for (i in healthFieldsToExport.indices) {
                        try {
                            val value = healthFieldsToExport[i]!!.get(healthData)
                            row.createCell(i).setCellValue(value?.toString() ?: "")
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun exportAccountSummary(uniqueId: String, workbook: XSSFWorkbook)  {
        try {
            val sheet = workbook.createSheet("Account Summary")
            val accountSummaryFields = arrayOf(
                "batchName",
                "totalIncome",
                "totalExpenses",
                "variance", "flockActive"
            )

            val allAccountSummaryFields = AccountsSummary::class.java.declaredFields
            val accountSummaryFieldsToExport = arrayOfNulls<Field>(accountSummaryFields.size)

            for (i in accountSummaryFields.indices) {
                for (field in allAccountSummaryFields) {
                    if (field.name == accountSummaryFields[i]) {
                        field.isAccessible = true // Make private fields accessible
                        accountSummaryFieldsToExport[i] = field
                        break

                    }
                }
            }

            //Add headers
            val header = sheet.createRow(0)
            val headerStyle = createHeaderStyle(workbook)
            for (i in accountSummaryFieldsToExport.indices) {
                val cell = header.createCell(i)
                cell.setCellValue(accountSummaryFieldsToExport[i]!!.name)
                cell.cellStyle = headerStyle
            }

            //Fetch health data
            val accountSummary = repository.getAllAccountsForExport(uniqueId).first()

            //Add health data
            val rowNum = 1

            val row = sheet.createRow(rowNum)
            for (i in accountSummaryFieldsToExport.indices) {
                try {
                    val value = accountSummaryFieldsToExport[i]!!.get(accountSummary)
                    row.createCell(i).setCellValue(value?.toString() ?: "")
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun exportFeedFields(uniqueId: String, workbook: XSSFWorkbook)  {
        try {
            val sheet = workbook.createSheet("Feed")

            val feedFields = arrayOf(
                "name",
                "week",
                "type",
                "consumed",
                "standardConsumption",
                "actualConsumptionPerBird",
                "standardConsumptionPerBird",
                "feedingDate"
            )
            val allFeedFields = Feed::class.java.declaredFields
            val feedFieldsToExport = arrayOfNulls<Field>(feedFields.size)

            for (i in feedFields.indices) {
                for (field in allFeedFields) {
                    if (field.name == feedFields[i]) {
                        field.isAccessible = true // Make private fields accessible
                        feedFieldsToExport[i] = field
                        break

                    }
                }
            }

            //Add headers
            val header = sheet.createRow(0)
            val headerStyle = createHeaderStyle(workbook)
            for (i in feedFieldsToExport.indices) {
                val cell = header.createCell(i)
                cell.setCellValue(feedFieldsToExport[i]!!.name)
                cell.cellStyle = headerStyle
            }

            //Fetch feed data
            val feed = repository.getAllFeedsForExport(uniqueId).first()

            //Add health data
            var rowNum = 1

            if (feed != null) {
                for (data in feed) {
                    val row = sheet.createRow(rowNum++)
                    for (i in feedFieldsToExport.indices) {
                        try {
                            val value = feedFieldsToExport[i]!!.get(data)
                            row.createCell(i).setCellValue(value?.toString() ?: "")
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun exportVaccinations(uniqueId: String, workbook: XSSFWorkbook) {
        try {
            val sheet = workbook.createSheet("Vaccinations")
            val vaccinationFields =
                arrayOf("name", "method", "date", "notes", "hasVaccineBeenAdministered")

            val allVaccinationFields = Vaccination::class.java.declaredFields
            val vaccinationFieldsToExport = arrayOfNulls<Field>(vaccinationFields.size)

            for (i in vaccinationFields.indices) {
                for (field in allVaccinationFields) {
                    if (field.name == vaccinationFields[i]) {
                        field.isAccessible = true // Make private fields accessible
                        vaccinationFieldsToExport[i] = field
                        break

                    }
                }
            }

            //Add headers
            val header = sheet.createRow(0)
            val headerStyle = createHeaderStyle(workbook)
            for (i in vaccinationFieldsToExport.indices) {
                val cell = header.createCell(i)
                cell.setCellValue(vaccinationFieldsToExport[i]!!.name)
                cell.cellStyle = headerStyle
            }

            //Fetch vaccination data
            val vaccinations = repository.getAllVaccinationsForExport(uniqueId).first()

            //Add health data
            var rowNum = 1

            for (data in vaccinations) {
                val row = sheet.createRow(rowNum++)
                for (i in vaccinationFieldsToExport.indices) {
                    try {
                        val value = vaccinationFieldsToExport[i]!!.get(data)
                        row.createCell(i).setCellValue(value?.toString() ?: "")
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun exportWeight(uniqueId: String, workbook: XSSFWorkbook)  {
        try {
            val sheet = workbook.createSheet("Weight")
            val weightFields = arrayOf(
                "week",
                "expectedWeight",
                "weight",
                "measuredDate"
            )

            val allWeightFields = Weight::class.java.declaredFields
            val weightFieldsToExport = arrayOfNulls<Field>(weightFields.size)

            for (i in weightFields.indices) {
                for (field in allWeightFields) {
                    if (field.name == weightFields[i]) {
                        field.isAccessible = true // Make private fields accessible
                        weightFieldsToExport[i] = field
                        break

                    }
                }
            }

            //Add headers
            val header = sheet.createRow(0)
            val headerStyle = createHeaderStyle(workbook)
            for (i in weightFieldsToExport.indices) {
                val cell = header.createCell(i)
                cell.setCellValue(weightFieldsToExport[i]!!.name)
                cell.cellStyle = headerStyle
            }

            //Fetch weight data
            val weight = repository.getAllWeightsForExport(uniqueId).first()

            //Add health data
            var rowNum = 1

            if (weight != null) {
                for (data in weight) {
                    val row = sheet.createRow(rowNum++)
                    for (i in weightFieldsToExport.indices) {
                        try {
                            val value = weightFieldsToExport[i]!!.get(data)
                            row.createCell(i).setCellValue(value?.toString() ?: "")
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun exportEggs(uniqueId: String, workbook: XSSFWorkbook)  {
        try {
            val sheet = workbook.createSheet("Eggs")

            val eggFields = arrayOf(
                "date",
                "flockUniqueId",
                "goodEggs",
                "badEggs"
            )

            val allEggFields = Eggs::class.java.declaredFields
            val eggFieldsToExport = arrayOfNulls<Field>(eggFields.size)

            for (i in eggFields.indices) {
                for (field in allEggFields) {
                    if (field.name == eggFields[i]) {
                        field.isAccessible = true // Make private fields accessible
                        eggFieldsToExport[i] = field
                        break

                    }
                }
            }

            //Add headers
            val header = sheet.createRow(0)
            val headerStyle = createHeaderStyle(workbook)
            for (i in eggFieldsToExport.indices) {
                val cell = header.createCell(i)
                cell.setCellValue(eggFieldsToExport[i]!!.name)
                cell.cellStyle = headerStyle
            }

            //Fetch egs data
            val eggs = repository.getAllEggsForExport(uniqueId).first()

            //Add health data
            var rowNum = 1

            for (egg in eggs) {
                val row = sheet.createRow(rowNum++)
                for (i in eggFieldsToExport.indices) {
                    try {
                        val value = eggFieldsToExport[i]!!.get(egg)
                        row.createCell(i).setCellValue(value?.toString() ?: "")
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun exportEggSummary(uniqueId: String, workbook: XSSFWorkbook) {
        try {
            val sheet = workbook.createSheet("Egg Summary")
            val eggFields = arrayOf(
                "totalGoodEggs",
                "totalBadEggs",
                "date"
            )
            val allEggFields = EggsSummary::class.java.declaredFields
            val eggFieldsToExport = arrayOfNulls<Field>(eggFields.size)

            for (i in eggFields.indices) {
                for (field in allEggFields) {
                    if (field.name == eggFields[i]) {
                        field.isAccessible = true // Make private fields accessible
                        eggFieldsToExport[i] = field
                        break

                    }
                }
            }

            //Add headers
            val header = sheet.createRow(0)
            val headerStyle = createHeaderStyle(workbook)
            for (i in eggFieldsToExport.indices) {
                val cell = header.createCell(i)
                cell.setCellValue(eggFieldsToExport[i]!!.name)
                cell.cellStyle = headerStyle
            }

            //Fetch health data
            val eggsSummary = repository.getAllEggsSummaryForExport(uniqueId).first()

            //Add health data
            val rowNum = 1

            val row = sheet.createRow(rowNum)
            for (i in eggFieldsToExport.indices) {
                try {
                    val value = eggFieldsToExport[i]!!.get(eggsSummary)
                    row.createCell(i).setCellValue(value?.toString() ?: "")
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun exportExpense(uniqueId: String, workbook: XSSFWorkbook)  {
        try {
            val sheet = workbook.createSheet("Expenses")
            val expenseFields = arrayOf(
                "date",
                "expenseName",
                "supplier",
                "costPerItem", "quantity",
                "totalExpense",
                "cumulativeTotalExpense",
                "notes"
            )

            val allExpenseFields = Expense::class.java.declaredFields
            val expenseFieldsToExport = arrayOfNulls<Field>(expenseFields.size)

            for (i in expenseFields.indices) {
                for (field in allExpenseFields) {
                    if (field.name == expenseFields[i]) {
                        field.isAccessible = true // Make private fields accessible
                        expenseFieldsToExport[i] = field
                        break

                    }
                }
            }

            //Add headers
            val header = sheet.createRow(0)
            val headerStyle = createHeaderStyle(workbook)
            for (i in expenseFieldsToExport.indices) {
                val cell = header.createCell(i)
                cell.setCellValue(expenseFieldsToExport[i]!!.name)
                cell.cellStyle = headerStyle
            }

            //Fetch health data
            val expenses = repository.getAllExpensesForExport(uniqueId).first()

            //Add health data
            var rowNum = 1

            if (expenses != null) {
                for (expense in expenses) {
                    val row = sheet.createRow(rowNum++)
                    for (i in expenseFieldsToExport.indices) {
                        try {
                            val value = expenseFieldsToExport[i]!!.get(expense)
                            row.createCell(i).setCellValue(value?.toString() ?: "")
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun exportIncome(uniqueId: String, workbook: XSSFWorkbook) {
        try {
            val sheet = workbook.createSheet("Income")
            val incomeFields = arrayOf(
                "date",
                "incomeName",
                "incomeType",
                "customer", "pricePerItem",
                "quantity",
                "totalIncome",
                "cumulativeTotalIncome",
                "notes"
            )

            val allIncomeFields = Income::class.java.declaredFields
            val incomeFieldsToExport = arrayOfNulls<Field>(incomeFields.size)

            for (i in incomeFields.indices) {
                for (field in allIncomeFields) {
                    if (field.name == incomeFields[i]) {
                        field.isAccessible = true // Make private fields accessible
                        incomeFieldsToExport[i] = field
                        break

                    }
                }
            }

            //Add headers
            val header = sheet.createRow(0)
            val headerStyle = createHeaderStyle(workbook)
            for (i in incomeFieldsToExport.indices) {
                val cell = header.createCell(i)
                cell.setCellValue(incomeFieldsToExport[i]!!.name)
                cell.cellStyle = headerStyle
            }

            //Fetch income data
            val income = repository.getAllIncomeForExport(uniqueId).first()

            //Add health data
            var rowNum = 1

            if (income != null) {
                for (incomeData in income) {
                    val row = sheet.createRow(rowNum++)
                    for (i in incomeFieldsToExport.indices) {
                        try {
                            val value = incomeFieldsToExport[i]!!.get(incomeData)
                            row.createCell(i).setCellValue(value?.toString() ?: "")
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createHeaderStyle(workbook: XSSFWorkbook): XSSFCellStyle {
        // Create a new cell style for the header
        val headerStyle: XSSFCellStyle = workbook.createCellStyle()

        // Set background color
        headerStyle.fillForegroundColor = IndexedColors.LIGHT_BLUE.index
        headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

        // Set font color and make it bold
        val font: XSSFFont = workbook.createFont()
        font.bold = true
        font.color = IndexedColors.WHITE.index
        headerStyle.setFont(font)

        // Set alignment
        headerStyle.alignment = HorizontalAlignment.CENTER
        headerStyle.verticalAlignment = VerticalAlignment.CENTER

        return headerStyle
    }
}