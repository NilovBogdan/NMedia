package ru.netology.nmedia.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRoomImpl
import kotlin.concurrent.thread

private val empty = Post(
    id = 0L,
    "",
    "",
    "",
    0,
    0,
    "",
    false,
    "null",
)
class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryRoomImpl()
    private val _data = MutableLiveData(FeedState())
    val data:LiveData<FeedState> = _data
    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
    fun playVideo(url: String) = repository.playVideo(url)
    fun logicLikeAndRepost(count: Double): String = repository.logicLikeAndRepost(count)
    val edited = MutableLiveData(empty)

    init {
        load()
    }

    fun applyChangesAndSave(newText: String){
        edited.value?.let {
            val text = newText.trim()
            if (text != it.content){
                repository.save(it.copy(content = text))
                edited.value = empty
            }

        }
    }
    fun edit(post: Post){
        if (post.id == 0L){
            edited.value = empty
        }
        edited.value = post
    }
    fun cancelEdit() {
        edited.value = empty
    }
    fun load(){
        thread {
            _data.postValue((FeedState(loading = true)))
            try {
               val posts =  repository.getAll()
                FeedState(posts = posts, empty = posts.isEmpty())
           }catch (e: Exception){
                FeedState(error = true)
            }.let(_data::postValue)

        }
    }
}