package la.handy.activiyrecognition.nearlocations.presentation.resources

import android.location.Location
import android.util.Log
import la.handy.activiyrecognition.nearlocations.domain.model.Coordinate

class DistanceUtilsImpl : DistanceUtils {
    override fun distanceBetweenTwoCoordinates(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): Float {
        val location1 = Location("")
        location1.latitude = coordinate1.latitude
        location1.longitude = coordinate1.longitude
        val location2 = Location("")
        location2.latitude = coordinate2.latitude
        location2.longitude = coordinate2.longitude
        return location1.distanceTo(location2)
    }

    override fun distanceLogs(message: String) {
        Log.e("LOCATION", "Distance: $message mts")
    }
}