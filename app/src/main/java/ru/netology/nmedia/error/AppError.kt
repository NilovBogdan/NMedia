package ru.netology.nmedia.error

import androidx.core.app.NotificationCompat.MessagingStyle.Message

sealed class AppError(val text: String): Exception()

class ApiError(val code: Int, val msg: String): AppError(msg)
object NetworkError: AppError("Network error")
object UnknownError: AppError("Unknown error")