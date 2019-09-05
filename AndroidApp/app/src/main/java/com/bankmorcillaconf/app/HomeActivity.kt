package com.bankmorcillaconf.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bankmorcillaconf.app.repository.TaskRepository
import com.bankmorcillaconf.app.repository.UserRepository
import com.bankmorcillaconf.app.repository.UserRepository.Companion.staticUser
import com.bankmorcillaconf.app.ui.NewTaskActivity
import com.bankmorcillaconf.app.util.ResultListener
import kotlinx.android.synthetic.main.home_activity.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bankmorcillaconf.app.model.Pomodoro
import com.bankmorcillaconf.app.model.Task
import com.bankmorcillaconf.app.repository.PomodoroRepository
import com.bankmorcillaconf.app.ui.PomodoroTimer
import com.bankmorcillaconf.app.ui.UsersAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.home_user_item.*


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

    private var counter: PomodoroTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        supportActionBar?.elevation = 0f
        supportActionBar?.title = staticUser!!.email.split("@")[0]

        createTaskButton.setOnClickListener {
            startActivity(NewTaskActivity.newIntent(this))
        }

        newPomodoroButton.setOnClickListener {
            if (tasks == null || tasks?.size == 0) {
                startActivity(NewTaskActivity.newIntent(this))
            } else {
                tasks?.get(0)?.let {
                    val time =  if (counter == null) MIN_5 else 0L
                    val pomodoro = Pomodoro(System.currentTimeMillis(), time)
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
    }

    override fun onResume() {
        super.onResume()
        updateUsersInfo()
    }

    private fun updateUsersInfo() {
        userRepository.getUserMe(staticUser!!.email, ResultListener(
            onSuccess = {
                counter?.cancel()
                counter = PomodoroTimer.create(myCurrentPomodoro, staticUser!!, android.R.color.white)
                if (counter != null) {
                    pomodoroMessage.text = "Stay focused!"
                    newPomodoroButton.text = "CANCEL"
                } else {
                    pomodoroMessage.text = "Ready?"
                    newPomodoroButton.text = "START"
                }
            },
            onError = {

            }
        ))

        userRepository.getAllUsers(ResultListener(
            onSuccess = { users ->
                val usersWithoutMe = users
                    .filter { it.email !=  staticUser!!.email}
                    .sortedByDescending { it.currentPomodoroStartDate }

                usersRecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@HomeActivity)
                    adapter = UsersAdapter(usersWithoutMe)
                }
            },
            onError = {
//                usersTextView.text = "Error get all users"
                Toast.makeText(this@HomeActivity, "Error get users", Toast.LENGTH_SHORT).show()
            }
        ))

        taskRepository.getAllTasks(staticUser!!, ResultListener(
            onSuccess = { tasks ->
                this.tasks = tasks
                renderActualTask(tasks.first())
            },
            onError = {
//                usersTextView.text = "Error get my tasks"
                Toast.makeText(this@HomeActivity, "Error get tasks", Toast.LENGTH_SHORT).show()
            }
        ))
    }

    private fun renderActualTask(task: Task) {
        actualTaskTextView.text = task.id
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        menu.add("Logged with ${staticUser!!.email}")
        inflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                logoutActualUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logoutActualUser() {
        FirebaseAuth.getInstance().signOut()
        closeHomeActivity()
    }

    private fun closeHomeActivity() {
        finish()
    }
}