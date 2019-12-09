package la.handy.activiyrecognition.nearlocations.presentation

import la.handy.activiyrecognition.nearlocations.domain.model.Coordinate

interface CustomerLocationsPresenter {
    fun detectNearLocations(currentLocation: Coordinate)
}