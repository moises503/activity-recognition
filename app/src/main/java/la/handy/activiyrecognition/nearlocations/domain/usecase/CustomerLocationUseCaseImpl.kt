package la.handy.activiyrecognition.nearlocations.domain.usecase

import la.handy.activiyrecognition.core.Constants
import la.handy.activiyrecognition.core.toDate
import la.handy.activiyrecognition.core.toString
import la.handy.activiyrecognition.nearlocations.domain.repository.CustomerLocationRepository
import la.handy.activiyrecognition.nearlocations.domain.model.Coordinate
import la.handy.activiyrecognition.nearlocations.domain.model.CustomerLocation
import la.handy.activiyrecognition.nearlocations.presentation.resources.DistanceUtils
import java.util.*
import java.util.concurrent.TimeUnit

class CustomerLocationUseCaseImpl(
    private val customerLocationRepository: CustomerLocationRepository,
    private val distanceUtils: DistanceUtils
) : CustomerLocationUseCase {

    override fun checkCustomerLocationForStartGeofence(
        currentLocation: Coordinate,
        nearLocations: List<CustomerLocation>
    ): CustomerLocation? {
        nearLocations.forEach { customerLocation ->
            val customerCurrentLocation =
                Coordinate(customerLocation.latitude, currentLocation.longitude)
            val currentDistance = distanceUtils.distanceBetweenTwoCoordinates(
                currentLocation,
                customerCurrentLocation
            )
            distanceUtils.distanceLogs("$currentDistance")
            if (currentDistance <= Constants.GEOFENCE_MINIMUM_RADIUS) {
                return customerLocation
            }
        }
        return null
    }

    override fun userIsOnRightSchedule(): Boolean {
        val initHour = Constants.INIT_HOUR.toDate("HH:mm:ss")
        val finishHour = Constants.FINISH_HOUR.toDate("HH:mm:ss")
        val currentHourString = Date().toString("HH:mm:ss")
        val currentHour = currentHourString.toDate("HH:mm:ss") ?: Date()
        return currentHour.after(initHour) && currentHour.before(finishHour)
    }

    override fun detectNearLocations(currentLocation: Coordinate): List<CustomerLocation> {
        val customerLocations = customerLocationRepository.getCustomerLocations()
        val customerNearLocations: MutableList<CustomerLocation> = mutableListOf()
        customerLocations.forEach { customerLocation ->
            val currentCustomerLocation =
                Coordinate(customerLocation.latitude, customerLocation.longitude)
            val currentDistance = distanceUtils.distanceBetweenTwoCoordinates(
                currentLocation,
                currentCustomerLocation
            )
            distanceUtils.distanceLogs("$currentDistance")
            if (currentDistance == Constants.NEAR_LOCATION_MINIMUM_DISTANCE) {
                customerNearLocations.add(customerLocation)
            }
        }
        return customerNearLocations
    }

    override fun determineIfSendPushNotification(dateTimeNotificationSend: String): Boolean {
        if (dateTimeNotificationSend.isEmpty())
            return true
        val startDate = dateTimeNotificationSend.toDate("dd-MM-yyyy HH:mm:ss")?.time ?: 0L
        val dateDiff = Date().time - startDate
        val minutes = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
        return minutes >= 30
    }


}