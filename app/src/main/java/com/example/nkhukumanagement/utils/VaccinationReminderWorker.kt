package com.example.nkhukumanagement.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.nkhukumanagement.data.Vaccination

class VaccinationReminderWorker(
    context: Context,
    val vaccination: Vaccination,
    val batchName: String,
    params: WorkerParameters
) : Worker(context, params) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val appContext = applicationContext
        NotificationUtil().createNotification(
            context = appContext,
            title = "Vaccination Reminder",
            contentText = "${vaccination.name} vaccination due.",
            bigText =
            "${vaccination.name} vaccination for $batchName is due tomorrow, " +
                    "${DateUtils().dateToStringLongFormat(vaccination.date)}."

        )
        return Result.success()
    }
}