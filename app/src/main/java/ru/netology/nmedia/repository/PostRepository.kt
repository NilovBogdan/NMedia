package ru.netology.nmedia.repository
import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun likeById(id: Long)
    suspend fun unLikeById(id: Long)
    suspend fun shareById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun playVideo(url: String)
    fun logicLikeAndRepost(count: Double): String
}
