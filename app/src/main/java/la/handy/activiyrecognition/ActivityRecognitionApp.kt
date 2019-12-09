package la.handy.activiyrecognition

import android.app.Application
import la.handy.activiyrecognition.nearlocations.presentation.di.nearLocationsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ActivityRecognitionApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ActivityRecognitionApp)
            modules(listOf(nearLocationsModule))
        }
    }
}