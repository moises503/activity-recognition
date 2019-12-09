package la.handy.activiyrecognition.nearlocations.data.repository

import la.handy.activiyrecognition.nearlocations.domain.datasource.CustomerLocationDataSource
import la.handy.activiyrecognition.nearlocations.domain.model.CustomerLocation
import la.handy.activiyrecognition.nearlocations.domain.repository.CustomerLocationRepository

class CustomerLocationRepositoryImpl(
    private val customerLocationDataSource: CustomerLocationDataSource) : CustomerLocationRepository {

    override fun getCustomerLocations(): List<CustomerLocation> {
        return customerLocationDataSource.getCustomerLocations()
    }
}