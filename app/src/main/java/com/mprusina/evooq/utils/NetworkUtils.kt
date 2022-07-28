package com.mprusina.evooq.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

// Function to check if device is connected to the internet before initiating network request
fun isConnectedToNetwork(context: Context?): Boolean {
    if (context != null) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            }
        }
        return false
    } else {
        return false
    }
}
