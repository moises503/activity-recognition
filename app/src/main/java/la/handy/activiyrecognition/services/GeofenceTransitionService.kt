package la.handy.activiyrecognition.services

import com.google.android.gms.location.GeofenceStatusCodes
import android.content.Intent
import android.text.TextUtils
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import android.app.IntentService
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import la.handy.activiyrecognition.MainActivity
import la.handy.activiyrecognition.core.NotificationHandler
import la.handy.activiyrecognition.core.userOnVisit
import java.util.*


class GeofenceTransitionService : IntentService(TAG) {

    private var notificationHandler : NotificationHandler? = null

    override fun onCreate() {
        super.onCreate()
        notificationHandler = NotificationHandler(applicationContext)
    }

    override fun onHandleIntent(intent: Intent?) {
        // Retrieve the Geofencing intent
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        // Handling errors
        if (geofencingEvent.hasError()) {
            val errorMsg = getErrorString(geofencingEvent.errorCode)
            Log.e(TAG, errorMsg)
            return
        }

        // Retrieve GeofenceTrasition
        val geoFenceTransition = geofencingEvent.geofenceTransition
        // Check if the transition type
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
            || geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Get the geofence that were triggered
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            // Create a detail message with Geofences received
            val geofenceTransitionDetails =
                getGeofenceTransitionDetails(geoFenceTransition, triggeringGeofences)
            // Send notification details as a String
            val notificationBuilder = notificationHandler?.createNotification(
                "Hemos detectado movimiento",
                geofenceTransitionDetails,
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

    // Create a detail message with Geofences received
    private fun getGeofenceTransitionDetails(
        geoFenceTransition: Int,
        triggeringGeofences: List<Geofence>
    ): String {
        // get the ID of each geofence triggered
        val triggeringGeofencesList: MutableList<String> = mutableListOf()
        for (geofence in triggeringGeofences) {
            triggeringGeofencesList.add(geofence.requestId)
        }

        var status: String? = null
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            status = "Estas entrando a la ubicación del cliente "
            this.userOnVisit(true)
        }
        else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            status = "Estas saliendo de la ubicación del cliente "
            this.userOnVisit(false)
        }
        Log.d(TAG, status!! + TextUtils.join(", ", triggeringGeofencesList))
        return status
    }


    companion object {

        private val TAG = GeofenceTransitionService::class.java.simpleName
        private fun getErrorString(errorCode: Int): String {
            return when (errorCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "GeoFence not available"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "Too many GeoFences"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "Too many pending intents"
                else -> "Unknown error."
            }
        }
    }
}