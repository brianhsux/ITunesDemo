package com.brianhsu.itunesdemo.model

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.brianhsu.itunesdemo.network.SearchService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MusicDataModel {
    val DEBUG_TAG: String = "MusicDataModel"
    var compositeDisposable: CompositeDisposable? = null
    var musicListLiveData: MutableLiveData<List<MusicTermData>> = MutableLiveData()
    private var musicListItems: MutableList<MusicTermData> = mutableListOf()

    fun retrieveMusicData(searchStr: String) {
        val searchService = SearchService.create()
        compositeDisposable = CompositeDisposable()

        musicListItems.clear()
        val response: Observable<SearchResultData> =
                searchService.getSearchResults(searchStr, SearchService.ENTITY_TYPE_MUSIC_TRACK)
        compositeDisposable?.add(
            response
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(DEBUG_TAG, "Result it: $it")
                musicListLiveData.value = it.resultData
            }, {
                error ->
                Log.d(DEBUG_TAG, "Result error: $error")
                musicListLiveData.value = emptyList()
            })
        )
    }
}