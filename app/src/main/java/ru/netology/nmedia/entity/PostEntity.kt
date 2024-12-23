package ru.netology.nmedia.entity
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post


@Entity
data class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val repost: Int = 0,
    val views: String? = null,
    val likedByMe: Boolean,
    val visible: Boolean = true,
    val urlVideo:String? = null,
    @Embedded
    val attachment: Attachment?
){



    fun toDto() = Post(id, author, authorAvatar, content, published, likes, repost, views, likedByMe, urlVideo, attachment?.toDto())

    companion object{
        fun fromDto (post: Post, isVisible: Boolean = true) = PostEntity(post.id, post.author, post.authorAvatar, post.content, post.published, post.likes, post.repost, post.views, post.likedByMe, visible = isVisible, post.urlVideo, Attachment.fromDto(post.attachment), )
    }
}


fun List<Post>.toEntity(isVisible: Boolean = true): List<PostEntity> = map { PostEntity.fromDto(it, isVisible) }
fun List<PostEntity>.toDto() = map{it.toDto()}
