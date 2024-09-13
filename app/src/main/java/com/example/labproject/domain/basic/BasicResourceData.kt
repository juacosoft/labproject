package com.example.labproject.domain.basic

sealed class BasicResourceData<T>(
    val data: T? = null,
    val errorType: BasicErrorType? = null
) {
    class Success<T>(data: T) : BasicResourceData<T>(data = data)
    class Error<T>(errorType: BasicErrorType) : BasicResourceData<T>(errorType = errorType)
}

enum class BasicErrorType {
    NETWORK,
    EMPTY_RESULTS,
    UNKNOWN
}