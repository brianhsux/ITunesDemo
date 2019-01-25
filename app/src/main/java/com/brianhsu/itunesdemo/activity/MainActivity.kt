package com.brianhsu.itunesdemo.activity

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.widget.Toast
import com.brianhsu.itunesdemo.adapter.AdapterMusicSearch
import com.brianhsu.itunesdemo.model.MusicTermData
import com.brianhsu.itunesdemo.services.MusicItemService
import kotlinx.android.synthetic.main.activity_main.*
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.view.View
import com.brianhsu.itunesdemo.BuildConfig
import com.brianhsu.itunesdemo.R
import com.brianhsu.itunesdemo.databinding.ActivityMainBinding
import com.brianhsu.itunesdemo.viewmodel.MainViewModel

import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory

class MainActivity : AppCompatActivity() {

    val DEBUG_TAG: String = "ITunesDemo"

    private var player: SimpleExoPlayer? = null
    private var currentMediaUrl: String? = null
    private var playbackPosition: Long = 0
    private var dataSourceFactory: DefaultHttpDataSourceFactory? = null
    private var musicListItems: MutableList<MusicTermData> = mutableListOf()

    lateinit var binding: ActivityMainBinding
    val mainViewModel: MainViewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainViewModel = mainViewModel

        init()
    }

    private fun init() {
        musicRecyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        musicRecyclerView.setHasFixedSize(true)
        musicRecyclerView.isNestedScrollingEnabled = false

        musicListItems = MusicItemService.musicItems

        setDataAndList()
        setSearchComponent()

        progressBar.visibility = View.INVISIBLE
    }

    private fun setDataAndList() {
        //set data and list adapter
        val gridSectionAdapter = AdapterMusicSearch(this, musicListItems) {
            musicItem ->
            player?.stop()  // Stop whatever is playing
            val playTrackToast: String = getString(R.string.message_playing_track) + " " + musicItem.trackName
            Toast.makeText(this, playTrackToast, Toast.LENGTH_SHORT).show()

            currentMediaUrl = musicItem.previewUrl
            player?.prepare(getMediaSource(), true, false)
            showTrackInfoDialog(musicItem)
        }

        musicRecyclerView?.adapter = gridSectionAdapter
    }

    private fun setSearchComponent() {
        searchView.setOnQueryTextListener (object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchStr: String?): Boolean {
                if (searchStr != null) {
                    mainViewModel.refreshMusicData(searchStr)
                }

//                progressBar.visibility = View.VISIBLE
//                val searchService = SearchService.create()
//                compositeDisposable = CompositeDisposable()
//
//                if (searchStr != null) {
//                    musicListItems.clear()
//                    val response: Observable<SearchResultData> =
//                        searchService.getSearchResults(searchStr, SearchService.ENTITY_TYPE_MUSIC_TRACK)
//                    compositeDisposable?.add(
//                        response
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeOn(Schedulers.io())
//                        .subscribe({
//                            result ->
//                            progressBar.visibility = View.INVISIBLE
//                            val resultModels = result.resultData
//                            for (musicTerm: MusicTermData in resultModels) {
//                                Log.d(DEBUG_TAG, "artistViewUrl: ${musicTerm.artistViewUrl}")
//                                musicListItems.add(musicTerm)
//                            }
//                            refreshMusicItems()
//                        }, {
//                            error ->
//                            progressBar.visibility = View.INVISIBLE
//                            Log.d(DEBUG_TAG, "Result error: $error")
//                            showErrorMessage(error.toString())
//                        })
//                    )
//
//                }

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
    }

    override fun onStart() {
        super.onStart()
        initExoPlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun refreshMusicItems() {
        if (musicListItems.size > 0) {
            musicRecyclerView?.adapter?.notifyDataSetChanged()
        } else {
            showErrorMessage(getString(R.string.error_response))
        }
    }

    private fun showTrackInfoDialog(musicTermData: MusicTermData) {
        AlertDialog.Builder(this)
            .setTitle(musicTermData.trackName)
            .setMessage(musicTermData.artistName)
            .setPositiveButton(R.string.text_close, null)
            .setOnDismissListener { player?.stop() }.show()
    }

    private fun initExoPlayer() {
        dataSourceFactory = DefaultHttpDataSourceFactory("exoplayer-" + BuildConfig.APPLICATION_ID)
        player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(this),
                DefaultTrackSelector(), DefaultLoadControl())
        player?.playWhenReady = true

        if (currentMediaUrl != null) {
            player?.prepare(getMediaSource())
            player?.seekTo(playbackPosition)
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            // Storing the playback position for resume
            playbackPosition = player!!.currentPosition
            player?.release()
            player = null
        }
    }

    private fun getMediaSource(): MediaSource {
        return ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(currentMediaUrl))
    }

    private fun showErrorMessage(error: String) {
        val errorToast: String = error
        Toast.makeText(this, errorToast, Toast.LENGTH_SHORT).show()
    }
}
