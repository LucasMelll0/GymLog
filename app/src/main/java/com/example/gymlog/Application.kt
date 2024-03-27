package com.example.gymlog

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.gymlog.di.authModule
import com.example.gymlog.di.bmiModule
import com.example.gymlog.di.firebaseModule
import com.example.gymlog.di.formModule
import com.example.gymlog.di.homeModule
import com.example.gymlog.di.logModule
import com.example.gymlog.di.mainModule
import com.example.gymlog.di.repositoryModule
import com.example.gymlog.di.roomModule
import com.example.gymlog.di.stopwatchModule
import com.example.gymlog.di.userProfileModule
import com.example.gymlog.services.DropdownTimerService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        val dropdownTimerNotificationChannel = NotificationChannel(
            DropdownTimerService.NOTIFICATION_ID,
            DropdownTimerService.NOTIFICATION_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString(R.string.app_dropdown_timer_notification_description)
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(dropdownTimerNotificationChannel)

        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(
                roomModule,
                repositoryModule,
                mainModule,
                homeModule,
                formModule,
                logModule,
                bmiModule,
                authModule,
                firebaseModule,
                userProfileModule,
                stopwatchModule
            )
        }
    }
}