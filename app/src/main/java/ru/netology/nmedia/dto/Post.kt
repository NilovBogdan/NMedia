package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val like: String,
    val repost: String,
    val views: String,
    val likedByMe: Boolean,
    val countLikes: Int,
    val countShare: Int,
    val urlVideo:String
)
