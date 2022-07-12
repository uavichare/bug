package com.example.buglibrary.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.buglibrary.MainActivity
import com.example.buglibrary.R
import com.example.buglibrary.helper.AppConstant
import kotlin.random.Random

class DCAANotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            if (remoteMessage.data["type"] != null && remoteMessage.data["type"] == "general") {
                val url = remoteMessage.data["url"]
                url?.let {
                    if (it.isNotEmpty()) {
                        pictureStyleNotification(
                            remoteMessage.data["title"]!!,
                            remoteMessage.data["body"]!!, url
                        )
                    } else {
                        sendNotification(
                            remoteMessage.data["title"]!!,
                            remoteMessage.data["body"]!!
                        )
                    }

                } ?: kotlin.run {
                    sendNotification(
                        remoteMessage.data["title"]!!,
                        remoteMessage.data["body"]!!
                    )
                }
                Log.d("TAG", "Message data payload: ${remoteMessage.data}")
            }


        }
    }

    private fun pictureStyleNotification(title: String, msgBody: String, image: String) {

        Glide.with(this).asBitmap().load(image).into(object :
            CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val intent =
                    Intent(this@DCAANotificationService, MainActivity::class.java)
                intent.putExtra(AppConstant.NOTIFICATION_TYPE, "notification")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val pendingIntent = PendingIntent.getActivity(
                    this@DCAANotificationService, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT
                )
                val channelId = "Bud_img_channel"
                val notification =
                    NotificationCompat.Builder(this@DCAANotificationService, channelId)
                        .setSmallIcon(R.drawable.ic_status_notification)
                        .setContentTitle(title)
                        .setContentText(msgBody)
                        .setLargeIcon(resource)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setStyle(
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(resource)
                                .bigLargeIcon(null)
                        )
                        .build()

                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        channelId,
                        "Bud_img_channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationManager.createNotificationChannel(channel)
                }

                notificationManager.notify(Random.nextInt()/* ID of notification */, notification)
            }
        }
        )


    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(AppConstant.NOTIFICATION_TYPE, "notification")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.app_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_status_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageBody)
            )
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

}