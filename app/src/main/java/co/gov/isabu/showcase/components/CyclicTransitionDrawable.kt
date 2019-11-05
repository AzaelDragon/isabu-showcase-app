package co.gov.isabu.showcase.components

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.SystemClock

/**
 * A simple class to create a multiple-drawable single instance object representable in an image view
 * to generate a slideshow-like presentation.
 */

open class CyclicTransitionDrawable(var drawables: Array<Drawable>) : LayerDrawable(drawables),

    /**
     * Miscellaneous animation parameters and variables.
     */

    Drawable.Callback {
    private var currentDrawableIndex: Int = 0
    private var alphaValue = 0
    private var fromAlpha: Int = 0
    private var toAlpha: Int = 0
    private var duration: Long = 0
    private var startTimeMillis: Long = 0
    private var pauseDuration: Long = 0

    private var transitionStatus: TransitionState

    /**
     * Simple enum object to determine the current transition state within the object.
     */

    enum class TransitionState {

        STARTING,
        PAUSED, RUNNING

    }

    /**
     * Ensure that whenever a new drawable gets instantiated it starts in a 'Paused' state.
     */

    init {

        transitionStatus = TransitionState.PAUSED

    }

    /**
     * Initialization method for a simple animation using the specified parameters. This function
     * must be used as soon as the drawable is set in a view, or else the view might flicker.
     */

    fun startTransition(durationMillis: Int, pauseTimeMillis: Int) {

        fromAlpha = 0
        toAlpha = 255
        duration = durationMillis.toLong()
        pauseDuration = pauseTimeMillis.toLong()
        startTimeMillis = SystemClock.uptimeMillis()
        transitionStatus = TransitionState.PAUSED
        currentDrawableIndex = 0

        invalidateSelf()

    }

    /**
     * Draw the different specified images through object instantiation on the view's canvas.
     */

    override fun draw(canvas: Canvas) {

        var done = true

        if  (transitionStatus == TransitionState.STARTING) {

            done = false
            transitionStatus = TransitionState.RUNNING

        }
      
        else if (transitionStatus == TransitionState.PAUSED) {

            val uptimeCalc = SystemClock.uptimeMillis() - startTimeMillis

            if ( uptimeCalc > pauseDuration ) {

                done = false
                startTimeMillis = SystemClock.uptimeMillis()
                transitionStatus = TransitionState.RUNNING

            }
          
        }


        if (startTimeMillis >= 0) {

            var normalized = (SystemClock.uptimeMillis() - startTimeMillis).toFloat() / duration
            done = normalized >= 1.0f
            normalized = normalized.coerceAtMost(1.0f)
            alphaValue = (fromAlpha + (toAlpha - fromAlpha) * normalized).toInt()

        }

        if (transitionStatus == TransitionState.RUNNING) {

            var nextDrawableIndex = 0

            if (currentDrawableIndex + 1 < drawables.size) {

                nextDrawableIndex = currentDrawableIndex + 1

            }

            val currentDrawable = getDrawable(currentDrawableIndex)
            val nextDrawable = getDrawable(nextDrawableIndex)

            currentDrawable.alpha = 255 - alphaValue
            currentDrawable.draw(canvas)
            currentDrawable.alpha = 0xFF

            if (alphaValue > 0) {

                nextDrawable.alpha = alphaValue
                nextDrawable.draw(canvas)
                nextDrawable.alpha = 0xFF

            }

            if (done) {

                currentDrawableIndex = nextDrawableIndex
                startTimeMillis = SystemClock.uptimeMillis()

                transitionStatus = TransitionState.PAUSED

            }

        } else {

            getDrawable(currentDrawableIndex).draw(canvas)

        }

        invalidateSelf()

    }

}