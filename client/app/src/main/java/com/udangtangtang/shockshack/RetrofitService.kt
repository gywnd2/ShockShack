package com.udangtangtang.shockshack

import standardMemberModel
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @POST("api/v1/auth/registration/google")
    fun postGoogleIdToken(@Body idToken: String): Call<Void>

    @POST("api/v1/auth/registration")
    fun postSignUpNewUser(@Body member: standardMemberModel): Call<Void>
}