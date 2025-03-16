package ru.netology.nmedia.postDiffCallback

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean =
        oldItem == newItem
}