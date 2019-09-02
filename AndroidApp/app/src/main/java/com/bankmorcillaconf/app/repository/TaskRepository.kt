package com.bankmorcillaconf.app.repository

import android.util.Log
import com.bankmorcillaconf.app.model.Task
import com.bankmorcillaconf.app.model.User
import com.bankmorcillaconf.app.repository.UserRepository.Companion.USERS
import com.bankmorcillaconf.app.util.ResultListener
import com.bankmorcillaconf.app.util.serializeToMap
import com.bankmorcillaconf.app.util.sha1
import com.bankmorcillaconf.app.util.toDataClass
import com.google.common.collect.Lists
import com.google.firebase.firestore.FirebaseFirestore


class TaskRepository {

    companion object {
        const val TASKS = "tasks"
        const val TAG = "TaskRepository"
    }

    var db = FirebaseFirestore.getInstance()


    fun createTask(user: User, task: Task, result: ResultListener<Task, Unit>) {
        // Add a new document with a generated ID
        db.collection(USERS)
            .document(user.email.sha1())
            .collection(TASKS)
            .document(task.id)
            .set(task.serializeToMap())
            .addOnSuccessListener {
                Log.d(TAG, "task added")
                getTask(user, task, result)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding task", e)
                result.error(Unit)
            }
    }

    fun getTask(user: User, task: Task, result: ResultListener<Task, Unit>) {
        // Add a new document with a generated ID
        db.collection(USERS)
            .document(user.email.sha1())
            .collection(TASKS)
            .document(task.id)
            .get()
            .addOnSuccessListener { document ->
                document.data?.let {
                    Log.d(TAG, document.id + " => " + it)
                    result.success(it.toDataClass())
                } ?: run {
                    Log.w(TAG, "Error getting document")
                    result.error(Unit)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding task", e)
                result.error(Unit)
            }
    }


    fun getAllTasks(user: User, result: ResultListener<List<Task>, Unit>) {
        db.collection(USERS)
            .document(user.email.sha1())
            .collection(TASKS)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val tasks = Lists.newArrayList<Task>()
                    for (document in task.result!!) {
                        Log.d(TAG, document.id + " => " + document.data)
                        tasks.add(document.data.toDataClass())
                    }
                    result.success(tasks)
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                    result.error(Unit)
                }
            }
    }

}