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
        locations.add(Location(20.6667983,-103.4380111, "Location 1"))
        locations.add(Location(20.676406,-103.4205262, "Location 2"))
        locations.add(Location(20.6739116,-103.4351432, "Location 3"))
        locations.add(Location(20.6745029,-103.4282778, "Location 4"))
        locations.add(Location(20.672654,-103.4217406, "Location 5"))
        return locations.toList()
    }

    private fun buildMoisesInformation() : List<Location> {
        val locations : MutableList<Location> = mutableListOf()
        locations.add(Location(18.980224, -98.246897, "Location 1"))
        locations.add(Location(18.980351, -98.247202, "Location 2"))
        locations.add(Location(18.980326, -98.246946, "Location 3"))
        locations.add(Location(18.979638, -98.246580, "Location 4"))
        locations.add(Location(18.979640, -98.246492, "Location 5"))
        return locations.toList()
    }
}