package com.bankmorcillaconf.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bankmorcillaconf.app.repository.TaskRepository
import com.bankmorcillaconf.app.repository.UserRepository
import com.bankmorcillaconf.app.repository.UserRepository.Companion.staticUser
import com.bankmorcillaconf.app.ui.NewTaskActivity
import com.bankmorcillaconf.app.util.ResultListener
import kotlinx.android.synthetic.main.home_activity.*
import java.util.*
import android.os.CountDownTimer
import android.widget.Toast
import com.bankmorcillaconf.app.model.Pomodoro
import com.bankmorcillaconf.app.model.Task
import com.bankmorcillaconf.app.repository.PomodoroRepository
import java.util.concurrent.TimeUnit


class HomeActivity : AppCompatActivity() {

    companion object {
        const val MIN_5 = 5L * 60L * 1000L

        fun newIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    private val userRepository = UserRepository()
    private val taskRepository = TaskRepository()
    private val pomodoroRepository = PomodoroRepository()

    private var tasks: List<Task>? = null

    private var counter: MyCount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        createTaskButton.setOnClickListener {
            startActivity(NewTaskActivity.newIntent(this))
        }

        newPomodoroButton.setOnClickListener {
            tasks?.get(0)?.let {
                val pomodoro = Pomodoro(System.currentTimeMillis(), MIN_5)
                pomodoroRepository.createPomodoro(staticUser!!, it, pomodoro, ResultListener(
                    onSuccess = {
                        Toast.makeText(this@HomeActivity, "Pomodoro created", Toast.LENGTH_SHORT).show()
                        updateUsersInfo()
                    },
                    onError = {
                        Toast.makeText(this@HomeActivity, "Error creating pomodoro", Toast.LENGTH_SHORT).show()
                    }
                ))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUsersInfo()
    }

    private fun updateUsersInfo() {
        userRepository.getUserMe(staticUser!!.email, ResultListener(
            onSuccess = {
                restartPomodoro()
            },
            onError = {

            }
        ))

        userRepository.getAllUsers(ResultListener(
            onSuccess = { users ->
                var usersString = "All users:"
                users.forEach { user ->
                    usersString += "\n" + user.email
                }
                usersTextView.text = usersString
            },
            onError = {
                usersTextView.text = "Error get all users"
            }
        ))

        taskRepository.getAllTasks(staticUser!!, ResultListener(
            onSuccess = { tasks ->
                this.tasks = tasks
                var tasksString = "My tasks:"
                tasks.forEach { task ->
                    tasksString += "\n" + task.url
                }
                myTasksTextView.text = tasksString
            },
            onError = {
                usersTextView.text = "Error get my tasks"
            }
        ))
    }

    private fun restartPomodoro() {
        staticUser!!.currentPomodoroStartDate?.let {
            val finishTime = it + (staticUser!!.currentPomodoroDuration ?: 0)
            val durationLeft = finishTime - System.currentTimeMillis()
            if (durationLeft > 0L) {
                counter = MyCount(durationLeft, 1000L)
                counter!!.start()
            } else {
                myCurrentPomodoro.text = "00:00"
            }
        } ?: run {
            myCurrentPomodoro.text = "00:00"
        }
    }

    inner class MyCount internal constructor(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            myCurrentPomodoro.text = "00:00"
        }

        override fun onTick(millis: Long) {
            val hms = ((TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))).toString() + ":"
                    + ((TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))).toString()))
            myCurrentPomodoro.text = hms
        }
    }
}