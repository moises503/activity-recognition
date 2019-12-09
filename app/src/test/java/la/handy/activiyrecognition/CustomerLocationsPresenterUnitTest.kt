package la.handy.activiyrecognition

import com.nhaarman.mockitokotlin2.*
import la.handy.activiyrecognition.nearlocations.domain.model.Coordinate
import la.handy.activiyrecognition.nearlocations.presentation.CustomerLocationsPresenter
import la.handy.activiyrecognition.nearlocations.presentation.view.CustomerLocationsView
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit

class CustomerLocationsPresenterUnitTest {
    @Rule
    @JvmField
    val rule = MockitoJUnit.rule()!!

    lateinit var presenter : CustomerLocationsPresenter
    lateinit var view : CustomerLocationsView
    lateinit var coordinate: Coordinate
    lateinit var errorMessage : String

    @Before
    fun setup() {
        presenter = mock()
        view = mock()
        coordinate = Coordinate(18.3849748, -98.21318237213)
        errorMessage = "This is an error message"
    }

    @Test
    fun whenRetrieveNearLocationsPresenterShouldSendNotifications() {
        whenever(presenter.detectNearLocations(coordinate)).then {
            view.sendNotifications(emptyList())
        }
        presenter.detectNearLocations(coordinate)
        verify(view, times(1)).sendNotifications(emptyList())
        verify(view, never()).showError(errorMessage)
    }

    @Test
    fun whenRetrieveALocationForStartGeofenceShouldReturnThisLocation() {
        whenever(presenter.detectNearLocations(coordinate)).then {
            view.startGeofence(coordinate)
        }

        presenter.detectNearLocations(coordinate)
        verify(view, times(1)).startGeofence(coordinate)
        verify(view, never()).showError(errorMessage)
    }

    @Test
    fun whenNotDetectNearLocationsShouldShowAnErrorMessage() {
        whenever(presenter.detectNearLocations(coordinate)).then {
            view.showError(errorMessage)
        }
        presenter.detectNearLocations(coordinate)
        verify(view, never()).sendNotifications(emptyList())
        verify(view, never()).startGeofence(coordinate)
        verify(view, times(1)).showError(errorMessage)
    }
}