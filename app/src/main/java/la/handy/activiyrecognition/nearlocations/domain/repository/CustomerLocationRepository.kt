package la.handy.activiyrecognition.nearlocations.domain.repository

import la.handy.activiyrecognition.nearlocations.domain.model.CustomerLocation

interface CustomerLocationRepository {
    fun getCustomerLocations() : List<CustomerLocation>
}