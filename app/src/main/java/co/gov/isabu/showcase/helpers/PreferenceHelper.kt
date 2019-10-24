package co.gov.isabu.showcase.helpers

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference
import java.util.*

class PreferenceHelper internal constructor(context: Context) {

    private val contextReference: WeakReference<Context> = WeakReference(context)
    private val properties = Properties()
    private var descriptorUrl: String

    init {

        val inputStream = contextReference.get()!!.assets.open("config.properties")
        properties.load(inputStream)

        this.descriptorUrl = properties.getProperty("resource_descriptor_url")

    }

    fun getDescriptorUrl() : String {

        return this.descriptorUrl

    }

}