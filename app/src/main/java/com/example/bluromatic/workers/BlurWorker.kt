package com.example.bluromatic.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bluromatic.DELAY_TIME_MILLIS
import com.example.bluromatic.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "BlurWorker"

class BlurWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        makeStatusNotification(
            applicationContext.resources.getString(R.string.blurring_image),
            applicationContext
        )

        //by adding withContext we can change default coroutine thread to IO thread
        return withContext(Dispatchers.IO) {

            //worker run very fast
            //add delay to emulate slow running task
            delay(DELAY_TIME_MILLIS)

            return@withContext try {
                val picture = BitmapFactory.decodeResource(
                    applicationContext.resources,
                    R.drawable.android_cupcake
                )
                val output = blurBitmap(picture, 1)
                //write bitmap to temp file
                val outputUri = writeBitmapToFile(applicationContext, output)

                makeStatusNotification(
                    "Output is $outputUri",
                    applicationContext
                )
                Result.success()

            } catch (throwable: Throwable) {
                Log.e(
                    TAG,
                    applicationContext.resources.getString(R.string.error_applying_blur),
                    throwable
                )

                Result.failure()
            }
        }
    }
}