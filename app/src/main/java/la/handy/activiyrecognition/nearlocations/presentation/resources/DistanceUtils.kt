package la.handy.activiyrecognition.nearlocations.presentation.resources

import la.handy.activiyrecognition.nearlocations.domain.model.Coordinate

interface DistanceUtils {
    fun distanceBetweenTwoCoordinates(coordinate1 : Coordinate, coordinate2 : Coordinate) : Float
}