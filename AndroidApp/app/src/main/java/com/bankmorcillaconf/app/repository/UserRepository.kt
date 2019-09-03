package com.bankmorcillaconf.app.repository

import android.util.Log
import com.bankmorcillaconf.app.model.Task
import com.bankmorcillaconf.app.model.User
import com.bankmorcillaconf.app.util.ResultListener
import com.bankmorcillaconf.app.util.serializeToMap
import com.bankmorcillaconf.app.util.sha1
import com.bankmorcillaconf.app.util.toDataClass
import com.google.common.collect.Lists
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class UserRepository {

    companion object {
        const val USERS = "users"
        const val TAG = "UserRepository"

        var staticUser: User? = null
    }

    var db = FirebaseFirestore.getInstance()

    fun getAllUsers(result: ResultListener<List<User>, Unit>) {
        db.collection(USERS)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val users = Lists.newArrayList<User>()
                    for (document in task.result!!) {
                        Log.d(TAG, document.id + " => " + document.data)
                        users.add(document.data.toDataClass())
                    }
                    result.success(users)
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                    result.error(Unit)
                }
            }
    }

    fun getUserMe(email: String, result: ResultListener<User, Unit>) {
        db.collection(USERS)
            .document(email.sha1())
            .get()
            .addOnSuccessListener { document ->
                document.data?.let {
                    Log.d(TAG, document.id + " => " + it)
                    if (email == staticUser?.email) {
                        staticUser = it.toDataClass()
                    }
                    result.success(it.toDataClass())
                } ?: run {
                    Log.w(TAG, "Error getting document")
                    result.error(Unit)
                }
            }
    }

    fun createOrUpdateUserMe(user: User, result: ResultListener<User, Unit>) {
        // Create a new user with a first and last name
        val userMap: Map<String, Any> = user.serializeToMap()

        // Add a new document with a generated ID
        db.collection(USERS)
            .document(user.email.sha1())
            .set(userMap, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added")
                getUserMe(user.email, result)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                result.error(Unit)
            }
    }

}