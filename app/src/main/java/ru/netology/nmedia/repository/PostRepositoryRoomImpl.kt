package ru.netology.nmedia.repository


import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.viewmodel.PhotoModel
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@Singleton
class PostRepositoryRoomImpl @Inject constructor(
    private val apiService: ApiService,
    private val postDao: PostDao,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb,
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 25, enablePlaceholders = false),
        remoteMediator = PostRemoteMediator(apiService, postDao, appDb, postRemoteKeyDao),
        pagingSourceFactory = { postDao.getPagingSource() },
    ).flow.map {
        it.map(PostEntity::toDto)
            .insertSeparators { previous, next ->
                if (previous?.id?.rem(5) == 0L) {
                    Ad(Random.nextLong(), "figma.jpg")
                } else {
                    null
                }
            }
    }


    private suspend fun upload(photoModel: PhotoModel): Media =
        apiService.upload(
            MultipartBody.Part.createFormData(
                "file",
                photoModel.file.name,
                photoModel.file.asRequestBody()
            )
        )
//


    override fun getNewerCount(newerId: Long): Flow<Int> = flow {
        while (true) {
            try {
                delay(10.seconds)
                val response = apiService.getNewer(newerId)
                val posts = response.body() ?: throw ApiError(response.code(), response.message())
                postDao.insert(posts.toEntity(false))
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
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val posts = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(posts.toEntity())
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
            postDao.likeById(id)
            val response = apiService.likeById(id)
            if (!response.isSuccessful) {

                throw ApiError(response.code(), response.message())
            }
        } catch (e: ApiError) {
            postDao.likeById(id)
            throw e
        } catch (e: IOException) {
            postDao.likeById(id)
            throw NetworkError
        } catch (_: Exception) {
            postDao.likeById(id)
            throw UnknownError
        }
    }

    override suspend fun unLikeById(id: Long) {
        try {
            postDao.likeById(id)
            val response = apiService.unLikeById(id)
            if (!response.isSuccessful) {

                throw ApiError(response.code(), response.message())
            }
        } catch (e: ApiError) {
            postDao.likeById(id)
            throw e
        } catch (e: IOException) {
            postDao.likeById(id)
            throw NetworkError
        } catch (_: Exception) {
            postDao.likeById(id)
            throw UnknownError
        }
    }

    override suspend fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            postDao.removeById(id)
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (_: Exception) {
            throw UnknownError
        }

    }


    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
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
            postDao.readAll()
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