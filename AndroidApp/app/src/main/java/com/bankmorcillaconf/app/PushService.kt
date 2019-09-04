package com.bankmorcillaconf.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bankmorcillaconf.app.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import android.content.ComponentName
import android.os.PersistableBundle
import com.bankmorcillaconf.app.ScheduledNotificationService.Companion.EXTRA_ORIGIN_USER_MAIL


class PushService : FirebaseMessagingService() {
    companion object {
        const val TAG = "PushService"
        private const val CURRENT_POMODORO_START_DATE_KEY = "currentPomodoroStartDate"
        private const val CURRENT_POMODORO_DURATION_KEY = "currentPomodoroDuration"
        private const val ISSUER_MAIL = "issuerUser"
    }

    private val userRepository = UserRepository()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from!!)
        val currentPomodoroStartDate = remoteMessage.data[CURRENT_POMODORO_START_DATE_KEY]
        val currentPomodoroDuration = remoteMessage.data[CURRENT_POMODORO_DURATION_KEY]
        val originUserMail = remoteMessage.data[ISSUER_MAIL]

        if (!(currentPomodoroStartDate.isNullOrEmpty() ||
                    currentPomodoroDuration.isNullOrEmpty() ||
                    originUserMail.isNullOrEmpty())
        ) {
            processCallingPush(currentPomodoroStartDate.toLong(), currentPomodoroDuration.toLong(), originUserMail)
        } else {
            Log.w(TAG, "Push info not valid")
        }
    }

    private fun processCallingPush(
        currentPomodoroStartDate: Long,
        currentPomodoroDuration: Long,
        originUserMail: String
    ) {
        val actualTimeStamp = System.currentTimeMillis()
        val finishPomodoroTimeStamp = currentPomodoroStartDate + currentPomodoroDuration

        if (actualTimeStamp > finishPomodoroTimeStamp) {
            showNotification(originUserMail)
        } else {
            scheduleNotification(finishPomodoroTimeStamp, originUserMail)
        }
    }

    private fun scheduleNotification(timeToScheduled: Long, originUser: String) {
        val actualTimeStamp = System.currentTimeMillis()
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val serviceComponent = ComponentName(this, ScheduledNotificationService::class.java)
        val bundle = PersistableBundle()
        bundle.putString(EXTRA_ORIGIN_USER_MAIL, originUser)

        val jobInfoBuilder = JobInfo.Builder(Random().nextInt(), serviceComponent)
        jobInfoBuilder.setMinimumLatency(timeToScheduled - actualTimeStamp)
            .setOverrideDeadline(timeToScheduled - actualTimeStamp)
            .setExtras(bundle)

        jobScheduler.schedule(jobInfoBuilder.build())
    }

    private fun showNotification(originUser: String) {
        ScheduledNotificationService.showNotification(this, originUser)
    }
}