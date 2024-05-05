package com.laspika.laspika

import android.app.Application
import com.laspika.laspika.core.di.databaseModule
import com.laspika.laspika.core.di.firebaseModule
import com.laspika.laspika.core.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class LaspikaApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@LaspikaApplication)
            modules(
                listOf(
                    firebaseModule,
                    databaseModule,
                    viewModelModule
                )
            )
        }
    }
}