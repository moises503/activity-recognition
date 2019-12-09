package la.handy.activiyrecognition.seeder

import la.handy.activiyrecognition.nearlocations.data.model.Location
import la.handy.activiyrecognition.nearlocations.domain.model.CustomerLocation

class Faker {
    companion object {
        fun buildDummyLocations() : List<Location> {
            val locations : MutableList<Location> = mutableListOf()
            locations.add(Location(18.980224, -98.246897, "Location 1"))
            locations.add(Location(18.980351, -98.247202, "Location 2"))
            locations.add(Location(18.980326, -98.246946, "Location 3"))
            locations.add(Location(18.979638, -98.246580, "Location 4"))
            locations.add(Location(18.979640, -98.246492, "Location 5"))
            return locations.toList()
        }

        fun buildDummyCustomerLocations() : List<CustomerLocation> {
            return buildDummyLocations().map {
                CustomerLocation(it.latitude, it.longitude, it.name)
            }
        }

    }
}