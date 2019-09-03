package com.bankmorcillaconf.app.model

data class User(val email: String,
                val lastConnection: Long,
                val tokenPush: String? = null)