package com.bankmorcillaconf.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bankmorcillaconf.app.R
import com.bankmorcillaconf.app.model.Task
import kotlinx.android.synthetic.main.user_task_item.view.*


class TasksAdapter(private val data: List<Task>) : RecyclerView.Adapter<TasksAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_task_item, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.view.taskUrlTextView.text = data[position].url
    }

    override fun getItemCount() = data.size
}
