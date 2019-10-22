package co.gov.isabu.showcase.helpers

import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity

/**
 * Simple general network utilities used across the application.
 */

class NetworkHelper {

    companion object {

        /**
         * Verifies if there is an Available network connection in the device.
         *
         * @param activity The activity in which the verification will be performed.
         * @return true if there is network connectivity, false if there isn't.
         */

        fun isNetworkAvailable(activity: AppCompatActivity) : Boolean{

            val connectivityManager = activity.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return  networkInfo != null && networkInfo.isConnected

        }

    }

}