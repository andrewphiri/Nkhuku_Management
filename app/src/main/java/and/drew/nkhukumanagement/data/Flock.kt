package and.drew.nkhukumanagement.data

import and.drew.nkhukumanagement.R
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Data class that represent the a table in the database.
 */
@Entity(tableName = "flock")
data class Flock(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val uniqueId: String,
    val batchName: String,
    val flockType: String,
    val breed: String,
    @ColumnInfo(name = "Date Received")
    val datePlaced: LocalDate,
    @ColumnInfo(name = "Quantity")
    val numberOfChicksPlaced: Int,
    @ColumnInfo(name = "Price/bird")
    val costPerBird: Double,
    val stock: Int,
    @ColumnInfo(name = "Donor")
    val donorFlock: Int,
    val mortality: Int,
    val imageResourceId: Int = R.drawable.add_flock_placeholder,
    @ColumnInfo(name = "Deformities")
    val culls: Int,
    val active: Boolean = true,
)
