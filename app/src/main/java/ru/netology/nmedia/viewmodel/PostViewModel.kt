package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

private val empty = Post(
    id = 0L,
    "",
    0L,
    "",
    "",
    "",
    0,
    0,
    "",
    false,
    "null",
    null,
)

@HiltViewModel
class PostViewModel@Inject constructor(
    private val repository: PostRepository,
     appAuth: AppAuth
) : ViewModel() {
    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<PagingData<Post>> = appAuth.authState
        .flatMapLatest {
            cached.map { pagingData ->
                pagingData.map { post ->
                    post.copy(ownedByMe = post.authorId == it?.id)
                }
            }
        }

//    val newerCount: LiveData<Int> = data.switchMap {
//        val newerPostId = it.posts.firstOrNull()?.id ?: 0L
//        repository.getNewerCount(newerPostId).asLiveData(Dispatchers.Default)
//    }

    private val _postCreated = SingleLiveEvent<Unit>()
    private val _singleError = SingleLiveEvent<Unit>()
    val singleError: LiveData<Unit>
        get() = _singleError
    private val _dataState = MutableLiveData<FeedModelState>()
    val postCreated: LiveData<Unit> = _postCreated
    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?> = _photo

//    init {
//        load()
//    }

    fun readAll() {
        viewModelScope.launch {
            try {
                repository.readAll()
            } catch (e: AppError) {
                when (e) {
                    is ApiError -> _dataState.value = FeedModelState(error = FeedError.API)
                    is NetworkError -> _dataState.value = FeedModelState(error = FeedError.NETWORK)
                    is UnknownError -> _dataState.value = FeedModelState(error = FeedError.UNKNOWN)
                }
            }
        }
    }

    fun checkAuth(check: Boolean): Boolean {
        return check
    }

    fun likeById(id: Long) {
        val currentState = data
        currentState.map {
            it.map { post ->
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

                    else -> _dataState.value =
                        FeedModelState(error = FeedError.UNKNOWN)
                }
            }
        }
    }

        //    fun shareById(id: Long) = repository.shareById(id)
        fun removeById(id: Long) {
            viewModelScope.launch {
                try {
                    repository.removeById(id)
                } catch (e: AppError) {
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
            edited.value?.let { post ->
                viewModelScope.launch {
                    val text = newText.trim()
                    try {
                        if (text != post.content) {
                            _photo.value?.let {
                                repository.saveWithAttachment(post.copy(content = text), it)
                            } ?: repository.save(post.copy(content = text))
                            _postCreated.postValue(Unit)
                        }
                    } catch (e: AppError) {
                        when (e) {
                            is ApiError -> _dataState.value = FeedModelState(error = FeedError.API)
                            is NetworkError -> _dataState.value =
                                FeedModelState(error = FeedError.NETWORK)

                            is UnknownError -> _dataState.value =
                                FeedModelState(error = FeedError.UNKNOWN)
                        }
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
                        is NetworkError -> _dataState.value =
                            FeedModelState(error = FeedError.NETWORK)

                        is UnknownError -> _dataState.value =
                            FeedModelState(error = FeedError.UNKNOWN)
                    }
                }

            }
        }

        fun savePhoto(photoModel: PhotoModel?) {
            _photo.value = photoModel
        }


    }
