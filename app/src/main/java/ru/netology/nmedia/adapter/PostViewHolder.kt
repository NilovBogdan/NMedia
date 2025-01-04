package ru.netology.nmedia.adapter

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.OnInteractionListener
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            val urlAvatar = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
            val urlAttachment = "http://10.0.2.2:9999/media/${post.attachment?.url}"
            author.text = post.author
            content.text = post.content
            published.text = post.published
            reposts.text = onInteractionListener.logicLikeAndRepost(post.repost.toDouble())
            countViews.text = post.views
            like.isChecked = post.likedByMe
            like.text = onInteractionListener.logicLikeAndRepost(post.likes.toDouble())
            if (post.urlVideo?.isNotBlank() == true) {
                videoGroup.visibility = View.VISIBLE
            } else {
                videoGroup.visibility = View.INVISIBLE
                videoGroup.visibility = View.GONE
            }
            play.setOnClickListener {
                onInteractionListener.playVideo(post.urlVideo)
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
            content.setOnClickListener {
                onInteractionListener.details(post)
            }
            attachment.setOnClickListener {
                onInteractionListener.fullScreenImage(post)
            }
            Glide.with(binding.attachment)
                .load(urlAttachment)
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .timeout(30_000)
                .into(binding.attachment)
            if (post.attachment != null) {
                attachment.visibility = View.VISIBLE
            } else {
                attachment.visibility = View.INVISIBLE
                attachment.visibility = View.GONE
            }
            Glide.with(binding.author)
                .load(urlAvatar)
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .timeout(50_000)
                .circleCrop()
                .into(binding.avatar)



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

