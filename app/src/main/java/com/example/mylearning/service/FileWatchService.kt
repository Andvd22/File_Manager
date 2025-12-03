package com.example.mylearning.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.metrics.Event
import android.os.Build
import android.os.Environment
import android.os.FileObserver
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.mylearning.R
import java.io.File

class FileWatchService : Service(){
    private var fileObserver: FileObserver?= null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        startForeground(
            FOREGROUND_ID,
            buildForegroundNotification("Đang theo dõi thay đổi files trong bộ nhớ")
        )
        startWatchingAppFolder()
        // startWatchingRoot()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        fileObserver?.stopWatching()
        fileObserver = null
        Log.d(TAG, "FileObserver stoppped")
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val manager = getSystemService(NotificationManager::class.java)

            val foregroundChannel = NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                "File Watch Foreground",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel cho service theo doi file dang chay"
            }

            val eventChannel = NotificationChannel(
                EVENT_CHANNEL_ID,
                "File Change Events",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thong bao khi file duoc crud"
            }

            manager.createNotificationChannel(foregroundChannel)
            manager.createNotificationChannel(eventChannel)
        }
    }

    private fun buildForegroundNotification(content: String): Notification{
        return NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Đang theo dõi files")
            .setContentText(content)
            .setOngoing(true)
            .build()
    }

    private fun startWatchingAppFolder() {
        // Thư mục gốc riêng của app: .../Android/data/<package>/files/
        val baseDir = applicationContext.getExternalFilesDir(null)
    
        if (baseDir == null) {
            Log.w(TAG, "App dir is null, cannot start FileObserver")
            return
        }
    

        val watchDir = File(baseDir, "MyWatchFolder")
        if (!watchDir.exists()) {
            watchDir.mkdirs()       // đảm bảo thư mục tồn tại
        }
    
        val rootDir = watchDir.absolutePath
        Log.d(TAG, "FileObserver sẽ theo dõi thư mục: $rootDir")
    
        fileObserver = object : FileObserver(
            rootDir,
            CREATE or DELETE or MODIFY or MOVED_TO or MOVED_FROM
        ) {
            override fun onEvent(event: Int, path: String?) {
                if (path == null) return
    
                val fullPath = "$rootDir/$path"
    
                val eventName = when {
                    event and CREATE != 0 -> "CREATE (Tạo mới)"
                    event and DELETE != 0 -> "DELETE (Xóa)"
                    event and MODIFY != 0 -> "MODIFY (Sửa)"
                    event and MOVED_TO != 0 -> "MOVED_TO (Di chuyển đến)"
                    event and MOVED_FROM != 0 -> "MOVED_FROM (Di chuyển đi)"
                    else -> "OTHER (Khác)"
                }
    
                Log.d(TAG, "File event: $eventName - $fullPath")
                showFileChangedNotification(eventName, fullPath)
            }
        }
    
        fileObserver?.startWatching()
        Log.d(TAG, "FileObserver đã bắt đầu cho thư mục: $rootDir")
    }

    private fun startWatchingRoot(){
        val rootDir = Environment.getExternalStorageDirectory().absolutePath

        fileObserver = object : FileObserver(
            rootDir,
            CREATE or DELETE or MODIFY or MOVED_TO or MOVED_FROM
        ){
            override fun onEvent(event: Int, path: String?) {
                if(path == null) return

                val fullPath = "$rootDir/$path"

                val eventName = when {
                    event and CREATE !=0 -> "CREATE (Tạo mới)"
                    event and DELETE !=0 -> "DELETE (xoa)"
                    event and MODIFY !=0 -> "MODIFY (Sua)"
                    event and MOVED_TO !=0 -> "MOVED_TO (Di chuyen den)"
                    event and MOVED_FROM !=0 -> "MOVED_FROM (di chuyen di)"
                    else -> "OTHER (khac)"
                }

                Log.d(TAG, "File event: $eventName - $fullPath")

                showFileChangedNotification(eventName, fullPath)
            }
        }
        fileObserver?.startWatching()
        Log.d(TAG, "FileObserver bat dau: $rootDir")
    }

    private fun showFileChangedNotification(event: String, fullPath: String){
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, EVENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.bg_bottom_sheet1)
            .setContentTitle("File $event")
            .setContentText(fullPath)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        manager.notify(id, notification)
    }
    companion object{
        private const val TAG = "FileWatchService"
        private const val FOREGROUND_CHANNEL_ID = "file_watch_forefround"

        private const val EVENT_CHANNEL_ID = "file_watch_events"
        private const val FOREGROUND_ID = 1
    }
}