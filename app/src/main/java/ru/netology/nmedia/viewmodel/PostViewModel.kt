package ru.netology.nmedia.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRoomImpl
import ru.netology.nmedia.util.SingleLiveEvent
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
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> = _postCreated
    fun likeById(id: Long) {
        val currentState = _data.value ?: return
        thread {
            try {
                _data.postValue(
                    currentState.copy(
                        posts = currentState.posts.map {
                            when {
                                it.id == id && !it.likedByMe -> repository.likeById(id)
                                it.id == id && it.likedByMe -> repository.unLikeById(id)
                                else -> return@thread
                            }

                        }

                    )
                )
            }catch (e: Exception){
                _data.postValue(currentState)
            }

        }
    }
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) {
        val currentState = _data.value ?: return
        thread {
            _data.postValue(
                currentState.copy(
                posts = currentState.posts.filter { it.id != id }
            )
            )
            try {
                repository.removeById(id)
            }catch (e: Exception){
                _data.postValue(currentState)
            }

        }
    }
    fun playVideo(url: String) = repository.playVideo(url)
    fun logicLikeAndRepost(count: Double): String = repository.logicLikeAndRepost(count)
    val edited = MutableLiveData(empty)

    init {
        load()
    }

    fun applyChangesAndSave(newText: String){
        edited.value?.let {
            thread {
                val text = newText.trim()
                if (text != it.content) {
                    repository.save(it.copy(content = text))
                    _postCreated.postValue(Unit)
                }
                edited.postValue(empty)
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