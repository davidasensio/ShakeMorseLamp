package com.handysparksoft.shakelamp

import android.app.Application
import com.handysparksoft.shakelamp.core.common.CommonModule
import com.handysparksoft.shakelamp.core.common.module
import com.handysparksoft.shakelamp.core.morse.MorseModule
import com.handysparksoft.shakelamp.core.morse.module
import com.handysparksoft.shakelamp.feature.flashlight.FlashlightModule
import com.handysparksoft.shakelamp.feature.flashlight.module
import com.handysparksoft.shakelamp.feature.settings.SettingsModule
import com.handysparksoft.shakelamp.feature.settings.module
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
            modules(
                CommonModule().module(),
                MorseModule().module(),
                FlashlightModule().module(),
                SettingsModule().module(),
                AppModule().module(),
            )
        }
    }
}
