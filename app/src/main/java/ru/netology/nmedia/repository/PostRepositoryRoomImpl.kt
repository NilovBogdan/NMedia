package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class PostRepositoryRoomImpl() : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}
    private companion object{
        const val BASE_URL = "http://10.0.2.2:9999/"
        val jsonType = "application/json".toMediaType()
    }
    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}api/slow/posts")
            .build()

        val call = client.newCall(request)

        val response = call.execute()

        val body = response.body ?: throw RuntimeException("body is null")

        return gson.fromJson(body.string(), typeToken)

    }



    override fun likeById(id: Long): Post{
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts/${id}/likes")
            .post(gson.toJson(id).toRequestBody(jsonType))
            .build()
        val call = client.newCall(request)
        val response = call.execute()
        val body = response.body ?: throw RuntimeException("body is null")
        return gson.fromJson(body.string(), Post::class.java)
    }
    override fun unLikeById(id: Long): Post{
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts/${id}/likes")
            .delete(gson.toJson(id).toRequestBody(jsonType))
            .build()
        val call = client.newCall(request)
        val response = call.execute()
        val body = response.body ?: throw RuntimeException("body is null")
        return gson.fromJson(body.string(), Post::class.java)
    }

    override fun shareById(id: Long) = TODO()

    override fun removeById(id: Long) {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts/${id}")
            .delete()
            .build()

        val call = client.newCall(request)

        call.execute()
    }

    override fun save(post: Post): Post{
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        val call = client.newCall(request)

        val response = call.execute()

        val body = response.body ?: error("Body is null")

        return gson.fromJson(body.string(), Post::class.java)
    }

    override fun playVideo(url: String) {
    }


    override fun logicLikeAndRepost(count: Double): String {
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