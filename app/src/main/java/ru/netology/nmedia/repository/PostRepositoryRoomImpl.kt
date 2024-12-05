package ru.netology.nmedia.repository


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post
import java.math.RoundingMode
import java.text.DecimalFormat


class PostRepositoryRoomImpl() : PostRepository {
    override fun getAll(callBack: PostCallBack<List<Post>>) {
        PostApi.service.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        callBack.onError(RuntimeException(response.message()))
                        return
                    }
                    val body = response.body() ?: throw RuntimeException("body is null")
                    callBack.onSuccess(body)


                }

                override fun onFailure(call: Call<List<Post>>, e: Throwable) {
                    callBack.onError(e)
                }

            })


    }


    override fun likeById(id: Long, callBack: PostCallBack<Post>) {
        PostApi.service.likeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callBack.onError(RuntimeException(response.message()))
                        return
                    }
                    val body = response.body() ?: throw RuntimeException("body is null")
                    callBack.onSuccess(body)
                }

                override fun onFailure(call: Call<Post>, e: Throwable) {
                    callBack.onError(e)
                }

            })
    }

    override fun unLikeById(id: Long, callBack: PostCallBack<Post>) {
        PostApi.service.unLikeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callBack.onError(RuntimeException(response.message()))
                        return
                    }
                    val body = response.body() ?: throw RuntimeException("body is null")
                    callBack.onSuccess(body)
                }

                override fun onFailure(call: Call<Post>, e: Throwable) {
                    callBack.onError(e)
                }

            })
    }

    override fun shareById(id: Long) = TODO()

    override fun removeById(id: Long, callBack: PostCallBack<Unit>) {
        PostApi.service.removeById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callBack.onError(RuntimeException(response.message()))
                        return
                    }
                    val body = response.body() ?: throw RuntimeException("body is null")
                    callBack.onSuccess(body)
                }

                override fun onFailure(call: Call<Unit>, e: Throwable) {
                    callBack.onError(e)
                }

            })
    }

    override fun save(post: Post, callBack: PostCallBack<Post>) {
        PostApi.service.save(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callBack.onError(RuntimeException(response.message()))
                        return
                    }
                    val body = response.body() ?: throw RuntimeException("body is null")
                    callBack.onSuccess(body)
                }

                override fun onFailure(call: Call<Post>, e: Throwable) {
                    callBack.onError(e)
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