package and.drew.nkhukumanagement.backupAndExport

import and.drew.nkhukumanagement.BaseFlockApplication
import and.drew.nkhukumanagement.BuildConfig
import and.drew.nkhukumanagement.data.FlockDatabase
import and.drew.nkhukumanagement.utils.Constants
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.time.LocalDateTime
import javax.inject.Inject
import androidx.core.net.toUri

@HiltViewModel
class BackupAndRestoreViewModel @Inject constructor(
    private val database: FlockDatabase,
    private val application: BaseFlockApplication,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : ViewModel() {
     private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading = _isDownloading.asStateFlow()

    private val _isUploadingSuccessful = MutableStateFlow(false)
    val isUploadingSuccessful = _isUploadingSuccessful.asStateFlow()

    private val _isDownloadingSuccessful = MutableStateFlow(false)
    val isDownloadingSuccessful = _isDownloadingSuccessful.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()


    fun backupAndShareFile() {
        try {
            //Close database
            database.close()

            val databaseName = "${LocalDateTime.now()}"

            val folder = File(application.filesDir, "PoultryManagement")
            //check if folder exists
            if (!folder.exists()) {
                folder.mkdirs()
            }
            //Copy database file
            val databaseFile =
                application.applicationContext.getDatabasePath(Constants.DATABASE_NAME)
//            val backupFile = File(
//                Environment.getExternalStorageDirectory(), "${Constants.DATABASE_NAME}_$databaseName.bak"
//            )

            val backupFile = File(
                folder, "${Constants.DATABASE_NAME}_$databaseName.bak"
            )

            databaseFile.copyTo(backupFile, true)

            //Share the backup file
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/octet-stream"
            val backupUri = FileProvider.getUriForFile(
                application.applicationContext,
                BuildConfig.APPLICATION_ID + ".fileprovider", backupFile
            )
            shareIntent.putExtra(Intent.EXTRA_STREAM, backupUri)
            application.applicationContext.startActivity(
                Intent.createChooser(
                    shareIntent,
                    "Share backup"
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun restoreBackUp(backupFileUri: Uri?) {
        //Get database file
        val databaseFile = application.applicationContext.getDatabasePath(Constants.DATABASE_NAME)

        //Read data from backup Uri
        val inputStream = backupFileUri?.let { application.contentResolver.openInputStream(it) }

        if (inputStream != null) {
            try {
                copyFiles(inputStream as FileInputStream, FileOutputStream(databaseFile))

            } catch (exception: IOException) {
                exception.printStackTrace()
            }

            val restartIntent =
                application.applicationContext.packageManager.getLaunchIntentForPackage(application.packageName)
            restartIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            application.applicationContext.startActivity(restartIntent)
        }
    }

    fun copyFiles(fromBackup: FileInputStream, toDatabase: FileOutputStream) {
        var fromChannel: FileChannel? = null
        var toChannel: FileChannel? = null
        try {
            fromChannel = fromBackup.channel
            toChannel = toDatabase.channel
            fromChannel.transferTo(0, fromChannel.size(), toChannel)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fromChannel?.close()
            } catch (e: IOException) {
            } finally {
                toChannel?.close()
            }
        }
    }

    /**
     * Check if database file is valid
     */
    fun isFileValid(uri: Uri?): Boolean {
        val resolver = application.contentResolver
        val cursor = uri?.let { resolver.query(it, null, null, null, null) }
        try {
            var fileName: String?
            //Get file name
            cursor?.moveToFirst()
            val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            fileName = nameIndex?.let { cursor.getString(it) }
            val mimeType = uri?.let { resolver.getType(it) }
           // Log.d("TAG", "isFileValid: $fileName")
          //  Log.d("TAG", "isFileValid: $mimeType")
            return  ("application/x-trash" == mimeType || "application/octet-stream" == mimeType) && fileName?.takeLast(4) == ".bak"
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            cursor?.close()
        }
    }

   suspend fun uploadFileToFirebaseStorage(uri: Uri, userId: String,) {
        try {
            val backupRef = storage.reference.child("backup/$userId/${Constants.DATABASE_NAME}")
            backupRef.putFile(uri).addOnCompleteListener {
                if (it.isSuccessful) {
                    _isUploadingSuccessful.value = true
                } else {
                    _isUploadingSuccessful.value = false
                }
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun downloadFileFromFirebaseStorage() : Uri? {
        try {
            _isDownloading.value = true
            _errorMessage.value = null
            _isDownloadingSuccessful.value = false
            val folder = File(application.filesDir, "PoultryManagement")
            //check if folder exists
            if (!folder.exists()) {
                folder.mkdirs()
            }

//            val backupFile = File(
//                Environment.getExternalStorageDirectory(), "${Constants.DATABASE_NAME}_$databaseName.bak"
//            )

            val backupFile = File(
                folder, "${Constants.DATABASE_NAME}.bak"
            )

            val backupRef = storage.reference.child("backup/${auth.currentUser?.uid}/${Constants.DATABASE_NAME}")
           // val localFile = File.createTempFile("", ".bak")
            backupRef.getFile(backupFile).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    _isDownloadingSuccessful.value = true
                } else {
                    _isDownloadingSuccessful.value = false
                }
                _isDownloading.value = false
            }.await()

         try {
             if (isDownloadingSuccessful.value) {
                 val backupUri = FileProvider.getUriForFile(
                     application.applicationContext,
                     BuildConfig.APPLICATION_ID + ".fileprovider", backupFile
                 )
                 _isDownloadingSuccessful.value = true
                 return backupUri
             } else {
                 _errorMessage.value = "Download failed"
                 _isDownloadingSuccessful.value = false
                 _isDownloading.value = false
                 return null
             }
         } catch (e: Exception) {
             e.printStackTrace()
             _errorMessage.value = "Download failed"
             _isDownloadingSuccessful.value = false
             _isDownloading.value = false
             return null
         }
        } catch (e: Exception) {
            e.printStackTrace()
            _errorMessage.value = "Download failed"
            _isDownloadingSuccessful.value = false
            _isDownloading.value = false
            _errorMessage.value = null
            return null
        } finally {
            _isDownloading.value = false
            _errorMessage.value = null
            _isDownloadingSuccessful.value = false
        }
    }

    suspend fun backupFileAndUploadToFirebaseStorage() {
        try {
            _isUploading.value = true
            _errorMessage.value = null
            //Close database
            database.close()

            val databaseName = "${LocalDateTime.now()}"
            //Copy database file
            val databaseFile =
                application.applicationContext.getDatabasePath(Constants.DATABASE_NAME)
            val backupFile = File(
                Environment.getExternalStorageDirectory(), "${Constants.DATABASE_NAME}_$databaseName.bak"
            )

            databaseFile.copyTo(backupFile, true)

            //Share the backup file
            val backupUri = FileProvider.getUriForFile(
                application.applicationContext,
                BuildConfig.APPLICATION_ID + ".fileprovider", backupFile
            )
            if (auth.currentUser?.uid != null) {
                auth.currentUser?.uid?.let { uploadFileToFirebaseStorage(backupUri,it) }
            } else {
                _errorMessage.value = "User not logged in"
            }
            _isUploading.value = false
        } catch (exception: Exception) {
            _isUploading.value = false
            _errorMessage.value = exception.message
            exception.printStackTrace()
        }
    }
}