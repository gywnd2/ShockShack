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

    private val Google="idToken"
    private val Normal="accessToken"

    private var backPressWaitTime:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init Retrofit
        retrofit = Retrofit.Builder().baseUrl(getString(R.string.server_addr))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service=retrofit.create(RetrofitService::class.java)

        // Get SharedPreferences handle
        pref=this.getSharedPreferences("loginInfo", Context.MODE_PRIVATE)
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
            service.enterChatQueue("Bearer "+pref.getString(Google, "Null").toString()).enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("Retrofit", "Entered queue : "+pref.getString(Google, "Null"))
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
            Snackbar.make(binding.root,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG).show()
        } else {
            finish()
        }
    }
}