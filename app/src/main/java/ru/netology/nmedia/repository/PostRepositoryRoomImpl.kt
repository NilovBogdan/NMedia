package ru.netology.nmedia.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat


class PostRepositoryRoomImpl(private val dao: PostDao) : PostRepository {
override val data: LiveData<List<Post>> = dao.getAll().map (List<PostEntity>::toDto)
    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val posts = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(posts.toEntity())
        }catch (e: ApiError){
            throw e
        } catch (e: IOException) {
            throw NetworkError
        }catch(_: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            dao.likeById(id)
            val response = PostsApi.service.likeById(id)
            if (!response.isSuccessful){

                throw ApiError(response.code(), response.message())
            }
        } catch (e: ApiError){
            dao.likeById(id)
            throw e
        } catch (e: IOException) {
            dao.likeById(id)
            throw NetworkError
        }catch(_: Exception) {
            dao.likeById(id)
            throw UnknownError
        }
    }

    override suspend fun unLikeById(id: Long) {
        try {
            dao.likeById(id)
            val response = PostsApi.service.unLikeById(id)
            if (!response.isSuccessful){

                throw ApiError(response.code(), response.message())
            }
        } catch (e: ApiError){
            dao.likeById(id)
            throw e
        } catch (e: IOException) {
            dao.likeById(id)
            throw NetworkError
        }catch(_: Exception) {
            dao.likeById(id)
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        val currentState = data.value ?: return
        try {
            dao.removeById(id)
            val response = PostsApi.service.removeById(id)
            if (!response.isSuccessful){
                throw ApiError(response.code(), response.message())
            }
        } catch (e: ApiError) {
            currentState.forEach {
                if (it.id == id){
                    dao.insert(PostEntity.fromDto(it))
                }
            }
                throw e
        }catch(e: IOException) {
            currentState.forEach {
                if (it.id == id){
                    dao.insert(PostEntity.fromDto(it))
                }
            }
            throw NetworkError
        }catch (_: Exception){
            currentState.forEach {
                if (it.id == id){
                    dao.insert(PostEntity.fromDto(it))
                }
            }
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful){
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: ApiError) {
            throw e
        }catch(e: IOException) {
            throw NetworkError
        }catch (_: Exception){
            throw UnknownError
        }
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



    override suspend fun shareById(id: Long) {
        TODO("Not yet implemented")
    }





    override suspend fun playVideo(url: String) {
        TODO("Not yet implemented")
    }

}