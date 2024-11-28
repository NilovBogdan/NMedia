package ru.netology.nmedia.repository
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long): Post
    fun unLikeById(id: Long): Post
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post): Post
    fun playVideo(url: String)
    fun logicLikeAndRepost(count: Double): String
}