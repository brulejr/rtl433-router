package io.jrb.labs.commons.workflow

sealed class Outcome<out T : Any> {

    data class Success<out T : Any>(val value: T) : Outcome<T>()
    data class Failure<out T : Any>(val reason: FailureReason, val value: T) : Outcome<T>()
    data class Error(val message: String, val cause: Exception? = null) : Outcome<Nothing>()

    enum class FailureReason {
        DATA_ERROR_CONTINUE,
        DATA_ERROR_EXIT
    }

}