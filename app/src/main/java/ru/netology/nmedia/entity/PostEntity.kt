package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int,
    val repost: Int,
    val views: String,
    val likedByMe: Boolean,
    val urlVideo:String
){
    fun toDto() = Post(id, author, content, published, likes, repost, views, likedByMe, urlVideo)

    companion object{
        fun fromDto (post: Post) = PostEntity(post.id, post.author, post.content, post.published, post.likes, post.repost, post.views, post.likedByMe, post.urlVideo)
    }
}
