package ru.netology.nmedia

import ru.netology.nmedia.dto.Post

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post)
    fun playVideo(url: String) {}
    fun details(post: Post)
}