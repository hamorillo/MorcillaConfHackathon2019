package com.bankmorcillaconf.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.split
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bankmorcillaconf.app.R
import com.bankmorcillaconf.app.model.Task
import com.bankmorcillaconf.app.repository.TaskRepository
import com.bankmorcillaconf.app.repository.UserRepository
import com.bankmorcillaconf.app.util.ResultListener
import kotlinx.android.synthetic.main.new_task_activity.*

class NewTaskActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, NewTaskActivity::class.java)
        }
    }

    private val taskRepository = TaskRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_task_activity)

        addTaskButton.setOnClickListener {
            var timeInMillis: Long = 0
            try {
                timeInMillis = taskPomodoTimeEditText.text.toString().toLong() * 60 * 1000
            } catch (exception: Exception) {
                Toast.makeText(this@NewTaskActivity, "Invalid time", Toast.LENGTH_SHORT).show()
            }

            try {
                val taskUrl = taskUrlEditText.text.toString()

                val task =
                    Task(taskUrl.split("//")[1].split("/").takeLast(1)[0], taskUrl, timeInMillis, "no description", System.currentTimeMillis())
                taskRepository.createTask(UserRepository.staticUser!!, task, ResultListener(
                    onSuccess = {
                        Toast.makeText(this@NewTaskActivity, "Task created", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onError = {
                        Toast.makeText(this@NewTaskActivity, "Error creating task", Toast.LENGTH_SHORT).show()
                    }
                ))
            } catch (exception: Exception) {
                Toast.makeText(this@NewTaskActivity, "Invalid url", Toast.LENGTH_SHORT).show()
            }
        }


        taskRepository.getAllTasks(
            UserRepository.staticUser!!, ResultListener(
                onSuccess = { tasks ->
                    var tasksString = "Your tasks:"
                    tasks.forEach { task ->
                        tasksString += "\n" + task.url
                    }
                    myTasksTextView.text = tasksString
                },
                onError = {
                    myTasksTextView.text = "Error get my tasks"
                }
            ))
    }
}
