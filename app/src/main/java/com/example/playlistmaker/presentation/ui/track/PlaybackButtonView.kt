package com.example.playlistmaker.presentation.ui.track

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface Listener {
        fun onPlayRequested()
        fun onPauseRequested()
    }

    var listener: Listener? = null

    var isPlaying: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            updateA11y()
            invalidate()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
        isDither = true
    }

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null

    private val dstRect = RectF()
    private val tmpRect = Rect()

    init {
        isClickable = true
        isFocusable = true

        context.obtainStyledAttributes(attrs, R.styleable.PlaybackButtonView, defStyleAttr, 0).use { ta ->
            playBitmap = ta.getDrawable(R.styleable.PlaybackButtonView_playIcon)?.toBitmap()
            pauseBitmap = ta.getDrawable(R.styleable.PlaybackButtonView_pauseIcon)?.toBitmap()
        }
        updateA11y()
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {
        super.onSizeChanged(w, h, oldw, oldh)
        dstRect.set(0f, 0f, w.toFloat(), h.toFloat())
        tmpRect.set(0, 0, w, h)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val bitmap = if (isPlaying) pauseBitmap else playBitmap
        bitmap ?: return
        canvas.drawBitmap(bitmap, null, dstRect, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return super.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                val inside = tmpRect.contains(event.x.toInt(), event.y.toInt())
                if (inside) {
                    performClick()
                }
                return true
            }
            MotionEvent.ACTION_CANCEL -> return true
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (isPlaying) listener?.onPlayRequested() else listener?.onPauseRequested()
        return true
    }

    private fun updateA11y() {
        contentDescription = if (isPlaying) context.getString(R.string.pause) else context.getString(R.string.play)
    }
}