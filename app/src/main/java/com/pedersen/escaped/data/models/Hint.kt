package com.pedersen.escaped.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Hint(
        @SerializedName("id")
        val id: String = "",
        @SerializedName("title")
        val title: String = "",
        @SerializedName("body")
        val body: String = "",
        var hasAnimated: Boolean = false,
        var challenge: String = "",
        @SerializedName("key")
        var key: String = "") : Serializable
