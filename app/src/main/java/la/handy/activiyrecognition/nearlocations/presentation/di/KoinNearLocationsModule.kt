package la.handy.activiyrecognition.nearlocations.presentation.di

import android.content.Context
import la.handy.activiyrecognition.nearlocations.data.datasource.CustomerLocationDataSourceImpl
import la.handy.activiyrecognition.nearlocations.data.repository.CustomerLocationRepositoryImpl
import la.handy.activiyrecognition.nearlocations.domain.datasource.CustomerLocationDataSource
import la.handy.activiyrecognition.nearlocations.domain.repository.CustomerLocationRepository
import la.handy.activiyrecognition.nearlocations.domain.usecase.CustomerLocationUseCase
import la.handy.activiyrecognition.nearlocations.domain.usecase.CustomerLocationUseCaseImpl
import la.handy.activiyrecognition.nearlocations.presentation.NearLocationPresenterImpl
import la.handy.activiyrecognition.nearlocations.presentation.NearLocationsPresenter
import la.handy.activiyrecognition.nearlocations.presentation.resources.ApplicationPreferences
import la.handy.activiyrecognition.nearlocations.presentation.resources.ApplicationPreferencesImpl
import la.handy.activiyrecognition.nearlocations.presentation.resources.DistanceUtils
import la.handy.activiyrecognition.nearlocations.presentation.resources.DistanceUtilsImpl
import la.handy.activiyrecognition.nearlocations.presentation.view.NearLocationsView
import org.koin.dsl.module

fun providesCustomerLocationDataSource(): CustomerLocationDataSource =
    CustomerLocationDataSourceImpl()

fun providesCustomerLocationRepository(customerLocationDataSource: CustomerLocationDataSource)
        : CustomerLocationRepository = CustomerLocationRepositoryImpl(customerLocationDataSource)

fun providesDistanceUtils(): DistanceUtils = DistanceUtilsImpl()

fun providesApplicationPreferences(context: Context): ApplicationPreferences =
    ApplicationPreferencesImpl(context)

fun providesCustomerLocationUseCase(
    customerLocationRepository: CustomerLocationRepository, distanceUtils: DistanceUtils
): CustomerLocationUseCase = CustomerLocationUseCaseImpl(customerLocationRepository, distanceUtils)


val nearLocationsModule = module {
    single { providesCustomerLocationDataSource() }
    single { providesCustomerLocationRepository(get()) }
    single { providesDistanceUtils() }
    single { providesApplicationPreferences(get()) }
    single { providesCustomerLocationUseCase(get(), get()) }
    factory<NearLocationsPresenter> {(v : NearLocationsView) ->
        NearLocationPresenterImpl(v, get(), get())
    }
}