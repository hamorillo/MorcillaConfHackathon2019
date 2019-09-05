package com.bankmorcillaconf.app.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bankmorcillaconf.app.R
import com.bankmorcillaconf.app.model.Task
import com.bankmorcillaconf.app.repository.PomodoroRepository
import com.bankmorcillaconf.app.util.ResultListener
import kotlinx.android.synthetic.main.user_task_item.view.*


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
        val url = data[position].url

        holder.view.taskUrlTextView.text = url

        if (url.contains("jira")) {
            holder.view.linkImageView.setImageResource(R.drawable.jira)
        } else if (url.contains("trello")) {
            holder.view.linkImageView.setImageResource(R.drawable.trello)
        } else {
            holder.view.linkImageView.setImageResource(R.drawable.link)
        }

        holder.view.setOnClickListener {
            try {
                holder.view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (exception: Exception) {
                Log.e("TasksAdapter", "Invalid url", exception)
            }
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
