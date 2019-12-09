package la.handy.activiyrecognition.nearlocations.presentation.resources

interface ApplicationPreferences {
    fun isUserOnVisit() : Boolean
    fun dateForLastNotification() : String
    fun saveDateForLastNotificationSend(date : String)
    fun showLogErrorMessage(message : String)
    fun getUserMovement() : String
    fun getDateOfStay() : String
}