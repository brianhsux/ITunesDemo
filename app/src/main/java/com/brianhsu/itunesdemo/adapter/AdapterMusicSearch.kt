package com.brianhsu.itunesdemo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.brianhsu.itunesdemo.R
import com.brianhsu.itunesdemo.model.MusicTermModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class AdapterMusicSearch(private val context: Context, private val musicTerms: List<MusicTermModel>,
                         private val itemClick: (MusicTermModel) -> Unit) :
        RecyclerView.Adapter<AdapterMusicSearch.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMusicItem(context, musicTerms[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.music_recycler_view_item, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return musicTerms.count()
    }

    inner class ViewHolder(itemView: View, private val itemClick: (MusicTermModel) -> Unit) :
            RecyclerView.ViewHolder(itemView) {
        private var musicImage: ImageView? = itemView.findViewById(R.id.musicImage)
        private var musicTitle: TextView? = itemView.findViewById(R.id.musicTitleTxt)
        private var musicDes: TextView? = itemView.findViewById(R.id.musicDesTxt)
        private var musicPlayBtn: ImageView? = itemView.findViewById(R.id.playMusicBtn)

        fun bindMusicItem(context: Context, musicTerm: MusicTermModel) {

            try {
                Glide.with(context).load(musicTerm.artworkUrl60)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(musicImage)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            musicTitle?.text = musicTerm.trackName
            musicDes?.text = musicTerm.artistName

            musicPlayBtn?.setOnClickListener {
                itemClick(musicTerm)
            }
        }
    }
}