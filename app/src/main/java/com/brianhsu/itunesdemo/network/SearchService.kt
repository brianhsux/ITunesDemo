package com.brianhsu.itunesdemo.network

import com.brianhsu.itunesdemo.model.SearchResultData
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("search")
    fun getSearchResults(
            @Query("term") searchTerm: String,
            @Query("entity") entityType: String)
            : Observable<SearchResultData>

    companion object {
        private const val BASE_ITUNES_URL: String = "https://itunes.apple.com/"
        const val ENTITY_TYPE_MUSIC_TRACK: String = "musicTrack"

        fun create(): SearchService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_ITUNES_URL)
                    .build()

            return retrofit.create(SearchService::class.java)
        }
    }
}