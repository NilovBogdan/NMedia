package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.DetailsFragment.Companion.longArg
import ru.netology.nmedia.databinding.FragmentImageBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.math.max

@AndroidEntryPoint
class ImageFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val urlAttachment = arguments?.textArg
        val postId = arguments?.longArg ?: -1
        val binding = FragmentImageBinding.inflate(inflater, container, false)
        viewModel.data.observe(viewLifecycleOwner) { state ->
            val post = state.posts.find { it.id == postId } ?: return@observe
            binding.like.text = viewModel.logicLikeAndRepost(post.likes.toDouble())
            binding.like.isChecked = post.likedByMe
            binding.reposts.text = viewModel.logicLikeAndRepost(post.repost.toDouble())

            Glide.with(binding.image) // Получение вложения
                .load(urlAttachment)
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .timeout(30_000)
                .into(binding.image)


            binding.image.setOnTouchListener(View.OnTouchListener { view, event -> // Выход из режима просмотра свайпом вниз
                val displayMetrics = resources.displayMetrics
                val cardHeight = binding.image.height
                val cardStart = (displayMetrics.heightPixels.toFloat() / 2) - (cardHeight / 2)
                val minSwipeDistance = 900
                val currentY = binding.image.y
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        binding.image.animate()
                            .y(cardStart)
                            .setDuration(150)
                            .start()
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            delay(1000)
                            if (currentY > minSwipeDistance) {
                                findNavController().navigateUp()
                            }
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val newY = event.rawY
                        if (newY - cardHeight < cardStart) {
                            binding.image.animate()
                                .y(max(cardStart, newY - (cardHeight / 2)))
                                .setDuration(0)
                                .start()
                        }
                    }
                }
                view.performClick()
                return@OnTouchListener true
            })


            binding.like.setOnClickListener {
                viewModel.likeById(postId)
            }


            binding.back.setOnClickListener {
                findNavController().navigateUp()
            }


            binding.reposts.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
//                    viewModel.shareById(post.id)
            }
        }


        return binding.root
    }

    companion object {
        var Bundle.textArg by StringArg
    }
}