package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int,
    val repost: Int,
    val views: String,
    val likedByMe: Boolean,
    val urlVideo:String
)
