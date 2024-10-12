package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.nmedia.entity.PostEntity


@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    fun save(post: PostEntity) = if (post.id != 0L) changeContent(post.id, post.content) else insert(post)
    @Insert
    fun insert(post: PostEntity)
    @Query("UPDATE PostEntity SET content = :content WHERE id = :postId")
    fun changeContent(postId: Long, content: String)

    @Query(
        """
           UPDATE postentity SET
               likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = :id;
        """
    )
    fun likeById(id: Long)

    @Query(
        """
              UPDATE postentity SET
              repost = repost +1
              WHERE id =:id;
            """
    )
    fun shareById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeById(id: Long)

}