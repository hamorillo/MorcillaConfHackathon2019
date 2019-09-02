package com.bankmorcillaconf.app.util

import com.bankmorcillaconf.app.model.User

class ResultListener<T, E>(val onSuccess: (T) -> Unit, val onError: (E) -> Unit) {
    fun success(result: T) {
        onSuccess.invoke(result)
    }
    fun error(error: E) {
        onError.invoke(error)
    }
}