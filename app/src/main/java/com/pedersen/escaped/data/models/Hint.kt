package com.pedersen.escaped.data.models

import com.google.gson.annotations.SerializedName

data class Hint(
        @SerializedName("id")
        val id: String = "",
        @SerializedName("title")
        val title: String = "",
        @SerializedName("body")
        val body: String = "", var hasAnimated: Boolean = false)
