package com.akstudios.KSTWV

import androidx.multidex.MultiDexApplication
import com.akstudios.KSTWV.ads.OpenApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KeywordSuggestionToolApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        OpenApp(this)
    }
}