package la.handy.activiyrecognition.nearlocations.domain.usecase

import la.handy.activiyrecognition.nearlocations.domain.model.Coordinate
import la.handy.activiyrecognition.nearlocations.domain.model.CustomerLocation

interface CustomerLocationUseCase {
    fun detectNearLocations(currentLocation: Coordinate): List<CustomerLocation>
    fun determineIfSendPushNotification(dateTimeNotificationSend: String): Boolean
    fun userIsOnRightSchedule(): Boolean
    fun checkCustomerLocationForStartGeofence(
        currentLocation: Coordinate,
        nearLocations: List<CustomerLocation>
    ): CustomerLocation?
}