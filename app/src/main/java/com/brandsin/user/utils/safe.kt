package com.brandsin.user.utils

suspend fun <T> safe(call: suspend () -> T?): T? {
    return try {
        return call()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}