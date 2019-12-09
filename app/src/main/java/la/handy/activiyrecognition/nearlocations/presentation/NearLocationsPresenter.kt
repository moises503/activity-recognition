package la.handy.activiyrecognition.nearlocations.presentation

import la.handy.activiyrecognition.nearlocations.domain.model.Coordinate

interface NearLocationsPresenter {
    fun detectNearLocations(currentLocation: Coordinate)
}