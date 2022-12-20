package com.udangtangtang.shockshack.model

import com.google.gson.annotations.SerializedName

data class NormalLoginTokenModel (
    @SerializedName("accessToken")
    val accessToken:String,

    @SerializedName("refreshToken")
    val refreshToken:String

)