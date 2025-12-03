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
    private var lastMovedFromPath: String? = null
    private var lastMovedFromTime: Long = 0L
    private val RENAME_WINDOW_MS = 1000L


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
        startWatchingDownloadsFolder()
        //startWatchingAppFolder()
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

    private fun startWatchingDownloadsFolder(){
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        if(downloadsDir == null || !downloadsDir.exists()|| !downloadsDir.canRead()){
            Log.w(TAG, "Thư mục downloads không đọc, mở, tồn tại")
            return
        }

        val rootDir = downloadsDir.absolutePath
        Log.d(TAG, "FileObserve sẽ theo dõi thư mục : $rootDir")
        fileObserver = object : FileObserver(
            rootDir,
            CREATE or DELETE or MODIFY or MOVED_FROM or MOVED_TO
        ){
            override fun onEvent(event: Int, path: String?){
                if(path == null) return
                val fullPath = "$rootDir/$path"
                val now = System.currentTimeMillis()

                // val eventName = when {
                //     event and CREATE != 0 -> "CREATE (tao moi)"
                //     event and DELETE != 0 -> "DELETE (xoa)"
                //     event and MODIFY != 0 -> "MODIFY (sua)"
                //     event and MOVED_FROM != 0 -> "MOVED_FROM (di chuyen di)"
                //     event and MOVED_TO !=0 -> "MOVED_TO (di chuyen den)"
                //     else -> "OTHER (khac)"
                // }
                // Log.d(TAG, "File event: $eventName - $fullPath")
                // showFileChangedNotification(eventName, fullPath)

                when {
                    event and CREATE !=0 -> {
                        showFileChangedNotification("CREATE (tao moi)", fullPath)
                    }
                    event and DELETE !=0 -> {
                        showFileChangedNotification("DELETE (xoa)", fullPath)
                    }
                    event and MOVED_FROM !=0 -> {
                        lastMovedFromPath = fullPath
                        lastMovedFromTime = now
                    }
                    event and MOVED_TO !=0 -> {
                        val oldPath = lastMovedFromPath
                        val timeDiff = now - lastMovedFromTime
                        if (oldPath != null && timeDiff <= RENAME_WINDOW_MS) {
                            // => Xem như một lần ĐỔI TÊN
                            lastMovedFromPath = null
            
                            val oldName = java.io.File(oldPath).name
                            val newName = java.io.File(fullPath).name
                            val message = "Đổi tên: $oldName ➜ $newName"
                            lastMovedFromPath = null
                            lastMovedFromTime = 0L
                            showFileChangedNotification("RENAME", message)
                        } else {
                            // Không có MOVED_FROM gần đó => thực sự là di chuyển file
                            showFileChangedNotification("MOVED_TO (di chuyển đến)", fullPath)
                        }
                    }
                    event and MODIFY != 0 -> {
                        val timeDiff = now - lastMovedFromTime
                        if (timeDiff > RENAME_WINDOW_MS) {
                            // Không liên quan rename, xử lý như sửa nội dung thật sự
                            showFileChangedNotification("MODIFY (sửa)", fullPath)
                        } else {
                            // Nằm trong “cửa sổ” rename → bỏ qua, vì đã có noti RENAME rồi
                        }
                    }
            
                    else -> {
                        showFileChangedNotification("OTHER (khác)", fullPath)
                    }
                }
            }
        }
        fileObserver?.startWatching()
        Log.d(TAG, "File OBser đã bắt đầu theo dõi thư mục DOWNLOADS: $rootDir")
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