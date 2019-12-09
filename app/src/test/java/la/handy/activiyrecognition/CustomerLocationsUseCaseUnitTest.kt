package la.handy.activiyrecognition

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import la.handy.activiyrecognition.nearlocations.domain.model.Coordinate
import la.handy.activiyrecognition.nearlocations.domain.model.CustomerLocation
import la.handy.activiyrecognition.nearlocations.domain.usecase.CustomerLocationUseCase
import la.handy.activiyrecognition.seeder.Faker
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import org.junit.Assert.*


class CustomerLocationsUseCaseUnitTest {
    @Rule
    @JvmField
    val rule = MockitoJUnit.rule()!!

    lateinit var coordinate: Coordinate
    lateinit var useCase: CustomerLocationUseCase
    lateinit var customerLocations: List<CustomerLocation>
    lateinit var dateTimeNotificationSend: String
    lateinit var customerLocation: CustomerLocation

    @Before
    fun setup() {
        coordinate = Coordinate(19.8938948, -98.3838943)
        useCase = mock()
        customerLocations = Faker.buildDummyCustomerLocations()
        dateTimeNotificationSend = "21/12/2019 18:59:00"
        customerLocation = CustomerLocation(19.8938948, -98.3838943, "Location 1")
    }

    @Test
    fun whenDetectNearLocationsOfCurrentLocationReturnsAnArray() {
        whenever(useCase.detectNearLocations(coordinate)).then {
            customerLocations
        }
        val nearLocations = useCase.detectNearLocations(coordinate)
        assertArrayEquals(customerLocations.toTypedArray(), nearLocations.toTypedArray())
    }

    @Test
    fun whenNotDetectNearLocationsReturnsAnEmptyArray() {
        whenever(useCase.detectNearLocations(coordinate)).then {
            emptyList<CustomerLocation>()
        }
        val nearLocations = useCase.detectNearLocations(coordinate)
        assertEquals(0, nearLocations.size)
    }

    @Test
    fun whenUserIsOnRightScheduleReturnsTrue() {
        whenever(useCase.userIsOnRightSchedule()).then {
            true
        }
        assertEquals(true, useCase.userIsOnRightSchedule())
    }

    @Test
    fun whenUserIsNotOnRightScheduleReturnsFalse() {
        whenever(useCase.userIsOnRightSchedule()).then {
            false
        }
        assertEquals(false, useCase.userIsOnRightSchedule())
    }

    @Test
    fun whenUserIsOnMovementReturnsTrue() {
        whenever(useCase.isUserOnMovement()).then {
            true
        }
        assertEquals(true, useCase.isUserOnMovement())
    }

    @Test
    fun whenUserIsNotOnMovementReturnsFalse() {
        whenever(useCase.isUserOnMovement()).then {
            false
        }
        assertEquals(false, useCase.isUserOnMovement())
    }

    @Test
    fun whenServiceDetectThatIsNecessarySendAPushNotificationReturnsTrue() {
        whenever(useCase.determineIfSendPushNotification(dateTimeNotificationSend))
            .then {
                true
            }
        assertEquals(true, useCase.determineIfSendPushNotification(dateTimeNotificationSend))
    }

    @Test
    fun whenServiceDetectThatIsNotNecessarySendAPushNotificationReturnsFalse() {
        whenever(useCase.determineIfSendPushNotification(dateTimeNotificationSend))
            .then {
                false
            }
        assertEquals(false, useCase.determineIfSendPushNotification(dateTimeNotificationSend))
    }

    @Test
    fun whenExistsANearLocationForStartGeofenceReturnsAnObject() {
        whenever(
            useCase.checkCustomerLocationForStartGeofence(
                coordinate,
                customerLocations
            )
        ).then {
            customerLocation
        }
        assertEquals(
            customerLocation, useCase.checkCustomerLocationForStartGeofence(
                coordinate,
                customerLocations
            )
        )
    }

    @Test
    fun whenNotExistsANearLocationForStartGeofenceReturnsNull() {
        whenever(
            useCase.checkCustomerLocationForStartGeofence(
                coordinate,
                customerLocations
            )
        ).then {
            null
        }
        assertEquals(
            null, useCase.checkCustomerLocationForStartGeofence(
                coordinate,
                customerLocations
            )
        )
    }
}