package co.gov.isabu.showcase.tasks

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.gov.isabu.showcase.activities.ErrorActivity
import co.gov.isabu.showcase.activities.MainActivity
import co.gov.isabu.showcase.helpers.IntegrityHelper
import co.gov.isabu.showcase.helpers.NetworkHelper
import co.gov.isabu.showcase.helpers.StorageHelper
import com.github.kittinunf.fuel.Fuel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.lang.ref.WeakReference

/**
 * Asynchronous Worker Task for fetching all the multimedia contained in the resource map.
 */

class MediaDownloadTask internal constructor(activity: AppCompatActivity, private val helper: StorageHelper) : AsyncTask<Unit, MediaDownloadTask.Progress, Int>() {

    /**
     * Simple class to create dynamic progress dialogs based on file size and name.
     */

    class Progress(val current: Int, val max: Int, val title: String, val  showFlag: Boolean)

    private val activityReference: WeakReference<AppCompatActivity> = WeakReference(activity)
    private lateinit var mProgressDialog: ProgressDialog

    /**
     * Instantiate dynamic progress dialog
     */

    override fun onPreExecute() {

        this.mProgressDialog = ProgressDialog(activityReference.get())
        this.mProgressDialog.setTitle("Preparando archivos multimedia")
        this.mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        this.mProgressDialog.setCancelable(false)

    }

    /**
     * Downloads each of the memory-loaded map entities from the storage manager and attempts to
     * persist them in memory.
     *
     * @return A code depending on the outcome. Returns 0 if execution was successful, 1 if the
     * image fetch process failed, 2 if the video fetch process failed, and 3 if all the processes
     * failed.
     */

    override fun doInBackground(vararg params: Unit) : Int {

        val imageMap = helper.imageMap
        val videoMap = helper.videoMap

        val imageBatchStatus = processBatch(imageMap)
        val videoBatchStatus = processBatch(videoMap)

        if (!imageBatchStatus && !videoBatchStatus) return 3
        if (!imageBatchStatus) return 1
        if (!videoBatchStatus) return 2

        return 0

    }

    /**
     * Using the dynamic progress class defined, updates and shows a dynamic progress dialog
     * depending on the progress object provided.
     */

    override fun onProgressUpdate(vararg values: Progress?) {

        val progressObj = values[0]!!
        if (this.mProgressDialog.max != progressObj.max) this.mProgressDialog.max = progressObj.max
        this.mProgressDialog.progress = progressObj.current
        this.mProgressDialog.setTitle(progressObj.title)

        if (!mProgressDialog.isShowing && progressObj.showFlag) this.mProgressDialog.show()

    }

    /**
     * Depending on the final outcome of doInBackground(), shows a toast corresponding to the
     * return code launched, dismisses any open progress dialog, and starts the correct activity
     * depending on the final outcome, having activity MainActivity for code 0, and ErrorActivity
     * for codes 1, 2 and 3.
     */

    override fun onPostExecute(result: Int?) {

        when (result) {

            1 ->
                Toast.makeText(
                activityReference.get()!!,
                "Ha ocurrido un error inesperado al descargar las imágenes del mapa.",
                Toast.LENGTH_SHORT)
                .show()

            2 ->
                Toast.makeText(
                    activityReference.get()!!,
                    "Ha ocurrido un error inesperado al descargar los videos del mapa.",
                    Toast.LENGTH_SHORT)
                    .show()

            3 ->
                Toast.makeText(
                    activityReference.get()!!,
                    "Ha ocurrido un error inesperado al descargar tanto videos como imágenes del mapa.",
                    Toast.LENGTH_SHORT)
                    .show()

            else ->
                Toast.makeText(
                    activityReference.get()!!,
                    "Los archivos locales concuerdan con el mapa y han sido verificados satisfactoriamente.",
                    Toast.LENGTH_SHORT)
                    .show()

        }

        if (mProgressDialog.isShowing) mProgressDialog.dismiss()

        val activity = activityReference.get()!!

        if (result == 0) {

            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)

        } else {

            val intent = Intent(activity, ErrorActivity::class.java)
            activity.startActivity(intent)

        }

    }

    /**
     * Performs a download and checksum verification batch process using the fetchAndPersist() and
     * verifyIntegrity methods, with a final global success or error outcome.
     *
     * @param jsonArray the object map to introspect during the batch process.
     * @return true if ALL the process was successful, false if it failed at any point.
     */

    private fun processBatch(jsonArray: JSONArray) : Boolean {

        var completionFlag = true

        for (i in 0 until jsonArray.length()) {

            val item = jsonArray.getJSONObject(i)
            val path = StorageHelper.buildStoragePath(activityReference.get()!!, item.getString("name"))

            if (!File(path).exists()) completionFlag = fetchAndPersist(item)

            if (!IntegrityHelper.verifyIntegrity(activityReference.get()!!, item)) {

                completionFlag = fetchAndPersist(item)

            }

        }

        return completionFlag

    }

    /**
     * Using the provided object, builds a local storage path, fetches the file through a HTTP/GET
     * request, and stores it to the target file in local storage, generating a progress dialog
     * with the file's name and total Kilobytes count. Finally, it will verify it's integrity checksum.
     *
     * @param jsonObject the object to persist.
     * @return true if the file correctly downloaded and matches the target checksum, false if any
     * of the previous steps fails.
     */

    private fun fetchAndPersist(jsonObject: JSONObject) : Boolean {

        val url = jsonObject.getString("uri")
        val fileName = jsonObject.getString("name")
        val storageLocation = StorageHelper.buildStoragePath(activityReference.get()!!, fileName)
        val storageFile = File(storageLocation)

        if (NetworkHelper.isNetworkAvailable(activityReference.get() as AppCompatActivity)) {

            val result = Fuel.download(url)
                .fileDestination { _, _ -> storageFile }
                .progress { bytesRead, bytesLeft ->
                    val kbytesRead = (bytesRead/1024).toInt()
                    val kbytesTotal = (bytesLeft/1024).toInt()
                    val title = "Descargando recurso '${storageFile.nameWithoutExtension}'"
                    publishProgress(
                        Progress(
                            kbytesRead,
                            kbytesTotal,
                            title,
                            true
                        )
                    )
                }.responseString().third

            return result.fold({

                IntegrityHelper.verifyIntegrity(activityReference.get()!!, jsonObject)

            }, { e ->

                Log.e("Loader", "File fetch error: ${e.message}")
                false

            })

        } else {

            return false

        }

    }

}