package la.handy.activiyrecognition.nearlocations.presentation.view

import la.handy.activiyrecognition.nearlocations.domain.model.Coordinate
import la.handy.activiyrecognition.nearlocations.domain.model.CustomerLocation

interface NearLocationsView {
    fun startGeofence(coordinate: Coordinate)
    fun sendNotifications(nearLocations: List<CustomerLocation>)
}