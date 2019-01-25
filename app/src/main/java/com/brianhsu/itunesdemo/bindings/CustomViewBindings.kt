package com.brianhsu.itunesdemo.bindings

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.widget.ImageView
import com.brianhsu.itunesdemo.adapter.MusicSearchAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object CustomViewBindings {

    @BindingAdapter("setAdapter")
    @JvmStatic fun bindRecyclerViewAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<MusicSearchAdapter.ViewHolder>) {
        recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = adapter
    }

    @BindingAdapter("imageUrl")
    @JvmStatic fun bindImageView(imageView: ImageView, imageUrl: String) {
        try {
            Glide.with(imageView.context)
                .load(imageUrl)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}