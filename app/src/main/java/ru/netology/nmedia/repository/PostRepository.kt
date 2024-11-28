package ru.netology.nmedia.repository
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(callBack: PostCallBack<List<Post>>)
    fun likeById(id: Long, callBack: PostCallBack<Post>)
    fun unLikeById(id: Long, callBack: PostCallBack<Post>)
    fun shareById(id: Long)
    fun removeById(id: Long, callBack: PostCallBack<Unit>)
    fun save(post: Post, callBack: PostCallBack<Post>)
    fun playVideo(url: String)
    fun logicLikeAndRepost(count: Double): String
}
interface PostCallBack<T>{
    fun onSuccess(result: T)
    fun onError(error: Exception)
}