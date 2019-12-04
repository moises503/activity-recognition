package la.handy.activiyrecognition

import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHandler(base: Context) : ContextWrapper(base) {

    private var manager: NotificationManager? = null
    private val CHANNEL_HIGH_NAME = "HIGH CHANNEL"
    private val CHANNEL_LOW_NAME = "LOW CHANNEL"
    private val SUMMARY_GROUP_ID = 1001
    private val SUMMARY_GROUP_NAME = "GROUPING"
    private var intent: Intent? = null

    private val notificationIcon: Int
        get() {
            return R.drawable.ic_launcher_background
        }

    init {
        createChannels()
    }

    private fun getManager(): NotificationManager {
        manager?.let {
            return it
        }
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager!!
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= 26) {
            val highChannel = NotificationChannel(
                CHANNEL_HIGH_ID, CHANNEL_HIGH_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            highChannel.enableLights(true)
            highChannel.lightColor = Color.YELLOW
            highChannel.setShowBadge(true)
            highChannel.enableVibration(true)
            highChannel.vibrationPattern = longArrayOf(100, 200, 300, 300, 200, 100)

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            highChannel.setSound(defaultSoundUri, null)

            val lowChannel = NotificationChannel(
                CHANNEL_LOW_ID, CHANNEL_LOW_NAME,
                NotificationManager.IMPORTANCE_LOW
            )

            lowChannel.enableLights(true)
            lowChannel.lightColor = Color.YELLOW
            lowChannel.setShowBadge(true)
            lowChannel.enableVibration(true)
            lowChannel.vibrationPattern = longArrayOf(100, 200, 300, 300, 200, 100)

            val defaultSoundUris = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            lowChannel.setSound(defaultSoundUris, null)

            getManager().createNotificationChannel(highChannel)
            getManager().createNotificationChannel(lowChannel)
        }
    }

    fun createNotification(
        title: String,
        message: String,
        isHighImportance: Boolean,
        defaultClass: Class<out Activity>
    ): Notification.Builder? {
        intent = Intent(this, defaultClass)
        intent?.apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        return if (Build.VERSION.SDK_INT >= 26) {
            if (isHighImportance) {
                this.createNotificationWithChannel(
                    title, message,
                    CHANNEL_HIGH_ID
                )
            } else this.createNotificationWithChannel(
                title, message,
                CHANNEL_LOW_ID
            )
        } else this.createNotificationWithoutChannel(title, message)
    }

    private fun createNotificationWithChannel(
        title: String,
        message: String,
        channelId: String
    ): Notification.Builder? {
        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText(message)
        bigText.setBigContentTitle("")
        bigText.setSummaryText("")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = Notification.Builder(applicationContext, channelId)
                .setContentTitle(title)
                .setStyle(Notification.BigTextStyle().bigText(message))
                .setColor(Color.parseColor("#312445"))
                .setSmallIcon(notificationIcon)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setShowWhen(true)
                .setGroup(SUMMARY_GROUP_NAME)
                .setAutoCancel(true)
            val pendingIntent = PendingIntent.getActivity(
                applicationContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT
            )
            notification.setContentIntent(pendingIntent)
            return notification
        }

        return null
    }

    private fun createNotificationWithoutChannel(
        title: String,
        message: String
    ): Notification.Builder {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notification = Notification.Builder(this)
            .setContentTitle(title)
            .setStyle(Notification.BigTextStyle().bigText(message))
            .setPriority(Notification.PRIORITY_HIGH)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setSmallIcon(notificationIcon)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setDefaults(Notification.DEFAULT_LIGHTS)
            .setSound(defaultSoundUri)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        notification.setContentIntent(pendingIntent)
        return notification
    }

    fun publishNotificationSummaryGroup(isHighImportance: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = if (isHighImportance) CHANNEL_HIGH_ID else CHANNEL_LOW_ID
            val summaryNotification = Notification.Builder(
                applicationContext,
                channelId
            )
                .setSmallIcon(notificationIcon)
                .setGroup(SUMMARY_GROUP_NAME)
                .setGroupSummary(true)
                .build()
            getManager().notify(SUMMARY_GROUP_ID, summaryNotification)
        } else {
            val notif = NotificationCompat.Builder(applicationContext)
                .setSmallIcon(notificationIcon)
                .setGroup(SUMMARY_GROUP_NAME)
                .build()

            notif.number = 1

            getManager().notify(SUMMARY_GROUP_ID, notif)
        }
    }

    companion object {
        val CHANNEL_HIGH_ID = "1"
        val CHANNEL_LOW_ID = "2"
    }
}