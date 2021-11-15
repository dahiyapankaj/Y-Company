package com.y.company.data.firestore


sealed class ApiResponse<T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error<T>(val errors: List<Failure>) :
        ApiResponse<T>()
}

data class Failure(
    val code: String,
    val message: String = "Something went wrong",
    val exception: Exception? = null
) {
    companion object {
        val NoResponse = Failure(
            "NO_RESPONSE",
            "No Api response returned"
        )
    }
}