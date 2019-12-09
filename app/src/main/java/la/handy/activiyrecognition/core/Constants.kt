package la.handy.activiyrecognition.core

class Constants {
    companion object {
        const val SECONDS_FOR_STAYING = 180
        const val MOVEMENT_STAY_DATE = "movement_stay_date"
        const val DETECTION_INTERVAL_IN_MILLISECONDS : Long = 1000
        const val CONFIDENCE = 70
        const val NOTIFICATION_SERVICE_ID = 1094
        const val NEAR_LOCATION_MINIMUM_DISTANCE = 300.0f
        const val GEOFENCE_RADIUS = 300.0f
        const val GEOFENCE_MINIMUM_RADIUS = 299.0f
        const val INIT_HOUR = "09:00:00"
        const val FINISH_HOUR = "18:00:00"
        const val USER_ON_VISIT = "user_on_visit"
        const val LAST_NOTIFICATION_SEND = "last_notification_send"
        const val ON_WALK = "on_walk"
        const val ON_STAY = "on_stay"
        const val ON_UNKNOWN = "on_unknown"
        const val MOVEMENT_STATUS = "movement_status"
    }
}