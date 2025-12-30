package com.zr.nebula.extension

import android.content.Context
import androidx.core.util.TypedValueCompat


@Suppress("UNCHECKED_CAST")
internal fun <T : Number> T.dp(context: Context): T =
    TypedValueCompat.dpToPx(this.toFloat(), context.resources.displayMetrics) as T

@Suppress("UNCHECKED_CAST")
internal fun <T : Number> T.sp(context: Context): T =
    TypedValueCompat.spToPx(this.toFloat(), context.resources.displayMetrics) as T