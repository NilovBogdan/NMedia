package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val channelId = "notification"
    private val gson = Gson()
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    override fun onNewToken(token: String) {
        println(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val action = message.data["action"] ?: return

        try {
            when (ACTION.valueOf(action)) {
                ACTION.LIKE -> {
                    val content = message.data["content"]
                    val like = gson.fromJson<Like>(content, Like::class.java)
                    handelLike(like)
                }
                ACTION.NEW_POST ->{
                    val content = message.data["content"]
                    val post = gson.fromJson<NewPost>(content, NewPost::class.java)
                    handleNewPost(post)
                }

            }
        } catch (error: IllegalArgumentException) {
            errorNotification()
        }
    }

        private fun errorNotification() {
            val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.error_notification_title))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(Random.nextInt(100_000), notification)

    }
    private fun handelLike(like: Like){
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_user_like, like.userName, like.postAuthor))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random.nextInt(100_000), notification)
    }

    private fun handleNewPost(newPost: NewPost){
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString( R.string.notification_user_new_posted, newPost.userName))
            .setStyle(NotificationCompat.BigTextStyle().bigText(newPost.postContent))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random.nextInt(100_000), notification)
    }
}


enum class ACTION{
    LIKE,
    NEW_POST
}

data class Like (
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)
data class NewPost(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
    val postContent: String
)