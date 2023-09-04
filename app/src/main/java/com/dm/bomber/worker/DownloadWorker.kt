package com.dm.bomber.worker

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dm.bomber.BuildConfig
import com.dm.bomber.ui.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class DownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val filePath = applicationContext.getExternalFilesDir("/").toString() + "update.apk"

        val request: Request = Request.Builder()
            .url(Objects.requireNonNull<String?>(inputData.getString(URL)))
            .build()

        try {
            val response = OkHttpClient()
                .newCall(request)
                .execute()

            val body = response.body
            val input = body!!.byteStream()

            val output = FileOutputStream(filePath, false)
            val dataBuffer = ByteArray(1024)

            var readBytes: Int
            var total = 0
            var lastProgress = 0

            while (input.read(dataBuffer).also { readBytes = it } != -1) {
                total += readBytes

                val progress = (total * 100 / body.contentLength()).toInt()

                if (progress != lastProgress) setProgressAsync(
                    Data.Builder()
                        .putInt(MainViewModel.KEY_PROGRESS, progress)
                        .putInt(MainViewModel.KEY_MAX_PROGRESS, 100)
                        .build()
                )

                lastProgress = progress
                output.write(dataBuffer, 0, readBytes)
            }

            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(
                FileProvider.getUriForFile(
                    applicationContext,
                    BuildConfig.APPLICATION_ID + ".provider",
                    File(filePath)
                ),
                "application/vnd.android.package-archive"
            )

            applicationContext.startActivity(intent)
            return Result.success()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Result.failure()
    }

    companion object {
        const val URL = "url"
    }
}
