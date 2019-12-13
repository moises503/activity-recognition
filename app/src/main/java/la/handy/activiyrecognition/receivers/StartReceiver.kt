package la.handy.activiyrecognition.receivers

import android.app.Application
import android.content.*
import android.os.Build
import android.os.IBinder
import android.util.Log
import la.handy.activiyrecognition.services.BackgroundDetectedActivitiesService
import la.handy.activiyrecognition.services.LocationService

class StartReceiver : BroadcastReceiver() {

    private var locationService: LocationService? = null


    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            startActivityRecognitionService(context)
            startLocationService(context)
        }
    }


    private fun startActivityRecognitionService(context: Context) {
        Intent(context, BackgroundDetectedActivitiesService::class.java).also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(it)
                return
            }
            context.startService(it)
        }
    }

    private fun startLocationService(context: Context) {
        val application = context.applicationContext as Application
        val intent = Intent(context, LocationService::class.java)
        application.startService(intent)
        application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        locationService?.let {
            it.startTracking()
            Log.d(TAG, "tracking service started")
        } ?: let {
            Log.e(TAG, "tracking service is not started")
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val name = className.className
            if (name.endsWith("LocationService")) {
                locationService = (service as LocationService.LocationServiceBinder).service
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            if (className.className == "LocationService") {
                locationService = null
            }
        }
    }

    companion object {
        const val TAG = "StartReceiver"
    }
}