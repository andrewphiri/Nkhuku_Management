package and.drew.nkhukumanagement.data


import and.drew.nkhukumanagement.utils.DateConverter
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Database class with a singleton INSTANCE Object
 */
@Database(
    entities = [Flock::class, Vaccination::class, Feed::class, Weight::class,
        FlockHealth::class, Income::class, Expense::class, AccountsSummary::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class FlockDatabase : RoomDatabase() {

    abstract fun flockDao(): FlockDao

}