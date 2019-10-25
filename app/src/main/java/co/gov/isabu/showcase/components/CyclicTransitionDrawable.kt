package co.gov.isabu.showcase.components

import android.graphics.Canvas
import android.graphics.drawable.LayerDrawable
import android.os.SystemClock
import android.graphics.drawable.Drawable

open class CyclicTransitionDrawable(var drawables: Array<Drawable>) : LayerDrawable(drawables),

    Drawable.Callback {
    private var currentDrawableIndex: Int = 0
    private var alphaValue = 0
    private var fromAlpha: Int = 0
    private var toAlpha: Int = 0
    private var duration: Long = 0
    private var startTimeMillis: Long = 0
    private var pauseDuration: Long = 0

    private var transitionStatus: TransitionState

    enum class TransitionState {

        STARTING,
        PAUSED, RUNNING

    }

    init {
        transitionStatus = TransitionState.PAUSED
    }

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

    override fun draw(canvas: Canvas) {

        var done = true

        when (transitionStatus) {

            TransitionState.STARTING -> {

                done = false
                transitionStatus = TransitionState.RUNNING

            }

            TransitionState.PAUSED -> {

                val uptimeCalc = SystemClock.uptimeMillis() - startTimeMillis

                if ( uptimeCalc > pauseDuration ) {

                    done = false
                    startTimeMillis = SystemClock.uptimeMillis()
                    transitionStatus = TransitionState.RUNNING

                }

            }

            TransitionState.RUNNING -> {}

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