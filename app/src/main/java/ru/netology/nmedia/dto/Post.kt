package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

sealed interface FeedItem{
    val id:Long
}

data class Post(
   override val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Int,
    val repost: Int,
    val views: String?,
    val likedByMe: Boolean,
    val urlVideo:String? = null,
    val attachment :Attachment? = null,
    val ownedByMe: Boolean = false
): FeedItem

data class Ad(
    override val id: Long,
    val image: String
):FeedItem

data class Attachment(
    val url: String,
    val type: AttachmentType,
){
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            Attachment(it.url, it.type)
        }

    }
}
