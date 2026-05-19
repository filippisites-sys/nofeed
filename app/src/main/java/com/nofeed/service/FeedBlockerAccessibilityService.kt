package com.nofeed.service

import android.accessibilityservice.AccessibilityService
import android.content.SharedPreferences
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class FeedBlockerAccessibilityService : AccessibilityService() {

    private lateinit var overlayManager: OverlayManager
    private lateinit var prefs: SharedPreferences

    companion object {
        const val PREFS_NAME = "nofeed_prefs"
        const val KEY_BLOCKING_ENABLED = "blocking_enabled"

        var pauseUntil: Long = 0
        fun pauseMinutes(minutes: Int) {
            pauseUntil = System.currentTimeMillis() + minutes * 60 * 1000L
        }
        fun isPaused() = System.currentTimeMillis() < pauseUntil
    }

    override fun onServiceConnected() {
        overlayManager = OverlayManager(applicationContext)
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        Toast.makeText(this, "NoFeed ativo ✓", Toast.LENGTH_SHORT).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.packageName?.toString() != "com.instagram.android") return
        if (!prefs.getBoolean(KEY_BLOCKING_ENABLED, false) || isPaused()) {
            overlayManager.hide()
            return
        }

        val root = rootInActiveWindow ?: return

        if (FeedDetector.isOnFeed(root)) {
            val bounds = FeedDetector.findFeedBounds(root)
            if (bounds != null) {
                overlayManager.showAtBounds(bounds)
            }
            // If bounds not found yet (still loading), keep current state
        } else {
            overlayManager.hide()
        }
    }

    override fun onInterrupt() {
        if (::overlayManager.isInitialized) overlayManager.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayManager.isInitialized) overlayManager.hide()
    }
}
