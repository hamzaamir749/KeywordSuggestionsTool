package com.akstudios.KSTWV

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.akstudios.KSTWV.ads.OpenApp
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KeywordSuggestionToolApp :MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()
        OpenApp(this)
    }
}