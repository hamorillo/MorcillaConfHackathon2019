package com.bankmorcillaconf.app.repository

import android.util.Log
import com.bankmorcillaconf.app.model.Push
import com.bankmorcillaconf.app.util.ResultListener
import com.bankmorcillaconf.app.util.serializeToMap
import com.google.firebase.firestore.FirebaseFirestore

class PushRepository {

    companion object {
        const val PUSHES = "push"
        const val TAG = "PushRepository"
    }

    var db = FirebaseFirestore.getInstance()

    fun sendPush(push: Push, result: ResultListener<Unit, Unit>) {
        val pushMap: Map<String, Any> = push.serializeToMap()

        // Add a new document with auto-generated ID
        db.collection(PUSHES)
            .add(pushMap)
            .addOnSuccessListener {
                Log.d(TAG, "Push added")
                result.onSuccess(Unit)
            }
            .addOnFailureListener { e ->
                Log.w(UserRepository.TAG, "Error adding push", e)
                result.error(Unit)
            }
    }
}