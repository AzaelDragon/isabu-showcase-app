package co.gov.isabu.showcase.tasks

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.gov.isabu.showcase.helpers.NetworkHelper
import co.gov.isabu.showcase.helpers.StorageHelper
import org.json.JSONObject
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

/**
 * Asynchronous Worker Task for fetching the multimedia map to the System's Internal Storage.
 */

class JSONDownloadTask internal constructor(activity: AppCompatActivity) : AsyncTask<Unit, Int, JSONObject>() {

    private val activityReference: WeakReference<AppCompatActivity> = WeakReference(activity)
    private val logTag = "Application"

    /**
     * Figures out if there's network connectivity, fetches the JSON file and places it in the
     * system's internal storage.
     *
     * @return the fetched JSON object that could be an empty descriptor on download failure.
     */

    override fun doInBackground(vararg params: Unit?) : JSONObject {

        val activity = activityReference.get()!!

        return NetworkHelper.fetchMapFromRemote(activity)

    }

    /**
     * Displays a Toast depending on the outcome of doInBackground(), and launches the storage helper
     * to refresh the map indexes.
     */

    override fun onPostExecute(result: JSONObject?) {

        val activity = activityReference.get()!!
        val emptyReference = StorageHelper.buildEmptyDescriptor()

        if (result!! != emptyReference) {

            Toast.makeText(
                activity.applicationContext,
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

        doRestart(activity)

    }

    /**
     * Restarts the application in order to make sure that all changes to the map were applied
     * successfully after a restart, generating a new Storage Helper.
     */

    private fun doRestart(c: Context) {

        try {

            val pm = c.packageManager

            if (pm != null) {

                val mStartActivity = pm.getLaunchIntentForPackage(c.packageName)

                if (mStartActivity != null) {

                    mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                    val mPendingIntentId = 223344
                    val mPendingIntent = PendingIntent.getActivity(
                        c, mPendingIntentId, mStartActivity,
                        PendingIntent.FLAG_CANCEL_CURRENT
                    )

                    val mgr = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)

                    exitProcess(0)

                } else {

                    Log.e(logTag, "Was not able to restart application, mStartActivity null")

                }
            } else {

                Log.e(logTag, "Was not able to restart application, PM null")

            }
        } catch (ex: Exception) {

            Log.e(logTag, "Was not able to restart application")

        }

    }

}