package com.bankmorcillaconf.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.*

class ScheduledNotificationService : JobService() {

    companion object {
        const val EXTRA_ORIGIN_USER_MAIL = "EXTRA_ORIGIN_USER_MAIL"

        fun showNotification(context: Context, originUser: String) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val testChannel = NotificationChannel("general", "General", NotificationManager.IMPORTANCE_HIGH)
                testChannel.setShowBadge(true)
                testChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                notificationManager.createNotificationChannel(testChannel)
            }

            val notificationBuilder = NotificationCompat.Builder(context, "general")

            notificationBuilder
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(originUser)
                .setVibrate(LongArray(0))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setAutoCancel(true)

            notificationManager.notify(Random().nextInt(), notificationBuilder.build())
        }
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return true
    }

    override fun onStartJob(params: JobParameters): Boolean {
        params.extras.getString(EXTRA_ORIGIN_USER_MAIL)?.let {
            showNotification(this, it)
            stopSelf()
        }
        return true
    }

}