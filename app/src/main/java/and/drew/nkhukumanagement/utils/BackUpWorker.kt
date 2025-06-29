package and.drew.nkhukumanagement.utils

import and.drew.nkhukumanagement.BuildConfig
import and.drew.nkhukumanagement.data.FlockDatabase
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.work.HiltWorker
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject

@HiltWorker
class BackUpWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    var INSTANCE: FlockDatabase? = null
    @Inject
    lateinit var storage: FirebaseStorage
    @Inject
    lateinit var auth: FirebaseAuth

    companion object {
        @Volatile
        private var INSTANCE: FlockDatabase? = null

        fun getDatabase(context: Context): FlockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext, // Use application context
                    FlockDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private val database: FlockDatabase by lazy { getDatabase(appContext) }

    override suspend fun doWork(): Result {

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                return try {
                    backupFileAndUploadToFirebaseStorage()
                    Result.success()
                } catch (e: Exception) {
                    Result.failure()
                }
            }
            else -> {
                return Result.failure()
            }
        }
    }

    suspend fun backupFileAndUploadToFirebaseStorage() {
        try {

            //Close database
            database.close()

            val folder = File(appContext.applicationContext.filesDir, "PoultryManagement")
            //check if folder exists
            if (!folder.exists()) {
                folder.mkdirs()
            }

            val databaseName = "${LocalDateTime.now()}"
            //Copy database file
            val databaseFile =
                appContext.applicationContext.getDatabasePath(Constants.DATABASE_NAME)
//            val backupFile = File(
//                Environment.getExternalStorageDirectory(), "${Constants.DATABASE_NAME}_$databaseName.bak"
//            )

            val backupFile = File(
                folder, "${Constants.DATABASE_NAME}_$databaseName.bak"
            )

            databaseFile.copyTo(backupFile, true)

            //Share the backup file
            val backupUri = FileProvider.getUriForFile(
                appContext.applicationContext,
                BuildConfig.APPLICATION_ID + ".fileprovider", backupFile
            )
            if (auth.currentUser?.uid != null) {
                auth.currentUser?.uid?.let { uploadFileToFirebaseStorage(backupUri,it) }
            } else {
                throw Exception("User not logged in")
            }
        } catch (exception: Exception) {

            exception.printStackTrace()
        }
    }

    suspend fun uploadFileToFirebaseStorage(uri: Uri, userId: String,) {
        try {
            val backupRef = storage.reference.child("backup/$userId/${Constants.DATABASE_NAME}.bak")
            backupRef.putFile(uri).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}