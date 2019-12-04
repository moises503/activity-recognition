package la.handy.activiyrecognition

import android.app.*
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import java.util.*
import kotlin.collections.ArrayList

class DetectedActivitiesIntentService : IntentService(TAG) {

    private var notificationHandler: NotificationHandler? = null

    override fun onCreate() {
        super.onCreate()
        notificationHandler = NotificationHandler(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }

    override fun onHandleIntent(intent: Intent?) {
        val result = ActivityRecognitionResult.extractResult(intent)
        val detectedActivities = result.probableActivities as ArrayList<*>

        for (activity in detectedActivities) {
            broadcastActivity(activity as DetectedActivity)
        }
    }

    private fun broadcastActivity(activity: DetectedActivity) {
        handleUserActivity(activity.type, activity.confidence)
    }


    private fun handleUserActivity(type: Int, confidence: Int) {
        var label = getString(R.string.activity_unknown)

        when (type) {
            DetectedActivity.IN_VEHICLE -> {
                label = "You are in Vehicle"
            }
            DetectedActivity.ON_BICYCLE -> {
                label = "You are on Bicycle"
            }
            DetectedActivity.ON_FOOT -> {
                label = "You are on Foot"
            }
            DetectedActivity.RUNNING -> {
                label = "You are Running"
            }
            DetectedActivity.STILL -> {
                label = "You are Still"
            }
            DetectedActivity.TILTING -> {
                label = "Your phone is Tilted"
            }
            DetectedActivity.WALKING -> {
                label = "You are Walking"
            }
            DetectedActivity.UNKNOWN -> {
                label = "Unkown Activity"
            }
        }

        Log.e(TAG, "User activity: $label, Confidence: $confidence")

        if (confidence > Constants.CONFIDENCE) {
            val notificationBuilder = notificationHandler?.createNotification(
                "Hemos detectado movimiento",
                label,
                true,
                MainActivity::class.java
            )
            notificationBuilder?.let {
                val r = Random()
                val notificationId = r.nextInt(80 - 65) + 65
                NotificationManagerCompat.from(applicationContext).notify(notificationId, it.build())
            }
        }
    }

    companion object {
        private val TAG = DetectedActivitiesIntentService::class.java.simpleName
    }

}