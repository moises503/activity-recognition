package la.handy.activiyrecognition

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.ActivityRecognitionClient

class BackgroundDetectedActivitiesService : Service() {

    private lateinit var mIntentService: Intent
    private lateinit var mPendingIntent: PendingIntent
    private lateinit var mActivityRecognitionClient: ActivityRecognitionClient
    private var wakeLock: PowerManager.WakeLock? = null
    private var notificationHandler: NotificationHandler? = null


    private var mBinder: IBinder = LocalBinder()

    inner class LocalBinder : Binder()

    override fun onCreate() {
        super.onCreate()
        mActivityRecognitionClient = ActivityRecognitionClient(this)
        mIntentService = Intent(this, DetectedActivitiesIntentService::class.java)
        mPendingIntent = PendingIntent.getService(
            this,
            1,
            mIntentService,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationHandler = NotificationHandler(applicationContext)
        val notification = notificationHandler?.createNotification(
            "Handy Activity Recognition esta activo actualmente",
            "El servicio se esta ejecutando", true, MainActivity::class.java
        )?.build()
        startForeground(1, notification)
        requestActivityUpdatesButtonHandler()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    @SuppressLint("WakelockTimeout")
    private fun requestActivityUpdatesButtonHandler() {
        val task = mActivityRecognitionClient.requestActivityUpdates(
            Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
            mPendingIntent
        )

        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "BackgroundDetectedActivitiesService::lock"
                ).apply {
                    acquire()
                }
            }

        task?.addOnSuccessListener {
            Toast.makeText(
                applicationContext,
                "Successfully requested activity updates",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        task?.addOnFailureListener { e ->
            Log.e("BACKGROUND_DETECTED", "${e.message}")
            Toast.makeText(
                applicationContext,
                "Requesting activity updates failed to start",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    fun removeActivityUpdatesButtonHandler() {
        val task = mActivityRecognitionClient.removeActivityUpdates(
            mPendingIntent
        )
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            Log.e("ACTIVITY_RECOGNITION", "Service stopped without being started: ${e.message}")
        }
        task?.addOnSuccessListener {
            Toast.makeText(
                applicationContext,
                "Removed activity updates successfully!",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        task?.addOnFailureListener {
            Toast.makeText(
                applicationContext, "Failed to remove activity updates!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}