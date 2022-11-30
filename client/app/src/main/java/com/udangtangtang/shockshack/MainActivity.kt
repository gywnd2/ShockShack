package com.udangtangtang.shockshack

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.udangtangtang.shockshack.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    // SharedPreferences
    private lateinit var pref: SharedPreferences

    // Retrofit
    private lateinit var retrofit : Retrofit
    private lateinit var service : RetrofitService

    // Consider logged in from google or normal
    private lateinit var accountType : String
    private val Google="googleToken"
    private val Normal="accessToken"

    private var backPressWaitTime:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init Retrofit
        retrofit = Retrofit.Builder().baseUrl(BuildConfig.SERVER_ADDRESS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service=retrofit.create(RetrofitService::class.java)

        // Consider logged in from google or normal

        // Get SharedPreferences handle
        pref=this.getSharedPreferences("loginInfo", Context.MODE_PRIVATE)

        // Consider logged in from google or normal
        if(pref.getString(Google, "null").equals("null")){
            supportActionBar?.setTitle("Normal Account")
            accountType=Normal }
        else{
            supportActionBar?.setTitle("Google Account")
            accountType=Google }

        // Show test
        with(pref.edit()){
            binding.accessToken.text=pref.getString("accessToken","Null")
            binding.refreshToken.text=pref.getString("refreshToken", "Null")
            binding.googletoken.text=pref.getString("googleToken", "Null")
            binding.email.text=pref.getString("email","Null")
        }
        Snackbar.make(binding.root, "환영합니다 "+pref.getString("email", "Null")+" 님!", Snackbar.LENGTH_LONG).show()

        // Enter chat queue button
        binding.buttonMainEnqueue.setOnClickListener {
            Toast.makeText(this, pref.getString(accountType,"Null"), Toast.LENGTH_SHORT).show()
            service.enterChatQueue("Bearer "+pref.getString(accountType, "Null").toString()).enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("Retrofit", "Entered queue : "+pref.getString(accountType, "Null"))
                    Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("Retrofit", "Failed to enter queue")
                }

            })
        }
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - backPressWaitTime >=2000 ) {
            backPressWaitTime = System.currentTimeMillis()
            Snackbar.make(binding.root,getString(R.string.text_hint_on_backpressed), Snackbar.LENGTH_LONG).show()
        } else {
            finish()
        }
    }
}