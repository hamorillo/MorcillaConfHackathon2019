package com.bankmorcillaconf.app.repository

import android.util.Log
import com.bankmorcillaconf.app.model.Pomodoro
import com.bankmorcillaconf.app.model.Task
import com.bankmorcillaconf.app.model.User
import com.bankmorcillaconf.app.repository.TaskRepository.Companion.TASKS
import com.bankmorcillaconf.app.repository.UserRepository.Companion.USERS
import com.bankmorcillaconf.app.util.ResultListener
import com.bankmorcillaconf.app.util.serializeToMap
import com.bankmorcillaconf.app.util.sha1
import com.bankmorcillaconf.app.util.toDataClass
import com.google.common.collect.Lists
import com.google.firebase.firestore.FirebaseFirestore


class PomodoroRepository {

    companion object {
        const val POMODOROS = "pomodoros"
        const val TAG = "PomodoroRepository"
    }

    var db = FirebaseFirestore.getInstance()

    val userRepository = UserRepository()

    private fun toPomodoroId(pomodoro: Pomodoro) =  pomodoro.startDate.toString()

    fun createPomodoro(user: User, task: Task, pomodoro: Pomodoro, result: ResultListener<Pomodoro, Unit>) {
        // Add a new document with a generated ID
        db.collection(USERS)
            .document(user.email.sha1())
            .collection(TASKS)
            .document(task.id)
            .collection(POMODOROS)
            .document(toPomodoroId(pomodoro))
            .set(pomodoro.serializeToMap())
            .addOnSuccessListener {
                Log.d(TAG, "pomodoro added")
                val userWithPomodoro = user.copy(currentPomodoroStartDate = pomodoro.startDate, currentPomodoroDuration = pomodoro.duration)
                userRepository.createOrUpdateUserMe(userWithPomodoro, ResultListener(
                    onSuccess = {
                        result.success(pomodoro)
                    },
                    onError = {
                        result.error(Unit)
                    }
                ))
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding pomodoro", e)
                result.error(Unit)
            }
    }

    fun getPomodoro(user: User, task: Task, pomodoro: Pomodoro, result: ResultListener<Pomodoro, Unit>) {
        getPomodoro(user, task, toPomodoroId(pomodoro), result)
    }

    fun getPomodoro(user: User, task: Task, pomodoroId: String, result: ResultListener<Pomodoro, Unit>) {
        // Add a new document with a generated ID
        db.collection(USERS)
            .document(user.email.sha1())
            .collection(TASKS)
            .document(task.id)
            .collection(POMODOROS)
            .document(pomodoroId)
            .get()
            .addOnSuccessListener { document ->
                document.data?.let {
                    Log.d(TAG, document.id + " => " + it)
                    result.success(it.toDataClass())
                } ?: run {
                    Log.w(TAG, "Error getting pomodoro")
                    result.error(Unit)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding pomodoro", e)
                result.error(Unit)
            }
    }


    fun getAllPomodoros(user: User, task: Task, result: ResultListener<List<Pomodoro>, Unit>) {
        db.collection(USERS)
            .document(user.email.sha1())
            .collection(TASKS)
            .document(task.id)
            .collection(POMODOROS)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val pomodoros = Lists.newArrayList<Pomodoro>()
                    for (document in task.result!!) {
                        Log.d(TAG, document.id + " => " + document.data)
                        pomodoros.add(document.data.toDataClass())
                    }
                    result.success(pomodoros)
                } else {
                    Log.w(TAG, "Error getting pomodoros.", task.exception)
                    result.error(Unit)
                }
            }
    }

}