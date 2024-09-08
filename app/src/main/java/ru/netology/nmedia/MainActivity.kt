package ru.netology.nmedia

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val post = Post(
            1,
            "Нетология. Университет интернет-профессий будущего",
            "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            "21 мая в 18:36",
            "0",
            "0",
            "0",
            false
        )
        var countLikes = 0
        var countRepost = 0
        with(binding) {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            liked.text = post.like
            countReposts.text = post.repost
            countViews.text = post.views
            like.setOnClickListener {
                post.likedByMe = !post.likedByMe
                like.setImageResource(
                    if (post.likedByMe) {
                        countLikes++
                        R.drawable.ic_baseline_red_favorite_24
                    } else {
                        countLikes--
                        R.drawable.ic_baseline_favorite_24
                    }
                )
                liked.text = logicLikesAndRepost((countLikes).toDouble())
            }
            reposts.setOnClickListener {
                countRepost++
                countReposts.text = logicLikesAndRepost((countRepost).toDouble())
            }
        }

    }

    fun logicLikesAndRepost(count: Double): String {
        val cl: Double = (count / 1000)
        var result = "${count.toInt()}"
        if (count >= 1_000 && count < 10_000) {
            val df = DecimalFormat("#.#")
            df.roundingMode = RoundingMode.DOWN
            result = df.format(cl) + "K"
        }
        if (count >= 10_000 && count < 1_000_000) {
            val df = DecimalFormat("#")
            df.roundingMode = RoundingMode.DOWN
            result = df.format(cl) + "K"
        }
        if (count >= 1_000_000) {
            val df = DecimalFormat("#.#")
            df.roundingMode = RoundingMode.DOWN
            result = df.format(cl / 1000) + "M"
        }
        return result
    }


}