package ru.netology.nmedia.service.notification

data class PushMessage(
    val recipientId: Long?,
    val content: String,
)
