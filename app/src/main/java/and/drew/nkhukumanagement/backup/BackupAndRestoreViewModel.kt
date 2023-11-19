package and.drew.nkhukumanagement.backup

import and.drew.nkhukumanagement.BuildConfig
import and.drew.nkhukumanagement.FlockApplication
import and.drew.nkhukumanagement.data.FlockDatabase
import and.drew.nkhukumanagement.utils.Constants
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import javax.inject.Inject

@HiltViewModel
class BackupAndRestoreViewModel @Inject constructor(
    private val database: FlockDatabase,
    private val application: FlockApplication
) : ViewModel() {

    fun backupAndShareFile() {
        try {
            //Close database
            database.close()

            //Copy database file
            val databaseFile =
                application.applicationContext.getDatabasePath(Constants.DATABASE_NAME)
            val backupFile = File(
                Environment.getExternalStorageDirectory(), "${Constants.DATABASE_NAME}.bak"
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
            return "application/octet-stream" == mimeType && fileName == "${Constants.DATABASE_NAME}.bak"
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            cursor?.close()
        }
    }
}