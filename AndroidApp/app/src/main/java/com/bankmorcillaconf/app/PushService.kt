package com.bankmorcillaconf.app

import android.util.Log
import com.bankmorcillaconf.app.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage



class PushService : FirebaseMessagingService() {
    companion object {
        const val TAG = "PushService"
    }

    private val userRepository = UserRepository()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from!!)
    }
}