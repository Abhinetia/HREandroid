package com.android.hre.others

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

// Constants for notification channels
private const val CHANNEL_ID_DOWNLOAD = "download_channel"
private  const val CHANNEL_NAME_DOWNLOAD = "Download Channel"
private  const val CHANNEL_DESCRIPTION_DOWNLOAD = "Channel for file download notifications"

class Notifications {


    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
             val channel = NotificationChannel(
             CHANNEL_ID_DOWNLOAD,
                 CHANNEL_NAME_DOWNLOAD,
              NotificationManager.IMPORTANCE_DEFAULT
             )
             channel.description = CHANNEL_DESCRIPTION_DOWNLOAD
             notificationManager.createNotificationChannel(channel)
          }
    }
}