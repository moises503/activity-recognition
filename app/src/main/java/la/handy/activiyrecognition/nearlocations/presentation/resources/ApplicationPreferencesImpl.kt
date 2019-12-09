package la.handy.activiyrecognition.nearlocations.presentation.resources

import android.content.Context
import android.util.Log
import la.handy.activiyrecognition.core.*

class ApplicationPreferencesImpl(private val context: Context) : ApplicationPreferences {


    override fun showLogErrorMessage(message: String) {
        Log.e("LOCATION", message)
    }

    override fun isUserOnVisit(): Boolean = context.isUserOnVisit()

    override fun dateForLastNotification(): String = context.getLastNotificationSend()

    override fun saveDateForLastNotificationSend(date: String) =
        context.saveLastNotificationSend(date)

    override fun getUserMovement(): String = context.getMovementStatus()

    override fun getDateOfStay(): String = context.getDateOfStay()
}