package com.brianhsu.itunesdemo.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.widget.Toast
import com.brianhsu.itunesdemo.adapter.AdapterMusicSearch
import com.brianhsu.itunesdemo.model.MusicTermModel
import com.brianhsu.itunesdemo.model.SearchResultModel
import com.brianhsu.itunesdemo.network.SearchService
import com.brianhsu.itunesdemo.services.MusicItemService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import android.content.DialogInterface
import android.net.Uri
import android.support.v7.app.AlertDialog
import com.brianhsu.itunesdemo.BuildConfig
import com.brianhsu.itunesdemo.R

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

    var compositeDisposable: CompositeDisposable? = null
    private var player: SimpleExoPlayer? = null
    private var currentMediaUrl: String? = null
    private var playbackPosition: Long = 0
    private var dataSourceFactory: DefaultHttpDataSourceFactory? = null
    private var musicListItems: MutableList<MusicTermModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        musicRecyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        musicRecyclerView.setHasFixedSize(true)
        musicRecyclerView.isNestedScrollingEnabled = false

        musicListItems = MusicItemService.musicItems

        setDataAndList()
        setSearchComponent()
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
                val searchService = SearchService.create()
                compositeDisposable = CompositeDisposable()

                if (searchStr != null) {
                    musicListItems.clear()
                    val response: Observable<SearchResultModel> =
                            searchService.getSearchResults(searchStr, SearchService.ENTITY_TYPE_MUSIC_TRACK)
                    compositeDisposable?.add(
                            response
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({
                                        result ->
                                        val resultModels = result.resultModels
                                        for (item: MusicTermModel in resultModels) {
                                            Log.d(DEBUG_TAG, "artistViewUrl: ${item.artistViewUrl}")
                                            musicListItems.add(item)
                                        }
                                        refreshMusicItems()
                                    }, {
                                        error ->
                                        Log.d(DEBUG_TAG, "Result error: $error")
                                    }))

                }

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
        musicRecyclerView?.adapter?.notifyDataSetChanged()
    }

    private fun showTrackInfoDialog(musicTermModel: MusicTermModel) {
        AlertDialog.Builder(this)
                .setTitle(musicTermModel.trackName)
                .setMessage(musicTermModel.artistName)
                .setPositiveButton(R.string.text_close, null)
                .setOnDismissListener(DialogInterface.OnDismissListener { player?.stop() }).show()
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
}
