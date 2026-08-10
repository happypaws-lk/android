package lk.happypaws.app.data.remote.util

import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

object NetworkErrorParser {

    fun parseException(throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException -> "No internet connection. Check your connection and retry."
            is ConnectException -> "Unable to reach server. Try again in a few minutes."
            is SocketTimeoutException -> "Connection timed out. Please try again."
            is SSLException -> "Secure connection failed. Please try again."
            is IOException -> "Something went wrong. Please try again."
            else -> throwable.localizedMessage?.takeIf { it.isNotBlank() } ?: "Something went wrong. Please try again."
        }
    }

    fun <T> parseResponseError(
        response: Response<T>,
        defaultAuthError: String = "Incorrect email or password. Please try again.",
        conflictError: String = "Conflict occurred. Please try again."
    ): String {
        return when (response.code()) {
            400 -> "Please fill in all required fields."
            401 -> defaultAuthError
            403 -> "Access denied. You do not have permission."
            404 -> "Requested resource was not found."
            409 -> conflictError
            429 -> "Too many requests. Please wait a moment and try again."
            in 500..599 -> "Server error. We're fixing it, please try again later."
            else -> response.message().takeIf { it.isNotBlank() } ?: "Request failed (${response.code()})"
        }
    }
}

suspend inline fun <T> safeApiCall(
    defaultAuthError: String = "Incorrect email or password. Please try again.",
    conflictError: String = "Conflict occurred. Please try again.",
    crossinline apiCall: suspend () -> Response<T>
): Result<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception(NetworkErrorParser.parseResponseError(response, defaultAuthError, conflictError)))
        }
    } catch (e: Throwable) {
        Result.failure(Exception(NetworkErrorParser.parseException(e), e))
    }
}
