package com.pertamina.spbucare.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.pertamina.spbucare.repository.OrderRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class NotificationSyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params),
    KoinComponent {
    private val repo by inject<OrderRepository>()
    private val auth by inject<FirebaseAuth>()

    companion object {
        const val WORK_NAME = "NotificationSyncWorker"
    }

    override suspend fun doWork(): Result =
        try {
            repo.getNotifications(auth.currentUser?.uid.toString())
            Result.success()
        } catch (e: FirebaseFirestoreException) {
            Result.failure()
        }
}
