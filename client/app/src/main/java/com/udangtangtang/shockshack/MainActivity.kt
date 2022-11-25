package com.udangtangtang.shockshack

import PostGoogleToken
import android.app.ProgressDialog.show
import android.content.Intent
import android.content.IntentSender
import android.media.session.MediaSession
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.udangtangtang.shockshack.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    // Google Sign-in
    private lateinit var oneTapClient:SignInClient
    private lateinit var signInRequest:BeginSignInRequest
    private var REQ_ONE_TAP=100
    private var showOneTapUI=true
    private var TAG="MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide Action Bar
        supportActionBar?.hide()

        // Normal login Button
        binding.buttonMainLoginNormal.setOnClickListener {
            Toast.makeText(this, "일반 로그인으로 연결", Toast.LENGTH_SHORT).show()
        }

        // Google login button
        binding.buttonMainLoginGoogle.setOnClickListener {
            // Google Sign-in
            // Configure One-tap login
            oneTapClient=Identity.getSignInClient(this)
            signInRequest=BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build())
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .setAutoSelectEnabled(true)
                .build()

            // Display One-tap login UI
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(TAG, e.localizedMessage)
                }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQ_ONE_TAP->{
                try{
                    val credential=oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken=credential.googleIdToken
                    val username=credential.id
                    val password=credential.password
                    when{
                        idToken!=null->{
                            Log.d(TAG, "Got ID Token")
                            Log.d(TAG, idToken +"/"+ username)
                            // Retrofit
                            val retrofit = Retrofit.Builder().baseUrl("http://49.174.169.48:13883")
                                .addConverterFactory(GsonConverterFactory.create()).build()
                            val service=retrofit.create(RetrofitService::class.java)

                            service.postGoogleIdToken(idToken).enqueue(object:Callback<Void> {
                                override fun onResponse(
                                    call: Call<Void>,
                                    response: Response<Void>
                                ) {
                                    Log.d("Retrofit", "Token posted with status "+response.code().toString()+ " : " + idToken)
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Log.d("Retrofit", "Token post failed : " + t.message.toString())
                                }
                            })
                        }
                        password!=null->{
                            Log.d(TAG, "Got password.")
                        }
                        else ->{
                            Log.d(TAG, "No ID token or password!")
                        }
                    }
                }catch(e:ApiException){
                    when(e.statusCode){
                        CommonStatusCodes.CANCELED ->{
                            Log.d(TAG, "One-tap dialog was closed.")
                            showOneTapUI=false
                        }
                        CommonStatusCodes.NETWORK_ERROR->{
                            Log.d(TAG, "One-tap encountered a network error.")
                        }
                        else -> {
                            Log.d(TAG, "Couldn't get credential from result." +
                                    " (${e.localizedMessage})")
                        }
                    }
                }
            }
        }
    }
}