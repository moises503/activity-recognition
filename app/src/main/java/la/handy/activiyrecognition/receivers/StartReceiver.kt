package la.handy.activiyrecognition.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import la.handy.activiyrecognition.services.BackgroundDetectedActivitiesService
import la.handy.activiyrecognition.services.LocationService

class StartReceiver : BroadcastReceiver() {

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
        Intent(context, LocationService::class.java).also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(it)
                return
            }
            context.startService(it)
        }
    }

}