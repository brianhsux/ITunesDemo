package com.brianhsu.itunesdemo

import android.app.Activity
import android.app.Application
import com.brianhsu.itunesdemo.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

class ItunesApp: Application(), HasActivityInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        super.onCreate()

        // Dagger2
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)
    }
}