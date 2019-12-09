package la.handy.activiyrecognition

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri.fromParts
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import la.handy.activiyrecognition.core.isServiceRunning
import la.handy.activiyrecognition.services.BackgroundDetectedActivitiesService
import la.handy.activiyrecognition.services.LocationService


class MainActivity : AppCompatActivity() {

    private var locationService: LocationService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startTracking()
        val intent = Intent(this.application, LocationService::class.java)
        if (!this.isServiceRunning(LocationService::class.java)) {
            this.application.startService(intent)
            this.application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        checkIfHasPermissionsAndStartLocationTracking()
    }

    private fun startTracking() {
        Intent(applicationContext, BackgroundDetectedActivitiesService::class.java).also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it)
                return
            }
            startService(it)
        }
        startService(intent)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val name = className.className
            if (name.endsWith("LocationService")) {
                locationService = (service as LocationService.LocationServiceBinder).service
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            if (className.className == "BackgroundService") {
                locationService = null
            }
        }
    }

    private fun checkIfHasPermissionsAndStartLocationTracking() {
        Dexter.withActivity(this)
            .withPermission(ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    locationService?.startTracking()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        openSettings()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun openSettings() {
        val intent = Intent()
        intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = fromParts("package", BuildConfig.APPLICATION_ID, null)
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}
