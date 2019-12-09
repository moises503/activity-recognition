package la.handy.activiyrecognition.services

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import la.handy.activiyrecognition.R
import la.handy.activiyrecognition.core.*
import java.util.*
import kotlin.collections.ArrayList

class DetectedActivitiesIntentService : IntentService(TAG) {

    private var notificationHandler: NotificationHandler? = null

    override fun onCreate() {
        super.onCreate()
        notificationHandler =
            NotificationHandler(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
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
        var movement = Constants.ON_UNKNOWN

        when (type) {
            DetectedActivity.IN_VEHICLE -> {
                label = "You are in Vehicle"
                movement = Constants.ON_WALK
            }
            DetectedActivity.ON_BICYCLE -> {
                label = "You are on Bicycle"
                movement = Constants.ON_WALK
            }
            DetectedActivity.ON_FOOT -> {
                label = "You are on Foot"
                movement = Constants.ON_WALK
            }
            DetectedActivity.RUNNING -> {
                label = "You are Running"
                movement = Constants.ON_WALK
            }
            DetectedActivity.STILL -> {
                label = "You are Still"
                movement = Constants.ON_STAY
            }
            DetectedActivity.WALKING -> {
                label = "You are Walking"
                movement = Constants.ON_WALK
            }
            DetectedActivity.UNKNOWN -> {
                label = "Unkown Activity"
                movement = Constants.ON_UNKNOWN
            }
        }
        Log.e(TAG, "User activity: $label, Confidence: $confidence")

        if (confidence > Constants.CONFIDENCE) {
            this.saveMovementStatus(movement)
            if (movement == Constants.ON_STAY) {
                this.saveWhenUserIsStay(Date().toString("dd-MM-yyyy HH:mm:ss"))
            }
        }
    }

    companion object {
        private val TAG = DetectedActivitiesIntentService::class.java.simpleName
    }

}