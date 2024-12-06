package com.zr.nebula.extension

import android.content.Context
import android.content.res.Configuration


fun Context.isPortrait(): Boolean =
    resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

fun Context.isLandscape(): Boolean = !isPortrait()