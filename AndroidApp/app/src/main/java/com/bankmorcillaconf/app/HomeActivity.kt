package com.bankmorcillaconf.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bankmorcillaconf.app.model.Task
import com.bankmorcillaconf.app.repository.TaskRepository
import com.bankmorcillaconf.app.repository.UserRepository
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

        addTaskButton.setOnClickListener {
            val taskUrl = taskUrlEditText.text.toString()
            val task = Task(taskUrl.split("/").takeLast(1)[0], taskUrl, "no description")
            taskRepository.createTask(UserRepository.staticUser!!, task, ResultListener(
                onSuccess = {
                    Toast.makeText(this@HomeActivity, "Task created", Toast.LENGTH_SHORT).show()
                    updateUsersInfo()
                },
                onError = {
                    Toast.makeText(this@HomeActivity, "Error creating task", Toast.LENGTH_SHORT).show()
                }
            ))
        }
    }

    override fun onResume() {
        super.onResume()
        updateUsersInfo()
    }

    private fun updateUsersInfo() {

        userRepository.getAllUsers(ResultListener(
            onSuccess = { users ->
                var usersString = ""
                users.forEach { user ->
                    usersString += user.email + "\n"
                    taskRepository.getAllTasks(user, ResultListener(
                        onSuccess = { tasks ->
                            tasks.forEach { task ->
                                usersString += "- " + task.id + "\n"
                            }
                        },
                        onError = {
                            usersString += "Error get all tasks" + "\n"
                        }
                    ))
                }
                usersTextView.text = usersString
            },
            onError = {
                usersTextView.text = "Error get all users"
            }
        ))
    }
}