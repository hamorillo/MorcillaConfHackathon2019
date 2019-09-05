package com.bankmorcillaconf.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bankmorcillaconf.app.model.Task
import com.bankmorcillaconf.app.repository.PomodoroRepository
import com.bankmorcillaconf.app.util.ResultListener
import kotlinx.android.synthetic.main.user_task_item.view.*
import android.content.Intent
import android.net.Uri




class TasksAdapter(private val userId: String, private val data: List<Task>) :
    RecyclerView.Adapter<TasksAdapter.MyViewHolder>() {

    val pomodoroRepository = PomodoroRepository()

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.bankmorcillaconf.app.R.layout.user_task_item, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.view.taskUrlTextView.text = data[position].url
        holder.view.setOnClickListener {
            holder.view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data[position].url)))
        }
        pomodoroRepository.getAllPomodoros(
            userId, data[position], ResultListener(
                onSuccess = { pomodoros ->
                    holder.view.pomodorosTextView.text = holder.view.context.getString(
                        com.bankmorcillaconf.app.R.string.pomodoros_on_task,
                        pomodoros.size,
                        data[position].pomodoTimeMillis / 60 / 1000
                    )
                },
                onError = {

                })
        )
    }

    override fun getItemCount() = data.size
}
