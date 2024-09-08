package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var like: String,
    var repost: String,
    var views: String,
    var likedByMe: Boolean
)
