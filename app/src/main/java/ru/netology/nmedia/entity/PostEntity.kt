package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post


@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val repost: Int = 0,
    val views: String? = null,
    val likedByMe: Boolean,
    val visible: Boolean = true,
    val urlVideo: String? = null,
    @Embedded
    val attachment: Attachment?,
) {


    fun toDto() = Post(
        id = id,
        author = author,
        authorId = authorId,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likes = likes,
        repost = repost,
        views = views,
        likedByMe = likedByMe,
        urlVideo = urlVideo,
        attachment = attachment?.toDto()
    )

    companion object {
        fun fromDto(post: Post, isVisible: Boolean = true) = PostEntity(
            id = post.id,
            author = post.author,
            authorId = post.authorId,
            authorAvatar = post.authorAvatar,
            content = post.content,
            published = post.published,
            likes = post.likes,
            repost = post.repost,
            views = post.views,
            likedByMe = post.likedByMe,
            visible = isVisible,
            urlVideo = post.urlVideo,
            attachment = Attachment.fromDto(post.attachment),
        )
    }
}


fun List<Post>.toEntity(isVisible: Boolean = true): List<PostEntity> =
    map { PostEntity.fromDto(it, isVisible) }

fun List<PostEntity>.toDto() = map { it.toDto() }
