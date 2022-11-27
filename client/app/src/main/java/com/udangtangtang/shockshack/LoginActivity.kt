package com.udangtangtang.shockshack

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.snackbar.Snackbar
import com.udangtangtang.shockshack.databinding.ActivityLoginBinding
import com.udangtangtang.shockshack.databinding.ActivityMainBinding
import com.udangtangtang.shockshack.model.normalLoginTokenModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import standardMemberModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding

    // Google Sign-in
    private lateinit var oneTapClient:SignInClient
    private lateinit var signInRequest:BeginSignInRequest
    private val REQ_ONE_TAP=100
    private var showOneTapUI=true
    private val TAG="MainActivity"

    // Retrofit
    private lateinit var retrofit: Retrofit
    private lateinit var service : RetrofitService

    // SharedPreferences
    private lateinit var pref : SharedPreferences

    private var backPressWaitTime:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide Action Bar
        supportActionBar?.hide()

        // Get SharedPreferences handle
        pref=this.getSharedPreferences("loginInfo", Context.MODE_PRIVATE)

        // Init Retrofit
        retrofit = Retrofit.Builder().baseUrl(getString(R.string.server_addr))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service=retrofit.create(RetrofitService::class.java)

        // Normal login Button
        binding.buttonLoginNormal.setOnClickListener {
            // Check if email / password are null or not
            var isEmailValid=false
            var isPasswordValid=false
            if(binding.inputTextLoginEmail.text.toString().equals("")||binding.inputTextLoginEmail.text.toString()==null){
                Toast.makeText(this, getString(R.string.hint_signup_email_null), Toast.LENGTH_SHORT).show()
            }else{
                // Check email pattern is valid
                if(Patterns.EMAIL_ADDRESS.matcher(binding.inputTextLoginEmail.text.toString()).matches()){ isEmailValid=true }
                else{
                    Toast.makeText(this, getString(R.string.hint_signup_email_pattern_invalid), Toast.LENGTH_SHORT).show()}

            }
            if(binding.inputTextLoginPassword.text.toString().equals("")||binding.inputTextLoginPassword.text.toString()==null){
                Toast.makeText(this, getString(R.string.hint_signup_password_null), Toast.LENGTH_SHORT).show()
            }else{ isPasswordValid=true }

            // If email/password are valid
            if(isEmailValid && isPasswordValid)
            {
                // Request POST Google Idtoken to server
                service.normalLogin(binding.inputTextLoginEmail.text.toString(), binding.inputTextLoginPassword.text.toString()).enqueue(object : Callback<normalLoginTokenModel> {
                    override fun onResponse(
                        call: Call<normalLoginTokenModel>,
                        response: Response<normalLoginTokenModel>
                    ) {
                        Log.d("Retrofit", "Token posted with status "+response.code().toString())
                        // Store idToken to SharedPreferences
                        // TODO : 일반 로그인 토큰 받아와야 함
                        Log.d("Login test", response.body()?.accessToken+" / "+response.body()?.refreshToken+" / "+binding.inputTextLoginEmail.text.toString())
                        with(pref.edit()){
                            remove("googleToken")
                            putString("accessToken", response.body()?.accessToken)
                            putString("refreshToken", response.body()?.refreshToken)
                            putString("email", binding.inputTextLoginEmail.text.toString())
                            apply()
                        }
                        // Start mainActivity
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }

                    override fun onFailure(call: Call<normalLoginTokenModel>, t: Throwable) {
                        Log.d("Retrofit", "Token post failed : " + t.message.toString())
                    }
                })
            }
        }

        // Normal Signup Button
        binding.buttonLoginSignupNormal.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Google login button
        binding.buttonLoginGoogle.setOnClickListener {
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
                .setAutoSelectEnabled(false)
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

                            // Request POST Google Idtoken to server
                            service.postGoogleIdToken(idToken).enqueue(object:Callback<Void> {
                                override fun onResponse(
                                    call: Call<Void>,
                                    response: Response<Void>
                                ) {
                                    Log.d("Retrofit", "Token posted with status "+response.code().toString()+ " : " + idToken)
                                    // Store idToken to SharedPreferences
                                    with(pref.edit()){
                                        remove("accessToken")
                                        remove("refreshToken")
                                        putString("googleToken", idToken)
                                        putString("email", username)
                                        apply()
                                    }
                                    // Start mainActivity
                                    startActivity(Intent(applicationContext, MainActivity::class.java))
                                    finish()
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

    override fun onBackPressed() {
        if(System.currentTimeMillis() - backPressWaitTime >=2000 ) {
            backPressWaitTime = System.currentTimeMillis()
            Snackbar.make(binding.root,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.",Snackbar.LENGTH_LONG).show()
        } else {
            finish()
        }
    }
}