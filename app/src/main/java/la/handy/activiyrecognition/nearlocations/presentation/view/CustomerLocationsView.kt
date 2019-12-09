package la.handy.activiyrecognition.nearlocations.presentation.view

import la.handy.activiyrecognition.nearlocations.domain.model.Coordinate
import la.handy.activiyrecognition.nearlocations.domain.model.CustomerLocation

interface CustomerLocationsView {
    fun startGeofence(coordinate: Coordinate)
    fun sendNotifications(nearLocations: List<CustomerLocation>)
    fun showError(message : String)
}