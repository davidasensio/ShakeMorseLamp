package com.handysparksoft.shakelamp

import android.app.Application
import com.handysparksoft.shakelamp.feature.flashlight.FlashlightModule
import com.handysparksoft.shakelamp.feature.flashlight.module
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class ShakeMorseLampApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            androidContext(this@ShakeMorseLampApplication)
            modules(FlashlightModule().module())
        }
    }
}
