package com.brianhsu.itunesdemo.model

import com.google.gson.annotations.SerializedName

data class SearchResultModel(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val resultModels: List<MusicTermModel>
)