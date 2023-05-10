package ru.lyubeznyh.filemanager.domain.model

sealed class Result<T> {
    class PendingResult<T> : Result<T>()
    class ErrorResult<T>(val throwable: Throwable) : Result<T>()
    class SuccessResult<T>(val data: T) : Result<T>()
}
