package com.brianhsu.itunesdemo.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Log
import com.brianhsu.itunesdemo.model.MusicDataModel
import com.brianhsu.itunesdemo.model.MusicTermData

class MainViewModel {
    val DEBUG_TAG: String = "MainViewModel"
    var musicDataModel: MusicDataModel = MusicDataModel()

    val musicDataList: ObservableField<MutableList<MusicTermData>> = ObservableField()
    val isSearching: ObservableBoolean = ObservableBoolean()

    fun refreshMusicData(searchStr: String) {
        isSearching.set(true)

        musicDataModel.retrieveMusicData(searchStr, object: MusicDataModel.onDataReadyCallback {
            override fun onDataReady(musicTermDataList: MutableList<MusicTermData>) {
                Log.d(DEBUG_TAG, "onDataReady(): $musicTermDataList")
//                musicDataList.set(musicTermDataList)
                isSearching.set(false)
            }
        })
    }
}