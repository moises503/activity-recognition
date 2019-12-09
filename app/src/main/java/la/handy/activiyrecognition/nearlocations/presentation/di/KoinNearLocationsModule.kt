package la.handy.activiyrecognition.nearlocations.presentation.di

import android.content.Context
import la.handy.activiyrecognition.nearlocations.data.datasource.CustomerLocationDataSourceImpl
import la.handy.activiyrecognition.nearlocations.data.repository.CustomerLocationRepositoryImpl
import la.handy.activiyrecognition.nearlocations.domain.datasource.CustomerLocationDataSource
import la.handy.activiyrecognition.nearlocations.domain.repository.CustomerLocationRepository
import la.handy.activiyrecognition.nearlocations.domain.usecase.CustomerLocationUseCase
import la.handy.activiyrecognition.nearlocations.domain.usecase.CustomerLocationUseCaseImpl
import la.handy.activiyrecognition.nearlocations.presentation.CustomerLocationsPresenterImpl
import la.handy.activiyrecognition.nearlocations.presentation.CustomerLocationsPresenter
import la.handy.activiyrecognition.nearlocations.presentation.resources.ApplicationPreferences
import la.handy.activiyrecognition.nearlocations.presentation.resources.ApplicationPreferencesImpl
import la.handy.activiyrecognition.nearlocations.presentation.resources.DistanceUtils
import la.handy.activiyrecognition.nearlocations.presentation.resources.DistanceUtilsImpl
import la.handy.activiyrecognition.nearlocations.presentation.view.CustomerLocationsView
import org.koin.dsl.module

fun providesCustomerLocationDataSource(): CustomerLocationDataSource =
    CustomerLocationDataSourceImpl()

fun providesCustomerLocationRepository(customerLocationDataSource: CustomerLocationDataSource)
        : CustomerLocationRepository = CustomerLocationRepositoryImpl(customerLocationDataSource)

fun providesDistanceUtils(): DistanceUtils = DistanceUtilsImpl()

fun providesApplicationPreferences(context: Context): ApplicationPreferences =
    ApplicationPreferencesImpl(context)

fun providesCustomerLocationUseCase(
    customerLocationRepository: CustomerLocationRepository, distanceUtils: DistanceUtils,
    applicationPreferences: ApplicationPreferences
): CustomerLocationUseCase = CustomerLocationUseCaseImpl(
    customerLocationRepository,
    distanceUtils, applicationPreferences
)


val nearLocationsModule = module {
    single { providesCustomerLocationDataSource() }
    single { providesCustomerLocationRepository(get()) }
    single { providesDistanceUtils() }
    single { providesApplicationPreferences(get()) }
    single { providesCustomerLocationUseCase(get(), get(), get()) }
    factory<CustomerLocationsPresenter> { (v: CustomerLocationsView) ->
        CustomerLocationsPresenterImpl(v, get(), get())
    }
}