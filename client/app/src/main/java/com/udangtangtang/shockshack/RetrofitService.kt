package com.udangtangtang.shockshack

import com.udangtangtang.shockshack.model.normalLoginTokenModel
import standardMemberModel
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @POST("api/v1/auth/registration/google")
    fun postGoogleIdToken(@Body idToken: String): Call<Void>

    @POST("api/v1/auth/registration")
    fun postSignUpNewUser(@Body member: standardMemberModel): Call<Void>

    @FormUrlEncoded
    @POST("api/v1/auth/login")
    fun normalLogin(@Field("email") email:String, @Field("password") password:String): Call<normalLoginTokenModel>

    @POST("api/v1/queue/join")
    fun enterChatQueue(@Header("Authorization") token : String):Call<Void>
}