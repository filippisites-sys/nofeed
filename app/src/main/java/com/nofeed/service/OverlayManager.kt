package com.nofeed.service

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.nofeed.R

class OverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null

    fun showAtBounds(bounds: Rect) {
        if (overlayView != null) {
            val params = overlayView!!.layoutParams as WindowManager.LayoutParams
            if (params.x == bounds.left && params.y == bounds.top &&
                params.width == bounds.width() && params.height == bounds.height()
            ) return  // no change
            params.x = bounds.left
            params.y = bounds.top
            params.width = bounds.width()
            params.height = bounds.height()
            runCatching { windowManager.updateViewLayout(overlayView!!, params) }
            return
        }

        val view = LayoutInflater.from(context).inflate(R.layout.overlay_feed_blocker, null)

        val params = WindowManager.LayoutParams(
            bounds.width(),
            bounds.height(),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = bounds.left
            y = bounds.top
        }

        overlayView = view
        runCatching { windowManager.addView(view, params) }
    }

    fun hide() {
        overlayView?.let {
            runCatching { windowManager.removeView(it) }
            overlayView = null
        }
    }

    fun isShowing(): Boolean = overlayView != null
}
