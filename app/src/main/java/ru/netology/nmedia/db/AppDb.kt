package ru.netology.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class], version = 2)
abstract class AppDb : RoomDatabase(){
    abstract val postDao: PostDao
    abstract val postRemoteKeyDao: PostRemoteKeyDao

}

