package la.handy.activiyrecognition.core

import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import la.handy.activiyrecognition.BuildConfig
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
fun <T> Context.isServiceRunning(service: Class<T>) =
    (getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }


fun Date.toString(format : String) : String {
    val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
    return try {
        simpleDateFormat.format(this)
    } catch (ex : Exception) {
        ""
    }
}

fun String.toDate(format: String) : Date? {
    val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
    return try {
        simpleDateFormat.parse(this)
    } catch (ex : Exception) {
        null
    }
}

fun Context.getSharedPreferencesEditor(): SharedPreferences.Editor =
    this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).edit()

fun Context.userOnVisit(status : Boolean) =
    this.getSharedPreferencesEditor().putBoolean(Constants.USER_ON_VISIT, status).apply()

fun Context.saveLastNotificationSend(date : String) =
    this.getSharedPreferencesEditor().putString(Constants.LAST_NOTIFICATION_SEND, date).apply()

fun Context.isUserOnVisit() : Boolean =
    this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        .getBoolean(Constants.USER_ON_VISIT, false)

fun Context.getLastNotificationSend() : String =
    this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        .getString(Constants.LAST_NOTIFICATION_SEND, "") ?: ""