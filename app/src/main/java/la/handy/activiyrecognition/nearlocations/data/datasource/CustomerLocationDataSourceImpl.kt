package la.handy.activiyrecognition.nearlocations.data.datasource

import la.handy.activiyrecognition.nearlocations.data.model.Location
import la.handy.activiyrecognition.nearlocations.domain.datasource.CustomerLocationDataSource
import la.handy.activiyrecognition.nearlocations.domain.model.CustomerLocation

class CustomerLocationDataSourceImpl : CustomerLocationDataSource {

    override fun getCustomerLocations(): List<CustomerLocation> {
        return buildDummyInformation().map {
            CustomerLocation(it.latitude, it.longitude, it.name)
        }
    }

    private fun buildDummyInformation() : List<Location> {
        val locations : MutableList<Location> = mutableListOf()
        locations.add(Location(19.908,18.909, "Location 1"))
        locations.add(Location(19.908,18.909, "Location 2"))
        locations.add(Location(19.908,18.909, "Location 3"))
        locations.add(Location(19.908,18.909, "Location 4"))
        locations.add(Location(19.908,18.909, "Location 5"))
        return locations.toList()
    }
}