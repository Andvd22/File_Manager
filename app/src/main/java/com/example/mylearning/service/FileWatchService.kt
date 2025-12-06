package com.example.mylearning.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.metrics.Event
import android.os.Build
import android.os.Environment
import android.os.FileObserver
import android.os.FileObserver.ALL_EVENTS
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.mylearning.R
import com.example.mylearning.view.FileEventPopupActivity
import com.example.mylearning.view.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

data class FileEvent(
    val eventType: Int,
    val path: String,
    val timestamp: Long = System.currentTimeMillis()
)
class FileWatchService : Service(){
    private val lastImportantEventTime = mutableMapOf<String, Long>()
    private val debounceJobs = ConcurrentHashMap<String, Job>()
    private val fileEventFlow = MutableSharedFlow<FileEvent>(
        extraBufferCapacity = 64
    )

    //do service ko co san scope giong lifecycleScope cua activity
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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
        setupFlowCollector()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        fileObserver?.stopWatching()
        fileObserver = null
        serviceScope.cancel()
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
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
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

    private fun setupFlowCollector(){
        serviceScope.launch {
            fileEventFlow
                .collect{fileEvent ->
                    processPerFileDebounce(fileEvent)
                }
        }
    }

    private fun processPerFileDebounce(fileEvent: FileEvent){
        val path = fileEvent.path
        val key = path.trim().lowercase()
        if (key.endsWith(".crdownload") || // Chrome/Cốc Cốc đang tải
            key.endsWith(".tmp") ||        // File tạm hệ thống
            key.endsWith(".part") ||       // Firefox/IDM
            key.endsWith(".download") ||   // Samsung Internet cũ
            key.endsWith(".opdownload")    // Opera
        ) {
            return
        }
        debounceJobs[key]?.cancel()
        debounceJobs[key]=serviceScope.launch {
            delay(500)
            handleRealProcessing(fileEvent)
            debounceJobs.remove(key)
        }
    }

    private fun handleRealProcessing(fileEvent: FileEvent) {
        val event = fileEvent.eventType
        val originalPath = fileEvent.path
        val now = fileEvent.timestamp

        // 1. Chuẩn hóa Key (Quan trọng để không bị lỗi chữ hoa/thường)
        val normalizedKey = originalPath.trim().lowercase()

        Log.d(TAG, "Processing Event: $event | Key: $normalizedKey")

        // 2. Logic chặn (Debounce 10s cho tất cả các loại sự kiện)
        val lastTime = lastImportantEventTime[normalizedKey] ?: 0L

        // Nếu sự kiện mới cách sự kiện cũ dưới 10 giây -> CHẶN
        if (now - lastTime < 10000L) {
            // Ngoại lệ: Nếu là DELETE thì cho qua để xóa map, còn lại chặn hết
            if (event and FileObserver.DELETE == 0) {
                Log.d(TAG, ">>> BLOCKED (Spam/Echo): $originalPath")
                return
            }
        }

        val eventName = when {
            event and FileObserver.CREATE != 0 -> "CREATE (Tạo mới)"
            event and FileObserver.DELETE != 0 -> "DELETE (Xóa)"
            event and FileObserver.MODIFY != 0 -> "MODIFY (Sửa nội dung)"
            event and FileObserver.MOVED_TO != 0 -> "MOVED_TO (Đến/Đổi tên)"
            event and FileObserver.MOVED_FROM != 0 -> "MOVED_FROM (Đi/Đổi tên)"
            else -> "OTHER"
        }

        // 3. Cập nhật thời gian vào Map
        if (event and FileObserver.DELETE != 0) {
            lastImportantEventTime.remove(normalizedKey)
        } else {
            lastImportantEventTime[normalizedKey] = now
        }

        Log.d(TAG, "PROCESSED: $eventName - $originalPath")
        showFileChangedNotification(eventName, originalPath)
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
                fileEventFlow.tryEmit(FileEvent(event, fullPath))
            }
        }
        fileObserver?.startWatching()
        Log.d(TAG, "File OBser đã bắt đầu theo dõi thư mục DOWNLOADS: $rootDir")
    }

    private fun showFileChangedNotification(event: String, fullPath: String){
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val openFileIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("extra_path", fullPath)
            putExtra("extra_action_name", "open_file")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val openFilePendingIntent = PendingIntent.getActivity(this, 1, openFileIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val goToFileInAppIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("extra_path", fullPath)
            putExtra("extra_action_name", "go_to_file_in_app")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val goToFileInAppPendingIntent = PendingIntent.getActivity(this, 2, goToFileInAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val views = RemoteViews(packageName, R.layout.notification_file_event).apply{
           setImageViewResource(R.id.ivIcon, R.drawable.bg_bottom_sheet1)
            setTextViewText(R.id.tvTitle, "File $event")
            setTextViewText(R.id.tvDescription, fullPath)
            setTextViewText(R.id.tvTime, SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
            setViewVisibility(R.id.btnAction, View.VISIBLE)
            setOnClickPendingIntent(R.id.btnAction, openFilePendingIntent)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, EVENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.bg_bottom_sheet1)
             .setContentTitle("File $event")
             .setContentText(fullPath)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(views)
            .setCustomBigContentView(views)
            .setCustomHeadsUpContentView(views)
            .setContentIntent(goToFileInAppPendingIntent)
            .build()

        val id = fullPath.trim().lowercase().hashCode()
        manager.notify(id, notification)


//        val popupIntent = Intent(this, FileEventPopupActivity::class.java).apply {
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            putExtra(FileEventPopupActivity.EXTRA_EVENT, event)
//            putExtra(FileEventPopupActivity.EXTRA_PATH, fullPath)
//        }
//        startActivity(popupIntent)
    }
    companion object{
        private const val TAG = "FileWatchService"
        private const val FOREGROUND_CHANNEL_ID = "file_watch_forefround"

        private const val EVENT_CHANNEL_ID = "file_watch_events"
        private const val FOREGROUND_ID = 1
    }
}