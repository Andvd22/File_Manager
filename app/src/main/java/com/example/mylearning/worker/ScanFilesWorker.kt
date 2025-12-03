package com.example.mylearning.worker

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mylearning.BuildConfig
import com.example.mylearning.database.AppDatabase
import com.example.mylearning.repository.FileRepository
import com.example.mylearning.view.MainActivity
import java.io.File

class ScanFilesWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams){
    override suspend fun doWork(): Result {
        return try{
            val db = AppDatabase.getDatabase(applicationContext)
            val repository = FileRepository(db.fileDao())
            repository.refreshFiles()
            fun createTestFile(context: Context) {
 //               val baseDir = context.getExternalFilesDir(null) ?: return
//                val watchDir = File(baseDir, "MyWatchFolder").apply { mkdirs() }
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val testFile = File(downloadsDir,"test_${System.currentTimeMillis()}.txt")


//                val testFile = File(watchDir, "test_${System.currentTimeMillis()}.txt")

                try {
                    testFile.createNewFile()
  //                  testFile.writeText("Hello from Worker/Activity at ${System.currentTimeMillis()}")
                    // Log để xem trong Logcat vì Worker không hiện Toast nếu App đang tắt
                    Log.d("FileUtils", "Đã tạo file: ${testFile.absolutePath}")

                    // Lưu ý: Worker chạy thread phụ không thể Toast trực tiếp,
                    // nhưng nếu gọi từ Activity thì vẫn hiện được nếu muốn xử lý thêm UI thread.
                } catch (e: Exception) {
                    Log.e("FileUtils", "Lỗi tạo file: ${e.message}")
                }
            }
            createTestFile(applicationContext)

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