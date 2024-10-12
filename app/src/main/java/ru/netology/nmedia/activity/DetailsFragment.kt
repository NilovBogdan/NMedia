package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewAndChangePostFragment.Companion.textArg
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.FragmentDetailsBinding
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.viewmodel.PostViewModel


class DetailsFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val postId = arguments?.longArg ?: -1
        CardPostBinding.inflate(inflater, binding.container, true).apply {
            viewModel.data.observe(viewLifecycleOwner) { posts ->
                val post = posts.find { it.id == postId } ?: return@observe
                author.text = post.author
                content.text = post.content
                published.text = post.published
                reposts.text = viewModel.logicLikeAndRepost(post.repost.toDouble())
                countViews.text = post.views
                like.isChecked = post.likedByMe
                like.text = viewModel.logicLikeAndRepost(post.likes.toDouble()).toString()
                if (post.urlVideo != "null") {
                    videoGroup.visibility = View.VISIBLE
                } else {
                    videoGroup.visibility = View.INVISIBLE
                    videoGroup.visibility = View.GONE
                }
                play.setOnClickListener {
                    viewModel.playVideo(post.urlVideo)
                }
                preview.setOnClickListener {
                    viewModel.playVideo(post.urlVideo)
                }
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
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                    viewModel.shareById(post.id)
                }
                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.menu_post)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    viewModel.removeById(post.id)
                                    findNavController().navigateUp()
                                    true
                                }

                                R.id.edit -> {
                                    viewModel.edit(post)
                                    findNavController().navigate(R.id.action_detailsFragment_to_newAndChangePostFragment,
                                        Bundle().apply { textArg = post.content })
                                    true
                                }

                                else -> false
                            }
                        }
                    }.show()
                }

            }

        }
        return binding.root
    }

    companion object {
        var Bundle.longArg by LongArg
    }
}