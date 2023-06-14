package com.example.gymlog

import android.app.Application
import com.example.gymlog.di.authModule
import com.example.gymlog.di.bmiModule
import com.example.gymlog.di.formModule
import com.example.gymlog.di.homeModule
import com.example.gymlog.di.logModule
import com.example.gymlog.di.repositoryModule
import com.example.gymlog.di.roomModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(
                roomModule,
                repositoryModule,
                homeModule,
                formModule,
                logModule,
                bmiModule,
                authModule
            )
        }
    }
}