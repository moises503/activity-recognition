package la.handy.activiyrecognition.core

import android.app.ActivityManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity


@Suppress("DEPRECATION")
fun <T> Context.isServiceRunning(service: Class<T>) =
    (getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }