package com.brianhsu.itunesdemo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.brianhsu.itunesdemo.databinding.MusicRecyclerViewItemBinding
import com.brianhsu.itunesdemo.model.MusicTermData
import com.brianhsu.itunesdemo.viewmodel.MainViewModel

class MusicSearchAdapter(private val viewModel: MainViewModel) :
        RecyclerView.Adapter<MusicSearchAdapter.ViewHolder>() {

    var musicTerms: List<MusicTermData> = mutableListOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMusicItem(viewModel, musicTerms[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val musicTermBinding: MusicRecyclerViewItemBinding = MusicRecyclerViewItemBinding.inflate(
                layoutInflater, parent, false)
        return ViewHolder(musicTermBinding)
    }

    override fun getItemCount(): Int {
        return musicTerms.count()
    }

    inner class ViewHolder(private val musicTermBinding: MusicRecyclerViewItemBinding) :
            RecyclerView.ViewHolder(musicTermBinding.root) {

        fun bindMusicItem(viewModel: MainViewModel, musicTerm: MusicTermData) {
            musicTermBinding.musicTerm = musicTerm
            musicTermBinding.mainViewModel = viewModel
            musicTermBinding.executePendingBindings()
        }
    }
}