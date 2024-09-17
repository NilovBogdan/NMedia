package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel by viewModels<PostViewModel>()
        viewModel.data.observe(this) { post ->
            with(binding) {
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
                viewModel.like()

            }
            binding.reposts.setOnClickListener {
                viewModel.repost()
            }
        }

    }


}