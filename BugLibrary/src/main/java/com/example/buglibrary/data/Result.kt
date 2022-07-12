package com.example.buglibrary.data

/**
 * Generic class that holds the value in its loading state
 */
data class Result<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        LOADING,
        SUCCESS,
        ERROR

    }

    companion object {
        fun <T> success(data: T): Result<T> {
            return Result(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String?, data: T? = null): Result<T> {
            return Result(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): Result<T> {
            return Result(Status.LOADING, data, null)
        }
    }
}