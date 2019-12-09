package la.handy.activiyrecognition.nearlocations.presentation

import la.handy.activiyrecognition.core.toString
import la.handy.activiyrecognition.nearlocations.domain.model.Coordinate
import la.handy.activiyrecognition.nearlocations.domain.usecase.CustomerLocationUseCase
import la.handy.activiyrecognition.nearlocations.presentation.resources.ApplicationPreferences
import la.handy.activiyrecognition.nearlocations.presentation.view.NearLocationsView
import java.util.*

class NearLocationPresenterImpl(
    private val view: NearLocationsView,
    private val applicationPreferences: ApplicationPreferences,
    private val nearLocationsUseCase: CustomerLocationUseCase
) : NearLocationsPresenter {

    override fun detectNearLocations(currentLocation: Coordinate) {
        if (nearLocationsUseCase.userIsOnRightSchedule()) {
            val nearLocations = nearLocationsUseCase.detectNearLocations(currentLocation)
            if (nearLocations.isNotEmpty()) {
                val locationForStartGeofence = nearLocationsUseCase
                    .checkCustomerLocationForStartGeofence(currentLocation, nearLocations)
                if (!applicationPreferences.isUserOnVisit()) {
                    locationForStartGeofence?.let {
                        view.startGeofence(Coordinate(it.latitude, it.longitude))
                    }
                }
                if (nearLocationsUseCase
                        .determineIfSendPushNotification(applicationPreferences
                        .dateForLastNotification())) {
                    applicationPreferences
                        .saveDateForLastNotificationSend(Date().toString("dd-MM-yyyy HH:mm:ss"))
                    view.sendNotifications(nearLocations)
                }
            } else {
                applicationPreferences.showLogErrorMessage("Could not retrieve near locations")
            }
        } else {
            applicationPreferences.showLogErrorMessage("User is not in right schedule")
        }
    }
}