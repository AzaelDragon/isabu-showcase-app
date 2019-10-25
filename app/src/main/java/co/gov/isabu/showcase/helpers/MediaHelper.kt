package co.gov.isabu.showcase.helpers

import org.json.JSONObject
import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference


class MediaHelper(activity: AppCompatActivity) {

    enum class MediaType { VIDEO, IMAGE }
    private val activityReference: WeakReference<AppCompatActivity> = WeakReference(activity)
    private val resourceMap: JSONObject

    init {

        val prefs = activityReference.get()!!.getSharedPreferences(
            "co.gov.isabu.showcase", MODE_PRIVATE
        )

        val mapStr = prefs.getString("co.gov.isabu.showcase.map", StorageHelper.buildEmptyDescriptor().toString())
        resourceMap = JSONObject(mapStr!!)

    }

    fun buildAllImageDrawables() : Array<Drawable> {

        val drawableList = mutableListOf<Drawable>()
        val rawPaths = getAllPaths(MediaType.IMAGE)

        for (path in rawPaths) {

            val drawable = Drawable.createFromPath(path)

            if (drawable != null) {

                drawableList.add(drawable)

            }

        }

        return drawableList.toTypedArray()

    }

    private fun getAllPaths(type: MediaType) : MutableList<String> {

        val key = when (type) {

            MediaType.IMAGE -> "images"
            MediaType.VIDEO -> "videos"

        }

        val activity = activityReference.get()!!
        val mediaMap = resourceMap.getJSONArray(key)
        val mediaAmount = mediaMap.length()
        val mediaLocations = mutableListOf<String>()

        for (i in 0 until mediaAmount) {

            val resourceName = mediaMap.getJSONObject(i).getString("name")
            val localPath = StorageHelper.buildStoragePath(activity, resourceName)
            mediaLocations.add(localPath)
        }

        return mediaLocations

    }

}