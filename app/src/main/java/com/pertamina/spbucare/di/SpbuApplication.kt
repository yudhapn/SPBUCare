package com.pertamina.spbucare.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

open class SpbuApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SpbuApplication)
            modules(appComponent)
        }
    }
}