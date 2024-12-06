package com.zr.nebulademo

import android.app.Application
import com.zr.nebula.Nebula

class CustomApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Nebula.init(this)
    }
}