package com.bankmorcillaconf.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bankmorcillaconf.app.model.Task
import com.bankmorcillaconf.app.repository.TaskRepository
import com.bankmorcillaconf.app.repository.UserRepository
import com.bankmorcillaconf.app.repository.UserRepository.Companion.staticUser
import com.bankmorcillaconf.app.ui.NewTaskActivity
import com.bankmorcillaconf.app.util.ResultListener
import kotlinx.android.synthetic.main.home_activity.*

class HomeActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    private val userRepository = UserRepository()
    private val taskRepository = TaskRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        createTaskButton.setOnClickListener {
            startActivity(NewTaskActivity.newIntent(this))
        }
    }

    override fun onResume() {
        super.onResume()
        updateUsersInfo()
    }

    private fun updateUsersInfo() {

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
}