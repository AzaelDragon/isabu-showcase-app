package co.gov.isabu.showcase.helpers

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference
import java.util.*

/**
 * Simple helper that allows retrieving internal properties from the "config.properties" file.
 */

class PreferenceHelper internal constructor(context: Context) {

    private val contextReference: WeakReference<Context> = WeakReference(context)
    private val properties = Properties()
    private var descriptorUrl: String

    /**
     * Loads the properties' key-value map to memory to fetch specific properties.
     */

    init {

        val inputStream = contextReference.get()!!.assets.open("config.properties")
        properties.load(inputStream)

        this.descriptorUrl = properties.getProperty("resource_descriptor_url")

    }

    /**
     * Get the media descriptor URL from the properties file.
     */

    fun getDescriptorUrl() : String {

        return this.descriptorUrl

    }

}