package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Int,
    val repost: Int,
    val views: String,
    val likedByMe: Boolean,
    val urlVideo:String,
    val attachment :Attachment? = null
)
data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType,
)
