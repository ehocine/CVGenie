package com.hocel.cvgenie.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun String.toast(context: Context, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, this, length).show()

fun convertTimeStampToDateAndTime(epoch: Long): String {
    val date = Date(epoch)
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US)
    return sdf.format(date)
}

fun convertTimeStampToDate(epoch: Long): String {
    val date = Date(epoch)
    val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
    return sdf.format(date)
}

fun hasInternetConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

//find the current activity from a composable
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}