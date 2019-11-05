package co.gov.isabu.showcase.helpers

import android.content.Context.MODE_PRIVATE
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.File
import java.lang.ref.WeakReference

class MediaHelper(activity: AppCompatActivity) {

    /**
     * A simple enum defining which type of resource the invocation is interested in.
     */
    enum class MediaType { VIDEO, IMAGE }

    private val activityReference: WeakReference<AppCompatActivity> = WeakReference(activity)
    private val resourceMap: JSONObject

    /**
     * Find the required media map and fetch it's strings to memory. If there is not a valid media
     * map, a new, empty one will be used instead returning an empty array of strings.
     */

    init {

        val prefs = activityReference.get()!!.getSharedPreferences(
            "co.gov.isabu.showcase", MODE_PRIVATE
        )

        val mapStr = prefs.getString("co.gov.isabu.showcase.map", StorageHelper.buildEmptyDescriptor().toString())
        resourceMap = JSONObject(mapStr!!)

    }

    /**
     * Finds all the potential drawables in the resource map, instantiating a new in-memory drawable
     * for each one of the valid and existing routes.
     *
     * @return An array of ImageView-friendly drawables.
     */

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

    /**
     * Finds all the path of the specified [type] as in the enum previously defined, using simple
     * path regex techniques and using the current in-memory resource map.
     */

    fun getAllPaths(type: MediaType) : MutableList<String> {

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
            if (File(localPath).exists()) mediaLocations.add(localPath)

        }

        return mediaLocations

    }

}