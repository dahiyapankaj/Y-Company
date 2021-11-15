package com.y.company.data.firestore


sealed class ResponseState<out T> {
    object Loading : ResponseState<Nothing>()
    object Initial : ResponseState<Nothing>()
    data class Error(val failure: Failure) : ResponseState<Nothing>()
    data class Success<T>(val item: T) : ResponseState<T>()
}