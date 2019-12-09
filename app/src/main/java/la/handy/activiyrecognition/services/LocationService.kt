package la.handy.activiyrecognition.services

import android.app.PendingIntent
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
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import la.handy.activiyrecognition.core.Constants
import la.handy.activiyrecognition.core.NotificationHandler



class LocationService : Service() {
    private val binder = LocationServiceBinder()
    private var locationListener: LocationListener? = null
    private var locationManager: LocationManager? = null
    private val geoFencePendingIntent: PendingIntent? = null

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    private inner class LocationListener(provider: String) : android.location.LocationListener {
        private var lastLocation: Location? = null

        init {
            lastLocation = Location(provider)
        }

        override fun onLocationChanged(location: Location) {
            lastLocation = location
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
        if (locationManager != null) {
            try {
                locationManager?.removeUpdates(locationListener!!)
            } catch (ex: Exception) {
                Log.i(TAG, "fail to remove location listeners, ignore", ex)
            }

        }
    }

    private fun initializeLocationManager() {
        if (locationManager == null) {
            locationManager =
                applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    fun startTracking() {
        initializeLocationManager()
        locationListener = LocationListener(LocationManager.GPS_PROVIDER)

        try {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE.toFloat(),
                locationListener!!
            )

        } catch (ex: SecurityException) {
            Log.e(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, "gps provider does not exist " + ex.localizedMessage)
        }

    }

    private fun createGeofence(latLng: LatLng): Geofence {
        Log.d(TAG, "createGeofence")
        return Geofence.Builder()
            .setRequestId(GEOFENCE_REQ_ID)
            .setCircularRegion(latLng.latitude, latLng.longitude, GEOFENCE_RADIUS)
            .setExpirationDuration(GEO_DURATION)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    private fun createGeofenceRequest(geofence: Geofence): GeofencingRequest {
        Log.d(TAG, "createGeofenceRequest")
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
    }

    private fun createGeofencePendingIntent(): PendingIntent {
        Log.d(TAG, "createGeofencePendingIntent")
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent

        val intent = Intent(this, GeofenceTransitionService::class.java)
        return PendingIntent.getService(
            this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun addGeofence(request: GeofencingRequest) {
        val client = LocationServices.getGeofencingClient(this)
        client.addGeofences(request, createGeofencePendingIntent()).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    private fun startGeofence(position: LatLng) {
        Log.i(TAG, "startGeofence()")
        val geofence = createGeofence(position)
        val geofenceRequest = createGeofenceRequest(geofence)
        addGeofence(geofenceRequest)
    }

    inner class LocationServiceBinder : Binder() {
        val service: LocationService
            get() = this@LocationService
    }

     companion object {
         private const val GEO_DURATION = (60 * 60 * 1000).toLong()
         private const val GEOFENCE_REQ_ID = "customer_geofence"
         private const val GEOFENCE_RADIUS = Constants.GEOFENCE_RADIUS
         private const val TAG = "LocationService"
         private const val LOCATION_INTERVAL = 500
         private const val LOCATION_DISTANCE = 10
         private const val GEOFENCE_REQ_CODE = 0
     }
}