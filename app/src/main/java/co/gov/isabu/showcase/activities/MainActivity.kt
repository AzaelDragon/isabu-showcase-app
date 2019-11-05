package co.gov.isabu.showcase.activities

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import co.gov.isabu.showcase.R
import co.gov.isabu.showcase.components.CyclicTransitionDrawable
import co.gov.isabu.showcase.helpers.MediaHelper
import co.gov.isabu.showcase.helpers.PreferenceHelper
import co.gov.isabu.showcase.tasks.CacheUpdateTask
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mHideHandler = Handler()

    private val mHidePart2Runnable = Runnable {

        main_layout.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

    }

    private val mShowPart2Runnable = Runnable {

        supportActionBar?.show()

    }

    private var mVisible: Boolean = false

    private val mHideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->

        delayedHide(AUTO_HIDE_DELAY_MILLIS)
        false

    }

    private var videoAmount = 0
    private var index = 0
    private lateinit var videoView: VideoView
    private lateinit var videoPaths: MutableList<String>

    /**
     * Create a new activity, set the automatic hiding toolbar object, generate a media helper and
     * fetch all images/videos to place on the Image view's canvas and the Video view's media player.
     */

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = "ISABU - Reproductor de videos"

        mVisible = true

        main_layout.setOnClickListener { toggle() }

        val mediaHelper = MediaHelper(this)
        val preferenceHelper = PreferenceHelper(this)
        val imageDrawables = mediaHelper.buildAllImageDrawables()

        val drawable = CyclicTransitionDrawable(imageDrawables)
        val imageView = findViewById<ImageView>(R.id.image_view)
        imageView.setImageDrawable(drawable)
        drawable.startTransition(preferenceHelper.getImageTransitionTime(), preferenceHelper.getImageChangeDelay())

        initializePlayer()

        videoView.setOnPreparedListener {mediaPlayer ->
            mediaPlayer.setVolume(100f, 100f)
            videoView.start()
        }

        videoView.setOnCompletionListener {
            if (index == videoAmount)  index = 0
            videoView.setVideoPath(videoPaths[index])
            videoView.start()
            index++
        }

    }

    /**
     * Initializes a new video player with the fetched video URIs generated from the JSON resource
     * map.
     */

    private fun initializePlayer() {

        this.videoView = findViewById(R.id.video_view)
        this.videoPaths = MediaHelper(this).getAllPaths(MediaHelper.MediaType.VIDEO)

        if (videoPaths.isNotEmpty()) {

            this.videoAmount = videoPaths.size
            videoView.requestFocus()
            Log.w("OWO", videoPaths.toString())
            videoView.setVideoPath(videoPaths[0])
            index++

        }

    }

    /**
     * Sets the handlers for the activity's action bar items.
     */

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_refresh_map -> CacheUpdateTask(this).execute()

            else -> super.onOptionsItemSelected(item)

        }

        return true

    }

    /**
     * Implements the main menu fragment into the activity's action bar.
     */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        if (menu is MenuBuilder) menu.setOptionalIconsVisible(true)

        return true

    }

    /**
     * Delays hiding the action bar for a small amount of time.
     */

    override fun onPostCreate(savedInstanceState: Bundle?) {

        super.onPostCreate(savedInstanceState)
        delayedHide(100)

    }

    /**
     * Decides how to toggle the action bar based on the mVisible class flag.
     */

    private fun toggle() {

        if (mVisible) {

            hide()

        } else {

            show()

        }

    }


    /**
     * Changes the Action bar status to hidden with an animation.
     */

    private fun hide() {

        supportActionBar?.hide()
        mVisible = false

        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())

    }

    /**
     * Changes the Action bar status to visible with an animation.
     */

    private fun show() {

        main_layout.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        mVisible = true

        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())

    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */

    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {

        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */

        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */

        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */

        private const val UI_ANIMATION_DELAY = 300

    }

}
