package com.devmello.gymlog

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.devmello.gymlog.di.authModule
import com.devmello.gymlog.di.bmiModule
import com.devmello.gymlog.di.firebaseModule
import com.devmello.gymlog.di.formModule
import com.devmello.gymlog.di.homeModule
import com.devmello.gymlog.di.logModule
import com.devmello.gymlog.di.mainModule
import com.devmello.gymlog.di.repositoryModule
import com.devmello.gymlog.di.roomModule
import com.devmello.gymlog.di.stopwatchModule
import com.devmello.gymlog.di.userProfileModule
import com.devmello.gymlog.services.DropdownTimerService
import com.devmello.gymlog.services.StopwatchService
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
        val stopwatchNotificationChannel = NotificationChannel(
            StopwatchService.NOTIFICATION_ID,
            StopwatchService.NOTIFICATION_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.stopwatch_notification_description)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(dropdownTimerNotificationChannel)
        notificationManager.createNotificationChannel(stopwatchNotificationChannel)

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