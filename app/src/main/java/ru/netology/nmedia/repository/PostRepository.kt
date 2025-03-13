package ru.netology.nmedia.repository
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PhotoModel

interface PostRepository {
    val data: Flow<PagingData<Post>>
    fun getNewerCount(newerId: Long): Flow<Int>
    suspend fun getAll()
    suspend fun likeById(id: Long)
    suspend fun unLikeById(id: Long)
    suspend fun shareById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun playVideo(url: String)
    suspend fun readAll ()
    suspend fun saveWithAttachment(post: Post, photoModel: PhotoModel)
    fun logicLikeAndRepost(count: Double): String
}
