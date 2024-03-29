package com.bankmorcillaconf.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bankmorcillaconf.app.R
import com.bankmorcillaconf.app.UserActivity
import com.bankmorcillaconf.app.model.User
import kotlinx.android.synthetic.main.home_user_item.view.*


class UsersAdapter(private val data: List<User>) : RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var counter: PomodoroTimer? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_user_item, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.view.emailTextView.text = data[position].email.split("@")[0]
        holder.counter?.cancel()
        holder.counter = PomodoroTimer.create(holder.view.pomodoroTextView, data[position], android.R.color.black, R.color.pomodoroUserDisabled, object: PomodoroTimerListener{
            override fun working() {
                renderWorking(holder)
            }

            override fun finished() {
                renderTakingARest(holder)
            }
        })

        if (holder.counter != null) {
            renderWorking(holder)
        } else {
            renderTakingARest(holder)
        }

        val avatarId = data[position].email.length % 4
        when (avatarId) {
            0 -> holder.view.avatarImageView.setImageResource(R.drawable.avatar1)
            1 -> holder.view.avatarImageView.setImageResource(R.drawable.avatar2)
            2 -> holder.view.avatarImageView.setImageResource(R.drawable.avatar3)
            3 -> holder.view.avatarImageView.setImageResource(R.drawable.avatar4)
        }

        holder.view.setOnClickListener {
            holder.view.context.startActivity(UserActivity.newIntent(holder.view.context, data[position].email))
        }
    }

    private fun renderWorking(holder: MyViewHolder){
        holder.view.pomodoroMessageTextView.text = "working..."
    }

    private fun renderTakingARest(holder: MyViewHolder){
        holder.view.pomodoroMessageTextView.text = "taking a rest"
    }


    override fun getItemCount() = data.size
}
