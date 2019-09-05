package com.bankmorcillaconf.app.ui

import android.os.CountDownTimer
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bankmorcillaconf.app.R
import com.bankmorcillaconf.app.model.User
import com.bankmorcillaconf.app.repository.UserRepository
import com.bankmorcillaconf.app.util.format
import java.util.concurrent.TimeUnit

class PomodoroTimer constructor(val textView: TextView, millisInFuture: Long, countDownInterval: Long, colorDisabled: Int) :
    CountDownTimer(millisInFuture, countDownInterval) {

    override fun onFinish() {
        textView.text = "00:00"
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.pomodoroDisabled))
    }

    override fun onTick(millis: Long) {
        val hms =
            ((TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))).format(2)+ ":"
                    + ((TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    millis
                )
            )).format(2)))
        textView.text = hms
    }

    companion object {
        fun create(textView: TextView, user: User, colorEnabled: Int, colorDisabled: Int = R.color.pomodoroDisabled): PomodoroTimer? {
            user.currentPomodoroStartDate?.let {
                val finishTime = it + (user.currentPomodoroDuration ?: 0)
                val durationLeft = finishTime - System.currentTimeMillis()
                if (durationLeft > 0L) {
                    textView.setTextColor(ContextCompat.getColor(textView.context, colorEnabled))
                    val counter = PomodoroTimer(textView, durationLeft, 1000L, colorDisabled)
                    counter.start()
                    return counter
                } else {
                    textView.text = "00:00"
                    textView.setTextColor(ContextCompat.getColor(textView.context, colorDisabled))
                    return null
                }
            } ?: run {
                textView.text = "00:00"
                textView.setTextColor(ContextCompat.getColor(textView.context, colorDisabled))
                return null
            }
        }
    }

}