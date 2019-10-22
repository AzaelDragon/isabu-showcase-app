package co.gov.isabu.showcase.tasks

import android.os.AsyncTask
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.gov.isabu.showcase.helpers.NetworkHelper
import co.gov.isabu.showcase.helpers.StorageHelper
import com.github.kittinunf.fuel.Fuel
import org.json.JSONObject
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

/**
 * Asynchronous Worker Task for fetching the multimedia map to the System's Internal Storage.
 */

class JSONDownloadTask internal constructor(activity: AppCompatActivity) : AsyncTask<Unit, Int, JSONObject>() {

    private val activityReference: WeakReference<AppCompatActivity> = WeakReference(activity)

    override fun onPreExecute() {

        Toast.makeText(
            activityReference.get(),
            "EXECUTION TEST",
            Toast.LENGTH_SHORT)
            .show()

    }

    /**
     * Figures out if there's network connectivity, fetches the JSON file and places it in the
     * system's internal storage.
     *
     * @return the fetched JSON object that could be an empty descriptor on download failure.
     */

    override fun doInBackground(vararg params: Unit?) : JSONObject {

        val activity = activityReference.get()
        val context = activity?.applicationContext

        val properties = Properties()
        val inputStream = context?.assets?.open("config.properties")
        properties.load(inputStream)

        val descriptorUrl = properties.getProperty("resource_descriptor_url")
        val storageLocation = StorageHelper.buildStoragePath(activity!!, "map.json")

        return if (NetworkHelper.isNetworkAvailable(activityReference.get() as AppCompatActivity)) {

            val result = Fuel.download(descriptorUrl)
                .fileDestination { _, _ -> File(storageLocation)}
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
    /**
     * Displays a Toast depending on the outcome of doInBackground(), and launches the storage helper
     * to refresh the map indexes.
     */

    override fun onPostExecute(result: JSONObject?) {

        val activity = activityReference.get()
        val emptyReference = StorageHelper.buildEmptyDescriptor()

        if (result!! != emptyReference) {

            Toast.makeText(
                activity,
                "Mapa de multimedia descargado con éxito.",
                Toast.LENGTH_SHORT)
            .show()

        } else {

            Toast.makeText(
                activity,
                "Se utilizará un mapa multimedia vacío.",
                Toast.LENGTH_SHORT)
            .show()

        }

        StorageHelper(activity!!)

    }

}