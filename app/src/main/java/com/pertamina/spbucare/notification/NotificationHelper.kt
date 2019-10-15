package com.pertamina.spbucare.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_VIBRATE
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.pertamina.spbucare.R
import com.pertamina.spbucare.ui.MainActivity


class NotificationHelper {

    companion object NotificationApplication {
        private const val CHANNEL_ID = "spbu_carer"
        private const val CHANNEL_NAME = "SPBU Care"
        private const val CHANNEL_DESC = "SPBU Care Notification"
        private var countNotif = 0

        fun displayNotification(context: Context, title: String, body: String, type: String) {
            val summaryId = 0
            val notifyId = (0..40).random()
            countNotif++

            val image = when (type) {
                "pembatalan kiriman" -> R.drawable.cancel_menu
                "tambah perencanaan" -> R.drawable.add_menu
                "setor angkut" -> R.drawable.deposit_menu
                "emergency MS2 manual" -> R.drawable.emergency_menu
                "tarik LO" -> R.drawable.withdrawal_menu
                "quiz" -> R.drawable.quiz_menu
                else -> R.drawable.document_file
            }

            Log.d("testing", "type: $type drawable: $image")

            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = PendingIntent.getActivity(
                context,
                100,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val groupOrder = "com.pertamina.spbucare.order"

            Glide.with(context)
                .asBitmap()
                .load(image)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(resource)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setContentIntent(pendingIntent)
                            .setDefaults(DEFAULT_VIBRATE)
                            .setAutoCancel(true)
                            .setGroup(groupOrder)
                            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                            .setPriority(NotificationCompat.PRIORITY_MAX)

                        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setStyle(
                                NotificationCompat.InboxStyle()
                                    .setSummaryText("$countNotif Notifikasi")
                            )
                            .setGroup(groupOrder)
                            .setGroupSummary(true)
                            .build()


                        val notifManager = NotificationManagerCompat.from(context)
                        notifManager.notify(notifyId, builder.build())
                        notifManager.notify(summaryId, summaryNotification)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val channel =
                                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                            channel.description = CHANNEL_DESC
                            channel.setShowBadge(true)
                            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                            val manager = context.getSystemService(NotificationManager::class.java)
                            manager?.createNotificationChannel(channel)
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })
        }
    }
}