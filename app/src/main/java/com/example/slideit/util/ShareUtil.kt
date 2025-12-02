package com.example.slideit.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ShareUtil {

    private val QUICK_SHARE_COMPONENTS = listOf(
        // Samsung Quick Share
        "com.samsung.android.app.sharelive" to "com.samsung.android.app.sharelive.presentation.ui.MainActivity",
        // Google Quick Share (Pixel)
        "com.google.android.apps.photos" to "com.google.android.apps.photos.prewarm.PrewarmActivity"
    )

    fun createShareIntent(context: Context, file: File, mimeType: String): Intent {
        val fileUri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".fileprovider",
            file
        )

        val standardIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, fileUri)
            type = mimeType
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val packageManager = context.packageManager
        val resolvedActivities = packageManager.queryIntentActivities(standardIntent, 0)

        for ((pkg, cls) in QUICK_SHARE_COMPONENTS) {
            val isPackageInstalled = resolvedActivities.any { it.activityInfo.packageName == pkg }
            if (isPackageInstalled) {
                val explicitIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    type = mimeType
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setClassName(pkg, cls)
                }
                // Verify this specific component can handle the intent
                if (packageManager.resolveActivity(explicitIntent, 0) != null) {
                    return explicitIntent
                }
            }
        }

        // Fallback to the standard chooser
        return Intent.createChooser(standardIntent, "Share Business Card")
    }
}
