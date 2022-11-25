package com.udangtangtang.shockshack

import PostGoogleToken
import android.media.session.MediaSession
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @POST("api/v1/auth/registration/google")
    fun postGoogleIdToken(@Body idToken: String): Call<Void>
}