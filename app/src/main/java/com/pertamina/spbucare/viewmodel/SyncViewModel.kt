package com.pertamina.spbucare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.*
import com.pertamina.spbucare.worker.InitSyncWorker


class SyncViewModel(application: Application) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)

    fun syncData(type: String) {
        val builder = Data.Builder()
        val userData = builder.putString("userType", type).build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<InitSyncWorker>()
            .setInputData(userData)
            .setConstraints(constraints)
            .build()

        workManager.beginUniqueWork(InitSyncWorker.WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest).enqueue()
    }
}