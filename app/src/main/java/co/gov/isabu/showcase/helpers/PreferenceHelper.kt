package co.gov.isabu.showcase.helpers

import android.content.Context
import java.lang.ref.WeakReference
import java.util.*

/**
 * Simple helper that allows retrieving internal properties from the "config.properties" file.
 */

class PreferenceHelper internal constructor(context: Context) {

    private val contextReference: WeakReference<Context> = WeakReference(context)
    private val properties = Properties()

    /**
     * Loads the properties' key-value map to memory to fetch specific properties.
     */

    init {

        val inputStream = contextReference.get()!!.assets.open("config.properties")
        properties.load(inputStream)

    }

    fun getDescriptorUrl() : String {

        return properties.getProperty("resource_descriptor_url")

    }

    fun getImageTransitionTime() : Int {

        return properties.getProperty("image_change_transition_time").toInt()

    }

    fun getImageChangeDelay() : Int {

        return properties.getProperty("image_change_delay").toInt()

    }


}