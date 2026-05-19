package com.nofeed.service

import android.accessibilityservice.AccessibilityService
import android.content.SharedPreferences
import android.view.accessibility.AccessibilityEvent

class FeedBlockerAccessibilityService : AccessibilityService() {

    private lateinit var overlayManager: OverlayManager
    private lateinit var prefs: SharedPreferences

    companion object {
        const val PREFS_NAME = "nofeed_prefs"
        const val KEY_BLOCKING_ENABLED = "blocking_enabled"
    }

    override fun onServiceConnected() {
        overlayManager = OverlayManager(applicationContext)
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.packageName?.toString() != "com.instagram.android") return
        if (!prefs.getBoolean(KEY_BLOCKING_ENABLED, false)) {
            if (overlayManager.isShowing()) overlayManager.hide()
            return
        }

        val root = rootInActiveWindow ?: return
        if (FeedDetector.isOnFeed(root)) {
            overlayManager.show()
        } else {
            overlayManager.hide()
        }
    }

    override fun onInterrupt() {
        overlayManager.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayManager.isInitialized) overlayManager.hide()
    }
}
