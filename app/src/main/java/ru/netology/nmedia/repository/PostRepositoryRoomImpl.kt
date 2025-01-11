package ru.netology.nmedia.repository


import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.viewmodel.PhotoModel
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.time.Duration.Companion.seconds


class PostRepositoryRoomImpl(private val dao: PostDao) : PostRepository {

    private suspend fun upload(photoModel: PhotoModel): Media =
        Api.service.upload(
            MultipartBody.Part.createFormData(
                "file",
                photoModel.file.name,
                photoModel.file.asRequestBody()
            )
        )
//
    override val data: Flow<List<Post>> = dao.getAllVisible().map (List<PostEntity> :: toDto)

    override fun getNewerCount(newerId: Long): Flow<Int> = flow {
        while (true) {
            try {
                delay(10.seconds)
                val response = Api.service.getNewer(newerId)
                val posts = response.body() ?: throw ApiError(response.code(), response.message())
                dao.insert(posts.toEntity(false))
                emit(posts.size)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
//
            }
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val response = Api.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val posts = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(posts.toEntity())
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (_: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            dao.likeById(id)
            val response = Api.service.likeById(id)
            if (!response.isSuccessful) {

                throw ApiError(response.code(), response.message())
            }
        } catch (e: ApiError) {
            dao.likeById(id)
            throw e
        } catch (e: IOException) {
            dao.likeById(id)
            throw NetworkError
        } catch (_: Exception) {
            dao.likeById(id)
            throw UnknownError
        }
    }

    override suspend fun unLikeById(id: Long) {
        try {
            dao.likeById(id)
            val response = Api.service.unLikeById(id)
            if (!response.isSuccessful) {

                throw ApiError(response.code(), response.message())
            }
        } catch (e: ApiError) {
            dao.likeById(id)
            throw e
        } catch (e: IOException) {
            dao.likeById(id)
            throw NetworkError
        } catch (_: Exception) {
            dao.likeById(id)
            throw UnknownError
        }
    }

    override suspend fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(id: Long) {
        val currentState = data
        try {
            val response = Api.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.removeById(id)
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            currentState.collect { posts ->
                posts.forEach {
                    if (it.id == id) {
                        dao.insert(PostEntity.fromDto(it))
                        throw NetworkError
                    }
                }

            }
        } catch (_: Exception) {
            currentState.collect { posts ->
                posts.forEach {
                    if (it.id == id) {
                        dao.insert(PostEntity.fromDto(it))
                        throw UnknownError
                    }
                }
            }

        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = Api.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (_: Exception) {
            throw UnknownError
        }
    }

    override suspend fun playVideo(url: String) {
        TODO("Not yet implemented")
    }

    override suspend fun readAll() {
        try {
            dao.readAll()
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (_: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, photoModel: PhotoModel) {
        val media = upload(photoModel)
        val postWithAttachment = post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
        save(postWithAttachment)
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