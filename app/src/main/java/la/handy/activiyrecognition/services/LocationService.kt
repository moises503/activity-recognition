package la.handy.activiyrecognition.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import la.handy.activiyrecognition.core.Constants
import la.handy.activiyrecognition.core.NotificationHandler

class LocationService : Service() {
    private val binder = LocationServiceBinder()
    private val TAG = "LocationService"
    private var mLocationListener: LocationListener? = null
    private var mLocationManager: LocationManager? = null

    private val LOCATION_INTERVAL = 500
    private val LOCATION_DISTANCE = 10

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    private inner class LocationListener(provider: String) : android.location.LocationListener {
        private val TAG = "LocationListener"
        private var mLastLocation: Location? = null

        init {
            mLastLocation = Location(provider)
        }

        override fun onLocationChanged(location: Location) {
            mLastLocation = location
            Log.i(TAG, "LocationChanged: $location")
            Toast.makeText(applicationContext, "LocationChanged: $location", Toast.LENGTH_LONG)
                .show()
        }

        override fun onProviderDisabled(provider: String) {
            Log.e(TAG, "onProviderDisabled: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.e(TAG, "onProviderEnabled: $provider")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.e(TAG, "onStatusChanged: $status")
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    override fun onCreate() {
        Log.i(TAG, "onCreate")
        startForeground(
            Constants.NOTIFICATION_SERVICE_ID,
            NotificationHandler.getForegroundNotification(this)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationManager != null) {
            try {
                mLocationManager?.removeUpdates(mLocationListener!!)
            } catch (ex: Exception) {
                Log.i(TAG, "fail to remove location listners, ignore", ex)
            }

        }
    }

    private fun initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager =
                applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    fun startTracking() {
        initializeLocationManager()
        mLocationListener = LocationListener(LocationManager.GPS_PROVIDER)

        try {
            mLocationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE.toFloat(),
                mLocationListener!!
            )

        } catch (ex: SecurityException) {
            // Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (ex: IllegalArgumentException) {
            // Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    inner class LocationServiceBinder : Binder() {
        val service: LocationService
            get() = this@LocationService
    }
}