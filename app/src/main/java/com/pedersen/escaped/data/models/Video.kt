package com.pedersen.escaped.data.models

import com.google.gson.annotations.SerializedName

data class Video(
        @SerializedName("title")
        val title: String = "",
        @SerializedName("body")
        val body: String = "",
        @SerializedName("viewed")
        var viewed: Boolean = false,
        @SerializedName("key")
        var key: String = "")