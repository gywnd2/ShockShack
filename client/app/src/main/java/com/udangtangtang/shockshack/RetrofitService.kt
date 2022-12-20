package com.udangtangtang.shockshack

import com.udangtangtang.shockshack.model.JoinQueueResponseModel
import com.udangtangtang.shockshack.model.NormalLoginTokenModel
import StandardMemberModel
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @POST("api/v1/auth/registration/google")
    fun postGoogleIdToken(@Body idToken: String): Call<Void>

    @POST("api/v1/auth/registration")
    fun postSignUpNewUser(@Body member: StandardMemberModel): Call<Void>

    @FormUrlEncoded
    @POST("api/v1/auth/login")
    fun normalLogin(@Field("email") email:String, @Field("password") password:String): Call<NormalLoginTokenModel>

    @POST("api/v1/queue/join")
    fun enterChatQueue(@Header("Authorization") token : String):Call<JoinQueueResponseModel>
}