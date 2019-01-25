package com.brianhsu.itunesdemo.model

import com.google.gson.annotations.SerializedName

data class SearchResultData(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val resultData: List<MusicTermData>
)