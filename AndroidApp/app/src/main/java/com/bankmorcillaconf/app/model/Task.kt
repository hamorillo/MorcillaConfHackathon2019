package com.bankmorcillaconf.app.model

data class Task(val id: String,
                val url: String,
                val pomodoTimeMillis: Long,
                val description: String,
                val creationDate: Long?)