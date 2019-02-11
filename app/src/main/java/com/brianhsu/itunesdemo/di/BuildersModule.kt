package com.brianhsu.itunesdemo.di

import com.brianhsu.itunesdemo.activity.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    internal abstract fun contributeMainActivity(): MainActivity
}