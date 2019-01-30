package com.brianhsu.itunesdemo.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.util.Log
import com.brianhsu.itunesdemo.adapter.MusicSearchAdapter
import com.brianhsu.itunesdemo.model.MusicDataModel
import com.brianhsu.itunesdemo.model.MusicTermData

class MainViewModel : ViewModel() {
    val DEBUG_TAG: String = "MainViewModel"

    lateinit var musicDataModel: MusicDataModel
    lateinit var musicSearchAdapter: MusicSearchAdapter
    val selected: MutableLiveData<MusicTermData> = MutableLiveData()
    val isSearching: ObservableBoolean = ObservableBoolean()

    val musicDataList: MutableLiveData<List<MusicTermData>>
        get() {
            return musicDataModel.musicListLiveData
        }

    fun init() {
        musicSearchAdapter = MusicSearchAdapter(this)
        musicDataModel = MusicDataModel()
    }

    fun retrieveMusicData(searchStr: String) {
        musicDataModel.retrieveMusicData(searchStr)
    }

    fun setMusicDataInAdapter(musicTermDatas: List<MusicTermData>) {
        musicSearchAdapter.musicTerms = musicTermDatas
        musicSearchAdapter.notifyDataSetChanged()
    }

    fun getAdapter(): MusicSearchAdapter {
        return musicSearchAdapter
    }

    fun onItemClicked(musicTermData: MusicTermData) {
        Log.d(DEBUG_TAG, "onItemClicked(): $musicTermData")
        selected.value = musicTermData
    }
}