package ru.netology.nmedia.adapter

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.OnInteractionListener
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            reposts.text = post.repost
            countViews.text = post.views
            like.isChecked = post.likedByMe
            like.text = post.like
            if (post.urlVideo.isNotBlank()){
                videoGroup.visibility = View.VISIBLE
            }else{
                videoGroup.visibility = View.INVISIBLE
                videoGroup.visibility = View.GONE
            }
            play.setOnClickListener { onInteractionListener.playVideo(post.urlVideo)
            }
            preview.setOnClickListener {
                onInteractionListener.playVideo(post.urlVideo)
            }
            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            reposts.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }


    }
}

