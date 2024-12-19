package ru.netology.nmedia.viewmodel

import ru.netology.nmedia.dto.Post

data class FeedModel (
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false
)
data class FeedModelState(
    val loading: Boolean = false,
    val error: FeedError = FeedError.NONE,
    val refreshing: Boolean = false
)
enum class FeedError{
    API, NETWORK, UNKNOWN, NONE
}