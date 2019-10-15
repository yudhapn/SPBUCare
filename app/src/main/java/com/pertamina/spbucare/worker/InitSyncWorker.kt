package com.pertamina.spbucare.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.pertamina.spbucare.repository.OrderRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class InitSyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params),
    KoinComponent {
    private val repo by inject<OrderRepository>()
    private val auth by inject<FirebaseAuth>()

    companion object {
        const val WORK_NAME = "InitSyncWorker"
    }

    override suspend fun doWork(): Result =
        try {
            val source = Source.SERVER
            val userType = inputData.getString("user")

            // refresh notification
            repo.getNotifications(auth.currentUser?.uid.toString())
            // refresh history
            repo.getOrders(userType, auth.currentUser?.uid.toString())
            repo.getInformations()
            repo.getEducations()
            Result.success()
        } catch (e: FirebaseFirestoreException) {
            Result.failure()
        }
}
