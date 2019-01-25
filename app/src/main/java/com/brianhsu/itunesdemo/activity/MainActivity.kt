package com.brianhsu.itunesdemo.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.widget.Toast
import com.brianhsu.itunesdemo.model.MusicTermData
import kotlinx.android.synthetic.main.activity_main.*
import android.net.Uri
import android.support.v7.app.AlertDialog
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

    lateinit var binding: ActivityMainBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpBindings(savedInstanceState)
        setDataAndList()
        setSearchComponent()
    }

    override fun onStart() {
        super.onStart()
        initExoPlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun setUpBindings(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        if (savedInstanceState == null) {
            mainViewModel.init()
        }

        binding.mainViewModel = mainViewModel
    }

    private fun setDataAndList() {
        mainViewModel.musicDataList.observe(this, Observer<List<MusicTermData>> {
            musicListItems ->
            mainViewModel.isSearching.set(false)
            if (musicListItems != null && musicListItems.isNotEmpty()) {
                mainViewModel.setMusicDataInAdapter(musicListItems)
            } else {
                showErrorMessage(getString(R.string.error_response))
            }
        })

        mainViewModel.selected.observe(this, Observer<MusicTermData> {
            musicItem ->
            if (musicItem != null) {
                player?.stop()  // Stop whatever is playing
                val playTrackToast: String = getString(R.string.message_playing_track) + " " + musicItem.trackName
                Toast.makeText(this, playTrackToast, Toast.LENGTH_SHORT).show()

                currentMediaUrl = musicItem.previewUrl
                player?.prepare(getMediaSource(), true, false)
                showTrackInfoDialog(musicItem)
            }
        })
    }

    private fun setSearchComponent() {
        searchView.setOnQueryTextListener (object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchStr: String?): Boolean {
                if (searchStr != null) {
                    mainViewModel.retrieveMusicData(searchStr)
                    mainViewModel.isSearching.set(true)
                }

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
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
