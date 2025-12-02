package com.example.mylearning

import android.app.Application
import androidx.lifecycle.Observer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.mylearning.worker.ScanFilesWorker
import java.util.concurrent.TimeUnit

class MyLearningApp : Application() {

    override fun onCreate() {
        super.onCreate()
//        if (BuildConfig.DEBUG) {
//            scheduleTestScanWork()
//        } else {
            schedulePeriodicScanWork()
//        }
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