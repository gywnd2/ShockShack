package com.udangtangtang.shockshack.model

import com.google.gson.annotations.SerializedName

data class normalLoginTokenModel (
    @SerializedName("accessToken")
    val accessToken:String,

    @SerializedName("refreshToken")
    val refreshToken:String

)