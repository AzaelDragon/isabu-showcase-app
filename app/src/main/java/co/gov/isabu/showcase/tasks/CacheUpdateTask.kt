package co.gov.isabu.showcase.tasks

import android.app.ProgressDialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import co.gov.isabu.showcase.helpers.NetworkHelper
import co.gov.isabu.showcase.helpers.StorageHelper
import java.lang.ref.WeakReference

class CacheUpdateTask internal constructor(activity: AppCompatActivity) : AsyncTask<Unit, CacheUpdateTask.Progress, Unit>() {

    private val activityReference: WeakReference<AppCompatActivity> = WeakReference(activity)
    private lateinit var mProgressDialog: ProgressDialog

    class Progress(val title: String, val showFlag: Boolean)

    override fun onPreExecute() {

        this.mProgressDialog = ProgressDialog(activityReference.get())
        this.mProgressDialog.setTitle("Realizando actividad de refresco de caché")
        this.mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        this.mProgressDialog.isIndeterminate = true
        this.mProgressDialog.setCancelable(false)

    }

    override fun doInBackground(vararg params: Unit?) {

        val activity = activityReference.get()!!

        publishProgress(Progress("Reconstruyendo mapa desde localización remota...", true))
        NetworkHelper.fetchMapFromRemote(activity)

        publishProgress(Progress("Refrescando cache...", true))

    }

    override fun onProgressUpdate(vararg values: Progress?) {

        val progressObj = values[0]!!
        this.mProgressDialog.setTitle(progressObj.title)
        if (!mProgressDialog.isShowing && progressObj.showFlag) this.mProgressDialog.show()

    }

    override fun onPostExecute(result: Unit?) {

        val activity = activityReference.get()!!
        if (mProgressDialog.isShowing) mProgressDialog.dismiss()

        StorageHelper(activity)

    }

}