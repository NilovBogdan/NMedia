package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import java.math.RoundingMode
import java.text.DecimalFormat

class PostRepositoryInMemoryImpl : PostRepository {
    private var post = Post(
        1,
        "Нетология. Университет интернет-профессий будущего",
        "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        "21 мая в 18:36",
        "0",
        "0",
        "0",
        false
    )
    private var countLikes = 0
    private var countRepost = 0
    private val data = MutableLiveData(post)
    override fun get() = data
    override fun like() {
        val newLike: String
        if (post.likedByMe) countLikes-- else countLikes++
        if (countLikes < 1000) {
            newLike = countLikes.toString()
        } else newLike = logicLikesAndRepost(countLikes.toDouble())
        post = post.copy(likedByMe = !post.likedByMe, like = newLike)
        data.value = post
    }

    override fun repost() {
        countRepost++
        post = post.copy(repost = logicLikesAndRepost(countRepost.toDouble()))
        data.value = post
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