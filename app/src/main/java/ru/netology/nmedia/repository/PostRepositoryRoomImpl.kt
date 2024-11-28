package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class PostRepositoryRoomImpl() : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    private companion object {
        const val BASE_URL = "http://10.0.2.2:9999/"
        val jsonType = "application/json".toMediaType()
    }

    override fun getAll(callBack: PostCallBack<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}api/slow/posts")
            .build()
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callBack.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callBack.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callBack.onError(e)
                    }
                }
            })

    }


    override fun likeById(id: Long, callBack: PostCallBack<Post>) {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts/${id}/likes")
            .post(gson.toJson(id).toRequestBody(jsonType))
            .build()
        val post = client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callBack.onError(e)
                }


                override fun onResponse(call: Call, response: Response) {
                    val body = response.body ?: throw RuntimeException("body is null")
                    try {
                        callBack.onSuccess(gson.fromJson(body.string(), Post::class.java))
                    } catch (e: Exception) {
                        callBack.onError(e)
                    }
                }
            })
    }

    override fun unLikeById(id: Long, callBack: PostCallBack<Post>) {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts/${id}/likes")
            .delete(gson.toJson(id).toRequestBody(jsonType))
            .build()
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callBack.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body ?: throw RuntimeException("body is null")
                    try {
                        callBack.onSuccess(gson.fromJson(body.string(), Post::class.java))
                    } catch (e: Exception) {
                        callBack.onError(e)
                    }
                }
            })
    }

    override fun shareById(id: Long) = TODO()

    override fun removeById(id: Long, callBack: PostCallBack<Unit>) {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts/${id}")
            .delete()
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callBack.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body ?: throw RuntimeException("body is null")
                    try {
                        callBack.onSuccess(Unit)
                    } catch (e: Exception) {
                        callBack.onError(e)
                    }
                }

            })
    }

    override fun save(post: Post, callBack: PostCallBack<Post>) {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callBack.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body ?: throw RuntimeException("body is null")
                    try {
                        callBack.onSuccess(gson.fromJson(body.string(), Post::class.java))
                    } catch (e: Exception) {
                        callBack.onError(e)
                    }
                }
            })

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