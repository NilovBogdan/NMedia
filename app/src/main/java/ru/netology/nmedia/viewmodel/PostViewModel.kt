package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRoomImpl
import ru.netology.nmedia.util.SingleLiveEvent

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
    null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryRoomImpl(AppDb.getInstance(application).postDao)
    val data = repository.data.map { FeedModel(posts = it, empty = it.isEmpty()) }
    private val _postCreated = SingleLiveEvent<Unit>()
    private val _singleError = SingleLiveEvent<Unit>()
    val singleError: LiveData<Unit>
        get() = _singleError
    private val _dataState = MutableLiveData<FeedModelState>()
           val dataState: LiveData<FeedModelState>
        get() = _dataState
    val postCreated: LiveData<Unit> = _postCreated

    init {
        load()
    }

    fun likeById(id: Long) {
        val currentState = data.value ?: return
        currentState.posts.forEach { post ->
            when {
                post.id == id && !post.likedByMe -> viewModelScope.launch {
                    try {
                        repository.likeById(id)
                    } catch (e: AppError) {
                        when (e) {
                            is ApiError -> _dataState.value =
                                FeedModelState(error = FeedError.API)
                            is NetworkError -> _dataState.value =
                                FeedModelState(error = FeedError.NETWORK)
                            is UnknownError -> _dataState.value =
                                FeedModelState(error = FeedError.UNKNOWN)
                        }
                    }
                }
                post.id == id && post.likedByMe -> viewModelScope.launch {
                    try {
                        repository.unLikeById(id)
                    }catch (e: AppError) {
                        when (e) {
                            is ApiError -> _dataState.value =
                                FeedModelState(error = FeedError.API)
                            is NetworkError -> _dataState.value =
                                FeedModelState(error = FeedError.NETWORK)
                            is UnknownError -> _dataState.value =
                                FeedModelState(error = FeedError.UNKNOWN)
                        }
                    }
                }
            }
        }
    }

//    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) {
    val currentState = data.value ?: return
    viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: AppError) {
            currentState.posts.forEach {
                if (it.id == id){
                }
            }
            when (e) {
                is ApiError -> _dataState.value = FeedModelState(error = FeedError.API)
                is NetworkError -> _dataState.value =
                    FeedModelState(error = FeedError.NETWORK)
                is UnknownError -> _dataState.value =
                    FeedModelState(error = FeedError.UNKNOWN)
            }
        }
    }
}

//    fun playVideo(url: String) = repository.playVideo(url)
    fun logicLikeAndRepost(count: Double): String = repository.logicLikeAndRepost(count)
    val edited = MutableLiveData(empty)



    fun applyChangesAndSave(newText: String) {
        edited.value?.let {
            viewModelScope.launch {
                val text = newText.trim()
                if (text != it.content) {
                    repository.save(it.copy(content = text))
                    _postCreated.postValue(Unit)
                }
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
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(loading = true)
                repository.getAll()
                _dataState.value = FeedModelState()
            } catch (e: AppError) {
                when (e) {
                    is ApiError -> _dataState.value = FeedModelState(error = FeedError.API)
                    is NetworkError -> _dataState.value = FeedModelState(error = FeedError.NETWORK)
                    is UnknownError -> _dataState.value = FeedModelState(error = FeedError.UNKNOWN)
                }
            }

        }
    }

}