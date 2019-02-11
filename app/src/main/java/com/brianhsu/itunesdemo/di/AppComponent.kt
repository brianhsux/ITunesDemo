package com.brianhsu.itunesdemo.di

import com.brianhsu.itunesdemo.ItunesApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = [(AndroidSupportInjectionModule::class), (AppModule::class), (BuildersModule::class)])
//@Component(modules = [(AndroidSupportInjectionModule::class), (AppModule::class)])
//@Component
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: ItunesApp): Builder
        fun build(): AppComponent
    }

    fun inject(app: ItunesApp)
}