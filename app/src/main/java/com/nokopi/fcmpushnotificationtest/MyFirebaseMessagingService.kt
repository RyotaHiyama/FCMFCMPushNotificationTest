package com.nokopi.fcmpushnotificationtest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.i("Refreshed token: ", token)

        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // tokenをserverへ送る処理
        Log.i("sendRegistrationTokenToServer", "$token")
    }

    // 通知を受信したときの処理
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.i("From:", "${remoteMessage.from}")

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            it.body?.let { body ->
                it.title?.let { title ->
                    it.channelId?.let { channelId -> sendNotification(body, title, channelId) } ?: sendNotification(body, title, getString(R.string.default_notification_channel_id))
                } ?: sendNotification(body, getString(R.string.default_notification_title), getString(R.string.default_notification_channel_id))
            }
        }
    }

    // 通知を生成して表示
    private fun sendNotification(messageBody: String, messageTitle: String, channelId: String) {
        // 通知タップ時に開くActivityを設定
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE)

        // 通知の作成
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

}