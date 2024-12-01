package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostCallBack
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRoomImpl
import ru.netology.nmedia.util.SingleLiveEvent
import kotlin.concurrent.thread

private val empty = Post(
    id = 0L,
    "",
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
    val data: LiveData<FeedState> = _data
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> = _postCreated
    fun likeById(id: Long) {
        val currentState = _data.value ?: return
        currentState.posts.forEach { post ->
            when {
                post.id == id && !post.likedByMe -> repository.likeById(
                    id,
                    object : PostCallBack<Post> {
                        override fun onSuccess(result: Post) {
                            _data.postValue(_data.value?.posts?.let {
                                _data.value?.copy(
                                    posts = it.map {state ->
                                        when {
                                            state.id == id && !state.likedByMe -> result
                                            else -> state
                                        }
                                    }
                                )
                            })
                        }

                        override fun onError(error: Exception) {
                            _data.postValue(currentState)
                        }

                    })

                post.id == id && post.likedByMe -> repository.unLikeById(id,
                    object : PostCallBack<Post> {
                        override fun onSuccess(result: Post) {
                            _data.postValue(_data.value?.posts?.let {
                                _data.value?.copy(
                                    posts = it.map {state ->
                                        when {
                                            state.id == id && state.likedByMe -> result
                                            else -> state
                                        }
                                    }
                                )
                            })
                        }

                        override fun onError(error: Exception) {
                            _data.postValue(currentState)
                        }
                    }
                )

            }
        }

    }

    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) {
        val currentState = _data.value ?: return
        repository.removeById(id, object : PostCallBack<Unit> {
            override fun onSuccess(result: Unit) {
                _data.postValue(
                    currentState.copy(
                        posts = currentState.posts.filter { it.id != id })
                )
            }

            override fun onError(error: Exception) {
                _data.postValue(currentState)
            }

        })
    }

    fun playVideo(url: String) = repository.playVideo(url)
    fun logicLikeAndRepost(count: Double): String = repository.logicLikeAndRepost(count)
    val edited = MutableLiveData(empty)

    init {
        load()
    }

    fun applyChangesAndSave(newText: String) {
        edited.value?.let {
            val text = newText.trim()
            if (text != it.content) {
                repository.save(it.copy(content = text), object : PostCallBack<Post> {
                    override fun onSuccess(result: Post) {
                        _postCreated.postValue(Unit)
                    }

                    override fun onError(error: Exception) {
                        _data.postValue(FeedState(error = true))
                    }

                })

            }
            edited.postValue(empty)
        }
    }

    fun edit(post: Post) {
        if (post.id == 0L) {
            edited.value = empty
        }
        edited.value = post
    }

    fun cancelEdit() {
        edited.value = empty
    }

    fun load() {
        _data.postValue(FeedState(loading = true))
        repository.getAll(
            object : PostCallBack<List<Post>> {
                override fun onSuccess(result: List<Post>) {
                    _data.postValue(FeedState(posts = result, empty = result.isEmpty()))
                }

                override fun onError(error: Exception) {
                    _data.postValue(FeedState(error = true))
                }
            })

    }

}