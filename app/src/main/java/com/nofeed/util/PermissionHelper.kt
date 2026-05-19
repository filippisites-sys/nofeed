package com.nofeed.util

import android.content.Context
import android.provider.Settings

object PermissionHelper {

    fun hasOverlayPermission(context: Context): Boolean =
        Settings.canDrawOverlays(context)

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        // Android stores: "com.nofeed/com.nofeed.service.FeedBlockerAccessibilityService"
        return enabled.contains(context.packageName, ignoreCase = true)
    }

    fun allGranted(context: Context): Boolean =
        hasOverlayPermission(context) && isAccessibilityServiceEnabled(context)
}
