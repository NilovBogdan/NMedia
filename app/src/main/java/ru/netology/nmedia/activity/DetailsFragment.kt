package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.filter
import androidx.paging.flatMap
import androidx.paging.map
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.DetailsFragment.Companion.longArg
import ru.netology.nmedia.activity.NewAndChangePostFragment.Companion.textArg
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.FragmentDetailsBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel


class DetailsFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val postId = arguments?.longArg ?: -1
        CardPostBinding.inflate(inflater, binding.container, true).apply {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.data.collectLatest { state ->
                        state.map{ post ->
                            if (post is Post) {
                                if (post.id == postId) {
                                    val urlAvatar =
                                        "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
                                    val urlAttachment =
                                        "http://10.0.2.2:9999/media/${post.attachment?.url}"
                                    author.text = post.author
                                    content.text = post.content
                                    published.text = post.published
                                    reposts.text =
                                        viewModel.logicLikeAndRepost(post.repost.toDouble())
                                    countViews.text = post.views
                                    like.isChecked = post.likedByMe
                                    like.text = viewModel.logicLikeAndRepost(post.likes.toDouble())

                                    Glide.with(avatar)
                                        .load(urlAvatar)
                                        .placeholder(R.drawable.ic_loading_100dp)
                                        .error(R.drawable.ic_error_100dp)
                                        .timeout(50_000)
                                        .circleCrop()
                                        .into(avatar)

                                    Glide.with(attachment)
                                        .load(urlAttachment)
                                        .placeholder(R.drawable.ic_loading_100dp)
                                        .error(R.drawable.ic_error_100dp)
                                        .timeout(30_000)
                                        .into(attachment)
                                    if (post.attachment != null) {
                                        attachment.visibility = View.VISIBLE
                                    } else {
                                        attachment.visibility = View.INVISIBLE
                                        attachment.visibility = View.GONE
                                    }
                                    if (post.urlVideo != null) {
                                        videoGroup.visibility = View.VISIBLE
                                    } else {
                                        videoGroup.visibility = View.INVISIBLE
                                        videoGroup.visibility = View.GONE
                                    }
                                    attachment.setOnClickListener {
                                        findNavController().navigate(
                                            R.id.action_detailsFragment_to_imageFragment,
                                            Bundle().apply {
                                                textArg = urlAttachment
                                                longArg = post.id
                                            },
                                        )
                                    }
                                    //                play.setOnClickListener {
                                    //                    viewModel.playVideo(post.urlVideo)
                                    //                }
                                    //                preview.setOnClickListener {
                                    //                    viewModel.playVideo(post.urlVideo)
                                    //                }
                                    like.setOnClickListener {
                                        viewModel.likeById(post.id)
                                    }
                                    reposts.setOnClickListener {
                                        val intent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, post.content)
                                            type = "text/plain"
                                        }
                                        val shareIntent =
                                            Intent.createChooser(
                                                intent,
                                                getString(R.string.chooser_share_post)
                                            )
                                        startActivity(shareIntent)
                                        //                    viewModel.shareById(post.id)
                                    }
                                    menu.setOnClickListener {
                                        PopupMenu(it.context, it).apply {
                                            inflate(R.menu.menu_post)
                                            setOnMenuItemClickListener { item ->
                                                when (item.itemId) {
                                                    R.id.remove -> {
                                                        //                                    viewModel.removeById(post.id)
                                                        findNavController().navigateUp()
                                                        true
                                                    }

                                                    R.id.edit -> {
                                                        viewModel.edit(post)
                                                        findNavController().navigate(R.id.action_detailsFragment_to_newAndChangePostFragment,
                                                            Bundle().apply {
                                                                textArg = post.content
                                                            })
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
                    }
                }
            }

        }
        return binding.root
    }

    companion object {
        var Bundle.longArg by LongArg
        var Bundle.textArg by StringArg
    }
}