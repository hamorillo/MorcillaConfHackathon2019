package com.bankmorcillaconf.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bankmorcillaconf.app.repository.TaskRepository
import com.bankmorcillaconf.app.repository.UserRepository
import com.bankmorcillaconf.app.ui.TasksAdapter
import com.bankmorcillaconf.app.util.ResultListener
import kotlinx.android.synthetic.main.user_activity.*

class UserActivity : AppCompatActivity() {

    private val userRepository = UserRepository()
    private val taskRepository = TaskRepository()

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
        intent.getStringExtra(EXTRA_USER_MAIL).let {
            retrieveUser(it)
            retrieveTaskForUser(it)
            renderImage(it)
        }
    }

    private fun renderImage(email: String) {
        val avatarId = email.length % 4
        when (avatarId) {
            0 -> avatarImageView.setImageResource(R.drawable.avatar1)
            1 -> avatarImageView.setImageResource(R.drawable.avatar2)
            2 -> avatarImageView.setImageResource(R.drawable.avatar3)
            3 -> avatarImageView.setImageResource(R.drawable.avatar4)
        }
    }

    private fun retrieveTaskForUser(userMail: String) {
        taskRepository.getAllTasks(userMail, ResultListener(
            onSuccess = {
                userTasksRecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@UserActivity)
                    adapter = TasksAdapter(it)
                }
            },
            onError = {}
        ))
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
        statusTextView.setText(
            if (currentPomodoroStartDate != null && currentPomodoroDuration != null) {
                val actualTimeStamp = System.currentTimeMillis()
                val finishPomodoroTimeStamp = currentPomodoroStartDate + currentPomodoroDuration
                if (actualTimeStamp > finishPomodoroTimeStamp) {
                    "Now is taking a rest" //getDrawable(R.drawable.user_available_ic)
                } else {
                    "Now is working" //getDrawable(R.drawable.user_busy_ic)
                }
            } else {
                "Now is taking a rest" //getDrawable(R.drawable.user_available_ic)
            }
        )
    }

    private fun renderMail(email: String) {
        userNameTextView.text = email.split("@")[0]
        userMailTextView.text = email
    }
}