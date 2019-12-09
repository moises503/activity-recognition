package la.handy.activiyrecognition.nearlocations.domain.datasource

import la.handy.activiyrecognition.nearlocations.domain.model.CustomerLocation

interface CustomerLocationDataSource {
    fun getCustomerLocations() : List<CustomerLocation>
}