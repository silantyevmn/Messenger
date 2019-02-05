package ru.silantyevmn.mymessenger.utils

import android.content.Context
import android.net.ConnectivityManager
import ru.silantyevmn.mymessenger.di.App

class NetworkStatus {
    companion object {
        fun isInternetAvailable(): Boolean {
            val connectivityManager =
                App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
        }
    }
}