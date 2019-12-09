package la.handy.activiyrecognition

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import la.handy.activiyrecognition.nearlocations.data.model.Location
import la.handy.activiyrecognition.nearlocations.domain.datasource.CustomerLocationDataSource
import la.handy.activiyrecognition.seeder.Faker
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.junit.MockitoJUnit


class CustomerLocationsDataSourceUnitTest {

    @Rule
    @JvmField
    val rule = MockitoJUnit.rule()!!

    lateinit var customerLocations : List<Location>
    lateinit var dataSource : CustomerLocationDataSource

    @Before
    fun setup() {
        customerLocations = Faker.buildDummyLocations()
        dataSource = mock()
        whenever(dataSource.getCustomerLocations()).then {
            customerLocations
        }
    }

    @Test
    fun checkIfCustomerLocationsAreConsistent() {
        val locations = dataSource.getCustomerLocations()
        assertEquals(5, locations.size)
    }

    @Test
    fun checkIfCustomerLocationsAreWrong() {
        val locations = dataSource.getCustomerLocations()
        assertNotEquals(6, locations.size)
    }
}
