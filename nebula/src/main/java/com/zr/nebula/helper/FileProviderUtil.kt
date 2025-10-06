package com.zr.nebula.helper

import android.content.Context
import androidx.core.content.FileProvider

internal object FileProviderUtil {

    fun getUriForFile(context: Context, file: java.io.File): android.net.Uri {
        // Try to find the correct FileProvider authority dynamically
        val pm = context.packageManager
        val providers = pm.queryContentProviders(context.packageName, 0, 0)
        val nebulaProvider = providers.firstOrNull {
            it.name?.contains("NebulaFileProvider") == true 
        }

        val authority = nebulaProvider?.authority
            ?: "com.zr.nebula.fileprovider" // fallback, should exist from manifest

        return FileProvider.getUriForFile(context, authority, file)
    }
}
