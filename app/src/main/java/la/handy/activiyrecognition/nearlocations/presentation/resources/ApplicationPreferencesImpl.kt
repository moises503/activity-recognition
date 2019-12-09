package la.handy.activiyrecognition.nearlocations.presentation.resources

import android.content.Context
import la.handy.activiyrecognition.core.getLastNotificationSend
import la.handy.activiyrecognition.core.isUserOnVisit
import la.handy.activiyrecognition.core.saveLastNotificationSend

class ApplicationPreferencesImpl(private val context: Context) : ApplicationPreferences {

    override fun isUserOnVisit(): Boolean = context.isUserOnVisit()

    override fun dateForLastNotification(): String = context.getLastNotificationSend()

    override fun saveDateForLastNotificationSend(date: String) =
        context.saveLastNotificationSend(date)
}