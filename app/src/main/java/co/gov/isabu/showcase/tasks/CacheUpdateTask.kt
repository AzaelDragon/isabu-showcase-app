package co.gov.isabu.showcase.tasks

import android.app.ProgressDialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import co.gov.isabu.showcase.helpers.NetworkHelper
import co.gov.isabu.showcase.helpers.StorageHelper
import java.lang.ref.WeakReference

/**
 * Asynchronous Worker Task for refreshing the System's Internal Storage cache.
 */

class CacheUpdateTask internal constructor(activity: AppCompatActivity) : AsyncTask<Unit, CacheUpdateTask.Progress, Unit>() {

    private val activityReference: WeakReference<AppCompatActivity> = WeakReference(activity)
    private lateinit var mProgressDialog: ProgressDialog

    /**
     * Simple class to create dynamic progress dialogs based on title.
     */

    class Progress(val title: String, val showFlag: Boolean)

    /**
     * Instantiate dynamic progress dialog
     */

    override fun onPreExecute() {

        this.mProgressDialog = ProgressDialog(activityReference.get())
        this.mProgressDialog.setTitle("Realizando actividad de refresco de caché")
        this.mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        this.mProgressDialog.isIndeterminate = true
        this.mProgressDialog.setCancelable(false)

    }

    /**
     * Completely downloads again the JSON resource map from the remote URL.
     */

    override fun doInBackground(vararg params: Unit?) {

        val activity = activityReference.get()!!

        publishProgress(Progress("Reconstruyendo mapa desde localización remota...", true))
        NetworkHelper.fetchMapFromRemote(activity)

        publishProgress(Progress("Refrescando cache...", true))

    }

    /**
     * Refreshes the progress dialog with the provided title, and verifies if it needs to be shown
     * in screen.
     */

    override fun onProgressUpdate(vararg values: Progress?) {

        val progressObj = values[0]!!
        this.mProgressDialog.setTitle(progressObj.title)
        if (!mProgressDialog.isShowing && progressObj.showFlag) this.mProgressDialog.show()

    }

    /**
     * Verifies and downloads (if required) all the required files pending update that get detected
     * on the restart of the application.
     */

    override fun onPostExecute(result: Unit?) {

        val activity = activityReference.get()!!
        if (mProgressDialog.isShowing) mProgressDialog.dismiss()

        StorageHelper(activity)

    }

}