package com.bankmorcillaconf.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bankmorcillaconf.app.repository.UserRepository
import com.bankmorcillaconf.app.util.ResultListener
import kotlinx.android.synthetic.main.user_activity.*

class UserActivity : AppCompatActivity() {

    private val userRepository = UserRepository()

    companion object {
        private const val EXTRA_USER_MAIL = "EXTRA_USER_MAIL"

        fun newIntent(context: Context, userId: String): Intent {
            val intent = Intent(context, UserActivity::class.java)
            intent.putExtra(EXTRA_USER_MAIL, userId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)
    }

    override fun onResume() {
        super.onResume()
        retrieveUser(intent.getStringExtra(EXTRA_USER_MAIL))
    }

    private fun retrieveUser(userMail: String) {
        userRepository.getUserMe(userMail, ResultListener(
            onSuccess = {
                renderMail(it.email)
                renderStatusIcon(it.currentPomodoroStartDate, it.currentPomodoroDuration)
            },
            onError = {
                // TODO 2019-09-04 hector
            }
        ))
    }

    private fun renderStatusIcon(currentPomodoroStartDate: Long?, currentPomodoroDuration: Long?) {
        statusImageView.setImageDrawable(
            if (currentPomodoroStartDate != null && currentPomodoroDuration != null) {
                val actualTimeStamp = System.currentTimeMillis()
                val finishPomodoroTimeStamp = currentPomodoroStartDate + currentPomodoroDuration
                if (actualTimeStamp > finishPomodoroTimeStamp) {
                    getDrawable(R.drawable.user_available_ic)
                } else {
                    getDrawable(R.drawable.user_busy_ic)
                }
            } else {
                getDrawable(R.drawable.user_available_ic)
            }
        )
    }

    private fun renderMail(email: String) {
        userMailTextView.text = email
    }
}