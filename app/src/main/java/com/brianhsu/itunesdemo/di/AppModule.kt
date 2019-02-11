package com.brianhsu.itunesdemo.di

import com.brianhsu.itunesdemo.network.SearchService
import dagger.Module
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import javax.inject.Singleton
import dagger.Provides
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

@Module
class AppModule {

    private val BASE_ITUNES_URL: String = "https://itunes.apple.com/"

    @Provides
    @Singleton
    fun provideSearchService(): SearchService {
        return Retrofit.Builder()
                .baseUrl(BASE_ITUNES_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(SearchService::class.java)
    }
}