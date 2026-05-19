package com.nofeed.util

import android.content.Context
import android.provider.Settings

object PermissionHelper {

    fun hasOverlayPermission(context: Context): Boolean =
        Settings.canDrawOverlays(context)

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val componentName = "${context.packageName}/.service.FeedBlockerAccessibilityService"
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.split(":").any { it.equals(componentName, ignoreCase = true) }
    }

    fun allGranted(context: Context): Boolean =
        hasOverlayPermission(context) && isAccessibilityServiceEnabled(context)
}
