package ru.netology.nmedia.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: onShareListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            liked.text = post.like
            countReposts.text = post.repost
            countViews.text = post.views
            if (post.likedByMe) like.setImageResource(R.drawable.ic_baseline_red_favorite_24)
            else like.setImageResource(R.drawable.ic_baseline_favorite_24)
        }
        binding.like.setOnClickListener {
            onLikeListener(post)
        }
        binding.reposts.setOnClickListener {
            onShareListener(post)
        }





    }
}

