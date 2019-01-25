package com.brianhsu.itunesdemo.model

import android.util.Log
import com.brianhsu.itunesdemo.network.SearchService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MusicDataModel {
    val DEBUG_TAG: String = "MusicDataModel"
    var compositeDisposable: CompositeDisposable? = null
    private var musicListItems: MutableList<MusicTermData> = mutableListOf()

    fun retrieveMusicData(searchStr: String, callback: onDataReadyCallback) {
        Log.d(DEBUG_TAG, "retrieveMusicData(): $searchStr")
        val searchService = SearchService.create()
        compositeDisposable = CompositeDisposable()

        musicListItems.clear()
        val response: Observable<SearchResultData> =
                searchService.getSearchResults(searchStr, SearchService.ENTITY_TYPE_MUSIC_TRACK)
        compositeDisposable?.add(
            response
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    result ->
                    val resultModels = result.resultData
                    for (musicTerm: MusicTermData in resultModels) {
                        Log.d(DEBUG_TAG, "artistViewUrl: ${musicTerm.artistViewUrl}")
                        musicListItems.add(musicTerm)
                    }
                    callback.onDataReady(musicListItems)
                }, {
                    error ->
                    Log.d(DEBUG_TAG, "Result error: $error")
                })
        )
    }

    interface onDataReadyCallback {
        fun onDataReady(musicTermDataList: MutableList<MusicTermData>)
    }
}