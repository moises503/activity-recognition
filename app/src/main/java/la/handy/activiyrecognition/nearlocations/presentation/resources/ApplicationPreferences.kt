package la.handy.activiyrecognition.nearlocations.presentation.resources

interface ApplicationPreferences {
    fun isUserOnVisit() : Boolean
    fun dateForLastNotification() : String
    fun saveDateForLastNotificationSend(date : String)
}