package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import java.math.RoundingMode
import java.text.DecimalFormat

class PostRepositorySQLiteImpl(private val dao: PostDao) : PostRepository {
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)
    init {
        posts = dao.getAll()
        data.value = posts
    }
    override fun getAll(): LiveData<List<Post>> = data
    override fun likeById(id: Long) {
        var countLike: Int
        posts = posts.map {
            if (it.id == id) {
                if (it.likedByMe) {
                    countLike = it.countLikes
                    countLike--
                    it.copy(
                        countLikes = countLike,
                        likedByMe = false,
                        likes = logicLikesAndRepost(countLike.toDouble())
                    )
                } else {
                    countLike = it.countLikes
                    countLike++
                    it.copy(
                        countLikes = countLike,
                        likedByMe = true,
                        likes = logicLikesAndRepost(countLike.toDouble())
                    )
                }
            } else {
                it
            }
        }
        dao.likeById(id)
        data.value = posts
    }

    override fun shareById(id: Long) {
        var countRep: Int
        posts = posts.map {
            if (it.id == id) {
                countRep = it.countShare
                countRep++
                it.copy(
                    countShare = it.countShare + 1,
                    repost = logicLikesAndRepost(countRep.toDouble())
                )
            } else {
                it
            }
        }
        dao.shareById(id)
        data.value = posts
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
        posts = posts.filter { it.id != id }
        data.value = posts

    }

    override fun save(post: Post) {
        val id = post.id
        val saved = dao.save(post)
        posts = if (id == 0L) {
            listOf(saved) + posts
        } else {
            posts.map {
                if (it.id != id) it else saved
            }
        }
        data.value = posts
    }

    override fun playVideo(url: String) {
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