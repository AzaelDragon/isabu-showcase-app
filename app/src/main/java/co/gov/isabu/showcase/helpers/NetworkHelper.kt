package co.gov.isabu.showcase.helpers

import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import org.json.JSONObject
import java.io.File

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

        /**
         * Fetches a JSON map from a remote resource and stores it in the local cache.
         *
         * @param activity the activity to get the context from where the function is being executed.
         * @return The JSON object of the fetched resource, or an empty resource map if the download
         * was not successful.
         */

        fun fetchMapFromRemote(activity: AppCompatActivity) : JSONObject {

            val descriptorUrl = PreferenceHelper(activity).getDescriptorUrl()
            val storageLocation = StorageHelper.buildStoragePath(activity, "map.json")

            return if (isNetworkAvailable(activity)) {

                val result = Fuel.download(descriptorUrl)
                    .fileDestination { _, _ -> File(storageLocation) }
                    .progress { _, _ ->
                    }.responseString().third

                result.fold({ value ->
                    return@fold JSONObject(value)
                }, {
                    return@fold StorageHelper.buildEmptyDescriptor()
                })

            } else {

                StorageHelper.buildEmptyDescriptor()

            }

        }

    }

}