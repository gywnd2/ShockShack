package com.udangtangtang.shockshack

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.snackbar.Snackbar
import com.udangtangtang.shockshack.databinding.ActivityLoginBinding
import com.udangtangtang.shockshack.model.normalLoginTokenModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

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
//        supportActionBar?.hide()

        // Get SharedPreferences handle
        pref=this.getSharedPreferences("loginInfo", Context.MODE_PRIVATE)

        // Init Retrofit
        retrofit = Retrofit.Builder().baseUrl(BuildConfig.SERVER_ADDRESS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service=retrofit.create(RetrofitService::class.java)

        // Auto login
        if (pref.getString("tokenIssuedDateTime", "null").equals("null")) {
            // No action to make user login
        // If token exist
        }else{
            val now=SimpleDateFormat(getString(R.string.token_datetime_format)).parse(SimpleDateFormat(getString(R.string.token_datetime_format)).format(Date(System.currentTimeMillis())))
            val tokenIssed=SimpleDateFormat(getString(R.string.token_datetime_format)).parse(pref.getString("tokenIssuedDateTime", "null"))

            val diff=(now.time-tokenIssed.time)
            val diffDays=(diff/1000)/(24*60*60)
            val diffHour=diff/(60*60*1000)

            // Check accessToken first
            if(diffDays>=1){
                // User have to login again
                Toast.makeText(applicationContext, pref.getString("tokenIssuedDateTime", "null"), Toast.LENGTH_SHORT).show()
                Snackbar.make(binding.root, getString(R.string.text_login_session_expired), Snackbar.LENGTH_LONG).show()
            }else if (diffHour>=1){
                // Request token to server
                Toast.makeText(applicationContext, "accessToken expired", Toast.LENGTH_SHORT).show()
                // Request POST Google Idtoken to server
                service.normalLogin(binding.inputTextLoginEmail.text.toString(), binding.inputTextLoginPassword.text.toString()).enqueue(object : Callback<normalLoginTokenModel> {
                    override fun onResponse(
                        call: Call<normalLoginTokenModel>,
                        response: Response<normalLoginTokenModel>
                    ) {
                        Log.d("Retrofit", "Token posted with status "+response.code().toString())
                        if (response.code().toString().equals("200")){
                            // Store idToken to SharedPreferences
                            with(pref.edit()){
                                remove("googleToken")
                                putString("tokenIssuedDateTime", SimpleDateFormat(getString(R.string.token_datetime_format)).format(Date(System.currentTimeMillis())))
                                putString("accessToken", response.body()?.accessToken)
                                putString("refreshToken", response.body()?.refreshToken)
                                putString("email", binding.inputTextLoginEmail.text.toString())
                                apply()
                            }
                            // Start mainActivity
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                            finish()
                        }else{
                            Snackbar.make(binding.root, getString(R.string.text_login_input_invalid), Snackbar.LENGTH_LONG).show()
                        }

                    }

                    override fun onFailure(call: Call<normalLoginTokenModel>, t: Throwable) {
                        Log.d("Retrofit", "Token post failed : " + t.message.toString())
                        Snackbar.make(binding.root, getString(R.string.text_login_check_connection), Snackbar.LENGTH_LONG).show()
                    }
                })
            }
            else{
                // Both tokens are valid
                Toast.makeText(applicationContext, "Token valid", Toast.LENGTH_SHORT).show()
                // Start mainActivity
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }

        // Normal login Button
        binding.buttonLoginNormal.setOnClickListener {
            // Hide keyboard first
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

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
                        if (response.code().toString().equals("200")){
                            // Store idToken to SharedPreferences
                            with(pref.edit()){
                                remove("googleToken")
                                putString("tokenIssuedDateTime", SimpleDateFormat(getString(R.string.token_datetime_format)).format(Date(System.currentTimeMillis())))
                                putString("accessToken", response.body()?.accessToken)
                                putString("refreshToken", response.body()?.refreshToken)
                                putString("email", binding.inputTextLoginEmail.text.toString())
                                apply()
                            }
                            // Start mainActivity
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                            finish()
                        }else{
                            Snackbar.make(binding.root, getString(R.string.text_login_input_invalid), Snackbar.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<normalLoginTokenModel>, t: Throwable) {
                        Log.d("Retrofit", "Token post failed : " + t.message.toString())
                        Snackbar.make(binding.root, getString(R.string.text_login_check_connection), Snackbar.LENGTH_LONG).show()
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
                        .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
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
                                    if (response.code()==200 || response.code()==400){
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
                                    else{
                                        Snackbar.make(binding.root, getString(R.string.text_login_google_failed), Snackbar.LENGTH_LONG).show()
                                    }

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
            Snackbar.make(binding.root,getString(R.string.text_hint_on_backpressed),Snackbar.LENGTH_LONG).show()
        } else {
            finish()
        }
    }
}