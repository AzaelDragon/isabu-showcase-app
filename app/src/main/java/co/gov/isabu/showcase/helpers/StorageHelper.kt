package co.gov.isabu.showcase.helpers

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.gov.isabu.showcase.tasks.JSONDownloadTask
import co.gov.isabu.showcase.tasks.MediaDownloadTask
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.ref.WeakReference

/*
 * Storage manager for both cache and file retrieval.
 */

class StorageHelper(activity: AppCompatActivity) {

    private val activityReference: WeakReference<AppCompatActivity> = WeakReference(activity)
    private val cacheLocations = mutableListOf<String>()
    private val resourceMap: JSONObject
    val imageMap: JSONArray
    val videoMap: JSONArray

    /**
     * Load the resource map into memory, downloading it if possible.
     */

    init {

        this.resourceMap = loadResourceMap()
        this.imageMap = resourceMap.getJSONArray("images")
        this.videoMap = resourceMap.getJSONArray("videos")
        updateLocationsFromMap()
        MediaDownloadTask(activityReference.get()!!, this).execute()

    }

    /**
     * Using extracted File names from the resource map, updates the local "Must Have" cache.
     */

    private fun updateLocationsFromMap() {

        val imageNames = extractNamesFromMap(imageMap)
        val videoNames = extractNamesFromMap(videoMap)

        this.cacheLocations.addAll(imageNames)
        this.cacheLocations.addAll(videoNames)

    }

    /**
     * Finds and extracts all the File names from the specified JSONArray map to a list.
     *
     * @param map The map to process.
     * @return a list with all the found URI locations.
     */

    private fun extractNamesFromMap(map: JSONArray) : MutableList<String> {

        val extractedList = mutableListOf<String>()

        for (i in 0 until map.length()) {

            val entry = map.getJSONObject(i)
            val path = entry.getString("name")
            extractedList.add(path)

        }

        return extractedList

    }

    /**
     * Loads the Resource Map JSON into memory, attempting to download it if it could not be found
     * in the application's files.
     *
     * @return a JSONObject representing the resources map, which could be empty if a connection was
     * never available.
     */

    private fun loadResourceMap() : JSONObject {

        val mapMath = buildStoragePath(activityReference.get()!!,"map.json")

        return if (fileExists(mapMath)) {

            Toast.makeText(
                activityReference.get(),
                "Mapa de multimedia cargado con éxito.",
                Toast.LENGTH_SHORT)
                .show()

            loadJsonFromFile(mapMath)

        } else {

            Toast.makeText(
                activityReference.get(),
                "No se ha encontrado un mapa en caché. Intentando descargar uno nuevo...",
                Toast.LENGTH_SHORT)
                .show()

            JSONDownloadTask(activityReference.get()!!).execute()
            buildEmptyDescriptor()

        }

    }

    /**
     * Loads a JSON file from a file to memory.
     *
     * @param pathName The fully qualified absolute path to the JSON file.
     * @return the parsed JSON file as a JSONObject. If the deserialization files, returns
     * an empty object.
     */

    private fun loadJsonFromFile(pathName: String) : JSONObject {

        val file = File(pathName)
        val rawData = file.readText(Charsets.UTF_8)

        return try {

            JSONObject(rawData)

        }  catch (e: JSONException) {

            JSONObject()

        }

    }

    companion object {

        /**
         * Builds an empty sample resource map to be used in case of failure while fetching the remote
         * JSON descriptor.
         *
         * @return A sample JSON object with a schema, images and videos fields.
         */

        fun buildEmptyDescriptor(): JSONObject {

            val json = JSONObject()

            json.put("\$schema", "http://json-schema.org/draft-07/schema")
            json.put("images", JSONArray())
            json.put("videos", JSONArray())

            return json

        }

        /**
         * Builds an absolute external storage file path for the specified filename.
         *
         * @param fileName The name of the file to build the absolute path from.
         * @return The built absolute external storage path for the specified filename.
         */

        fun buildStoragePath(activity: AppCompatActivity, fileName: String): String {

            val basePath = activity.applicationContext.filesDir.absolutePath
            val systemSeparator = File.separator
            return "${basePath}${systemSeparator}${fileName}"

        }

        /**
         * Quickly checks if a file exists in the external storage.
         *
         * @param fileName The name of the file to verify without any other path.
         * @return true if the specified file exists, false if it doesn't.
         */

        fun fileExists(fileName: String) : Boolean {

            val tmpFile = File(fileName)
            return tmpFile.exists()

        }

    }

}