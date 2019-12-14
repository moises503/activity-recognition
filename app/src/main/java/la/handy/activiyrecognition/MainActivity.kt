package la.handy.activiyrecognition

import android.Manifest.permission.*
import android.content.Intent
import android.net.Uri.fromParts
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import la.handy.activiyrecognition.core.isServiceRunning
import la.handy.activiyrecognition.services.BackgroundDetectedActivitiesService
import la.handy.activiyrecognition.services.LocationService


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkIfHasPermissionsAndStartLocationTracking()
    }

    private fun startRecognitionTracking() {
        if (!this@MainActivity.isServiceRunning(BackgroundDetectedActivitiesService::class.java)) {
            Intent(applicationContext, BackgroundDetectedActivitiesService::class.java).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(it)
                    return
                }
                startService(it)
            }
        }
    }

    private fun startServiceTracking() {
        if (!this@MainActivity.isServiceRunning(LocationService::class.java)) {
            Intent(applicationContext, LocationService::class.java).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(it)
                    return
                }
                startService(it)
            }
        }
    }

    private fun checkIfHasPermissionsAndStartLocationTracking() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Dexter.withActivity(this)
                .withPermissions(
                    ACCESS_FINE_LOCATION,
                    ACCESS_BACKGROUND_LOCATION,
                    ACTIVITY_RECOGNITION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (it.deniedPermissionResponses.isEmpty()) {
                                startRecognitionTracking()
                                startServiceTracking()
                            } else {
                                openSettings()
                            }
                        }

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showPermissionRationale(token)
                    }
                }).check()
        } else {
            Dexter.withActivity(this)
                .withPermission(ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        startRecognitionTracking()
                        startServiceTracking()
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
    }

    private fun openSettings() {
        val intent = Intent()
        intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = fromParts("package", BuildConfig.APPLICATION_ID, null)
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    private fun showPermissionRationale(token: PermissionToken?) {
        AlertDialog.Builder(this).setTitle(R.string.permission_rationale_title)
            .setMessage(R.string.permission_rationale_message)
            .setNegativeButton(
                android.R.string.cancel
            ) { dialog, _ ->
                dialog.dismiss()
                token?.cancelPermissionRequest()
            }
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.dismiss()
                token?.continuePermissionRequest()
            }
            .setOnDismissListener { token?.cancelPermissionRequest() }
            .show()
    }
}
