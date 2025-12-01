package com.example.mylearning.worker

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mylearning.BuildConfig
import com.example.mylearning.database.AppDatabase
import com.example.mylearning.repository.FileRepository

class ScanFilesWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams){
    override suspend fun doWork(): Result {
        return try{
            val db = AppDatabase.getDatabase(applicationContext)
            val repository = FileRepository(db.fileDao())
            repository.refreshFiles()

            if(BuildConfig.DEBUG){
                Log.d(TAG, "ScanFileWorker: refreshFiles() completed")
            }
            Result.success()
        } catch(e: Exception){
            Log.e(TAG, "ScanFileWorker failed: ${e.message}", e)
            Result.retry()
        }
    }

    companion object{
        private const val TAG = "ScanFilesWorker"
    }
}