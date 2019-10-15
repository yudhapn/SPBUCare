package com.pertamina.spbucare.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pertamina.spbucare.repository.OrderRepository

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val tag = OrderRepository::class.java.simpleName

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(tag, "From: " + remoteMessage.from)
        val data = remoteMessage.data
        Log.d(tag, "Message data payload: $data")
        val title = data.getValue("title")
        val body = data.getValue("body")
        val type = data.getValue("type")

//        syncNotification()

        NotificationHelper.run { displayNotification(applicationContext, title, body, type) }
        // Check if message contains a notification_nav payload.
        if (remoteMessage.notification != null) {
            Log.d(tag, "Message Notification Body: " + remoteMessage.notification?.body)
        }
    }

    //    private fun syncNotification() {
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//
//        val workRequest = OneTimeWorkRequestBuilder<NotificationSyncWorker>()
//            .setConstraints(constraints)
//            .build()
//
//        WorkManager.getInstance(applicationContext)
//            .beginUniqueWork(NotificationSyncWorker.WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
//            .enqueue()
//    }
}