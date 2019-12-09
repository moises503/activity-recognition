package la.handy.activiyrecognition

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import la.handy.activiyrecognition.nearlocations.domain.model.CustomerLocation
import la.handy.activiyrecognition.nearlocations.domain.repository.CustomerLocationRepository
import la.handy.activiyrecognition.seeder.Faker
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.mockito.junit.MockitoJUnit

class CustomerLocationsRepositoryUnitTest {
    @Rule
    @JvmField
    val rule = MockitoJUnit.rule()!!

    lateinit var customerLocations : List<CustomerLocation>
    lateinit var repository : CustomerLocationRepository

    @Before
    fun setup() {
        customerLocations = Faker.buildDummyCustomerLocations()
        repository = mock()
        whenever(repository.getCustomerLocations()).then {
            customerLocations
        }
    }

    @Test
    fun checkIfCustomerLocationsAreConsistent() {
        val locations = repository.getCustomerLocations()
        assertEquals(5, locations.size)
    }

    @Test
    fun checkIfCustomerLocationsAreWrong() {
        val locations = repository.getCustomerLocations()
        assertNotEquals(6, locations.size)
    }
}