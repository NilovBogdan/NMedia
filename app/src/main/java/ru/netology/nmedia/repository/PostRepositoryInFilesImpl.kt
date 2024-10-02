package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import java.math.RoundingMode
import java.text.DecimalFormat

class PostRepositoryInFilesImpl(private val context: Context) : PostRepository {
    companion object{
        private const val FILE_NAME = "posts.json"
        private val gson = Gson()
    }
    private val typeToken = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private var nextId = 1L
    private var posts = emptyList<Post>()
        set(value){
            field = value
            sync()
        }
    private val data = MutableLiveData(posts)
    init {
        val file = context.filesDir.resolve(FILE_NAME)
        if (file.exists()){
            context.openFileInput(FILE_NAME).bufferedReader().use {
                posts = gson.fromJson(it, typeToken)
                nextId = posts.maxOfOrNull { it.id }?.inc() ?: 1
                data.value = posts
            }
        }
    }
    private fun sync(){
        context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
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
                        like = logicLikesAndRepost(countLike.toDouble())
                    )
                } else {
                    countLike = it.countLikes
                    countLike++
                    it.copy(
                        countLikes = countLike,
                        likedByMe = true,
                        like = logicLikesAndRepost(countLike.toDouble())
                    )
                }
            } else {
                it
            }
        }

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
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "Нетология. Университет интернет-профессий будущего",
                    published = "21 мая в 18:36",
                    like = "0",
                    repost = "0",
                    views = "0"
                )
            ) + posts
            data.value = posts
        } else {
            posts = posts.map { if (it.id != post.id) it else it.copy(content = post.content) }
            data.value = posts
        }

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