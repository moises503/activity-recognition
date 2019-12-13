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
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import la.handy.activiyrecognition.MainActivity
import la.handy.activiyrecognition.core.Constants
import la.handy.activiyrecognition.core.NotificationHandler
import la.handy.activiyrecognition.nearlocations.domain.model.Coordinate
import la.handy.activiyrecognition.nearlocations.domain.model.CustomerLocation
import la.handy.activiyrecognition.nearlocations.presentation.CustomerLocationsPresenter
import la.handy.activiyrecognition.nearlocations.presentation.view.CustomerLocationsView
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*


class LocationService : Service(), CustomerLocationsView {

    private val binder = LocationServiceBinder()
    private var locationListener: LocationListener? = null
    private var locationManager: LocationManager? = null
    private val geoFencePendingIntent: PendingIntent? = null
    private var notificationHandler : NotificationHandler?= null
    private var wakeLock: PowerManager.WakeLock? = null
    private val customerLocationPresenter : CustomerLocationsPresenter
            by inject { parametersOf(this) }

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
            val coordinate = Coordinate(location.latitude, location.longitude)
            customerLocationPresenter.detectNearLocations(coordinate)
            Log.i(TAG, "Coordinate: $coordinate")
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
        notificationHandler = NotificationHandler(this)
        startWakeLock()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopWakeLock()
        if (locationManager != null) {
            try {
                locationManager?.removeUpdates(locationListener!!)
            } catch (ex: Exception) {
                Log.i(TAG, "fail to remove location listeners, ignore", ex)
            }

        }
    }

    override fun startGeofence(coordinate: Coordinate) {
        startGeofence(LatLng(coordinate.latitude, coordinate.longitude))
    }

    override fun sendNotifications(nearLocations: List<CustomerLocation>) {
        nearLocations.forEach {
            buildNotification(it.name)
        }
    }

    override fun showError(message: String) {
        Log.e(TAG, message)
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

            Log.d(TAG, "Location tracking started")

        } catch (ex: SecurityException) {
            Log.e(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, "gps provider does not exist " + ex.localizedMessage)
        }

    }

    private fun initializeLocationManager() {
        if (locationManager == null) {
            locationManager =
                applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
            Log.d(TAG, "GEOFENCE_STARTED")
        }.addOnFailureListener {
            Log.e(TAG, "Exception: ${it.localizedMessage}")
        }
    }

    private fun startGeofence(position: LatLng) {
        Log.i(TAG, "startGeofence()")
        val geofence = createGeofence(position)
        val geofenceRequest = createGeofenceRequest(geofence)
        addGeofence(geofenceRequest)
    }

    private fun buildNotification(location : String) {
        val notificationBuilder = notificationHandler?.createNotification(
            "Te encuentras cerca del siguiente cliente",
            location,
            true,
            MainActivity::class.java
        )
        notificationBuilder?.let {
            val r = Random()
            val notificationId = r.nextInt(80 - 65) + 65
            NotificationManagerCompat.from(applicationContext).notify(notificationId, it.build())
        }
    }

    private fun startWakeLock() {
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationService::lock").apply {
                    acquire()
                }
            }
    }

    private fun stopWakeLock() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
        } catch (e : Exception) {
            Log.e(TAG, "Exception ${e.localizedMessage}")
        }
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