package com.example.mylearning

import android.app.Application
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.mylearning.view.UninstallWarningActivity
import com.example.mylearning.worker.ScanFilesWorker
import java.util.concurrent.TimeUnit

class MyLearningApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val night = prefs.getBoolean("night_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (night) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        schedulePeriodicScanWork()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val intent = Intent(this, UninstallWarningActivity::class.java)
                .setAction(Intent.ACTION_VIEW)

            val shortcut = ShortcutInfo.Builder(this, "uninstall_shortcut")
                .setShortLabel(getString(R.string.shortcut_uninstall_short))
                .setLongLabel(getString(R.string.shortcut_uninstall_long))
                .setIcon(Icon.createWithResource(this, R.drawable.feature_request_translate_document))
                .setIntent(intent)
                .build()

            getSystemService(ShortcutManager::class.java).dynamicShortcuts = listOf(shortcut)
        }
    }

    private fun schedulePeriodicScanWork(){
        val request = PeriodicWorkRequestBuilder<ScanFilesWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

//    private fun scheduleTestScanWork(){
//        val request = OneTimeWorkRequestBuilder<ScanFilesWorker>()
//            .setInitialDelay(10, TimeUnit.SECONDS)
//            .build()
//
//        val workManager = WorkManager.getInstance(this)
//
//        workManager.enqueueUniqueWork(
//            TEST_WORK_NAME,
//            ExistingWorkPolicy.REPLACE,
//            request
//        )
//
//        workManager.getWorkInfoByIdLiveData(request.id).observeForever(object : Observer<WorkInfo?>{
//            override fun onChanged(value: WorkInfo?) {
////                if(value == null) return
//                if(value?.state?.isFinished==true){
//                    workManager.getWorkInfoByIdLiveData(request.id).removeObserver(this)
//                    scheduleTestScanWork()
//                }
//            }
//        })
//    }


    companion object {
        private const val PERIODIC_WORK_NAME = "ScanFilesPeriodicWork"
        private const val TEST_WORK_NAME = "ScanFilesTestWork"
    }

}