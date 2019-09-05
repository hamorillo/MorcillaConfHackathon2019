package com.bankmorcillaconf.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bankmorcillaconf.app.model.Push
import com.bankmorcillaconf.app.repository.PushRepository
import com.bankmorcillaconf.app.repository.TaskRepository
import com.bankmorcillaconf.app.repository.UserRepository
import com.bankmorcillaconf.app.ui.TasksAdapter
import com.bankmorcillaconf.app.util.ResultListener
import com.bankmorcillaconf.app.util.sha1
import kotlinx.android.synthetic.main.user_activity.*

class UserActivity : AppCompatActivity() {

    private val userRepository = UserRepository()
    private val taskRepository = TaskRepository()
    private val pushRepository = PushRepository()

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
                renderStatus(it.currentPomodoroStartDate, it.currentPomodoroDuration)
                renderCall(it.email)
            },
            onError = {
                // TODO 2019-09-04 hector
            }
        ))
    }

    private fun renderCall(email: String) {
        val logedUser = UserRepository.staticUser?.email

        if (logedUser == null || logedUser == email) {
            sendNotificationImageView.visibility = View.GONE
        } else {
            sendNotificationImageView.visibility = View.VISIBLE
            sendNotificationImageView.setOnClickListener {
                pushRepository.sendPush(Push(email.sha1(), logedUser.sha1()), ResultListener(
                    onSuccess = {
                        Toast.makeText(
                            this@UserActivity, "Push Sended",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onError = {
                        Toast.makeText(
                            this@UserActivity, "Send Push Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ))
            }
        }
    }

    private fun renderStatus(currentPomodoroStartDate: Long?, currentPomodoroDuration: Long?) {
        statusTextView.text = if (currentPomodoroStartDate != null && currentPomodoroDuration != null) {
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
    }

    private fun renderMail(email: String) {
        userNameTextView.text = email.split("@")[0]
        userMailTextView.text = email
    }
}