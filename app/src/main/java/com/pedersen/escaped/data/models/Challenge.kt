package com.pedersen.escaped.data.models

import com.google.gson.annotations.SerializedName

data class Challenge(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String = "") {
    override fun toString(): String = title
}