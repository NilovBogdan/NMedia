package ru.netology.nmedia.service.notification

data class NewPost(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
    val postContent: String
)