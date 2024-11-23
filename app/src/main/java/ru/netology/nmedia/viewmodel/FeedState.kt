package ru.netology.nmedia.viewmodel

import ru.netology.nmedia.dto.Post

data class FeedState(
    val posts: List<Post> = emptyList(),
    val error: Boolean = false,
    val loading: Boolean = false,
    val empty: Boolean = false
)