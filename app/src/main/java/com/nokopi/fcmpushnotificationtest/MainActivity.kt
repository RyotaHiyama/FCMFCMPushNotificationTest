package com.nokopi.fcmpushnotificationtest

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            // TODO: Inform user that that your app will not show notifications.
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askNotificationPermission()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val notificationManager = getSystemService(NotificationManager::class.java)

            val defaultChannelId = getString(R.string.default_notification_channel_id)
            val defaultChannelName = getString(R.string.default_notification_channel_name)
            // 通知チャンネルのID、名前、重要度を設定
            val defaultChannel = NotificationChannel(
                defaultChannelId,
                defaultChannelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // 通知チャンネルの登録
            notificationManager?.createNotificationChannel(defaultChannel)

            val testChannelId = getString(R.string.test_notification_channel_id)
            val testChannelName = getString(R.string.test_notification_channel_name)
            // 通知チャンネルのID、名前、重要度を設定
            val testChannel = NotificationChannel(
                testChannelId,
                testChannelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // 通知チャンネルの説明を設定
            testChannel.description = getString(R.string.test_notification_channel_description)
            // 通知チャンネルの登録
            notificationManager?.createNotificationChannel(testChannel)
        }

        // Current Notificationトークンの取得
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    // token取得失敗
                    Log.d("getInstanceId failed ${task.exception}", "a")
                    return@OnCompleteListener
                }

                // new Instance ID token
                val token = task.result

                val msg = "InstanceID Token: $token"
                Log.d("msg", msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })

    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}