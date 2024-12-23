package ru.netology.nmedia.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity


@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE visible = 1 ORDER BY id DESC")
    fun getAllVisible(): Flow<List<PostEntity>>

//    @Query("SELECT COUNT(*) == 0 FROM PostEntity WHERE visible = 0")
//    fun hiddenPostsCount(): Flow<Int>

    suspend fun save(post: PostEntity) = if (post.id != 0L) changeContent(post.id, post.content) else insert(post)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :postId")
    suspend fun changeContent(postId: Long, content: String)


    @Query(
        """
           UPDATE postentity SET
               likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = :id;
        """
    )
    suspend fun likeById(id: Long)


    @Query(
        """
              UPDATE postentity SET
              repost = repost +1
              WHERE id =:id;
            """
    )
    suspend fun shareById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("UPDATE PostEntity SET visible = 1")
    suspend fun readAll()

}