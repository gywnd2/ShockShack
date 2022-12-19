package com.udangtangtang.shockshack

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.get
import com.google.android.material.snackbar.Snackbar
import com.udangtangtang.shockshack.databinding.ActivityMainBinding
import com.udangtangtang.shockshack.databinding.DrawerMainHeaderBinding
import com.udangtangtang.shockshack.databinding.LayoutToolbarMainBinding
import com.udangtangtang.shockshack.model.joinQueueResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    // TODO : Encrypt SharedPreferences

    private lateinit var binding:ActivityMainBinding
    private lateinit var toolbarBinding:LayoutToolbarMainBinding

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
        toolbarBinding=LayoutToolbarMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set ToolBar
        setSupportActionBar(toolbarBinding.root)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // NavigationView
        binding.navigationMain.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_main_drawer_logout -> {
                    // Remove token
                    with(pref.edit()){
                        putString(getString(R.string.pref_checkbox_autologin), "false")
                        apply()
                    }
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    Toast.makeText(applicationContext, getString(R.string.text_main_logout), Toast.LENGTH_LONG).show()
                    this.finish()
                    true
                }
                R.id.aa -> {
                    Toast.makeText(applicationContext, "Menu 1", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.bb -> {
                    Toast.makeText(applicationContext, "Menu 2", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.cc -> {
                    Toast.makeText(applicationContext, "Menu 3", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    Toast.makeText(applicationContext, "?????", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }

        // Drawer open/close Button
        binding.toolbarMain.root.findViewById<ImageButton>(R.id.button_main_toolbar_open).setOnClickListener {
            binding.layoutMainDrawer.openDrawer(GravityCompat.START)
        }


        binding.navigationMain.getHeaderView(0).findViewById<ImageButton>(R.id.button_main_toolbar_close).setOnClickListener {
            binding.layoutMainDrawer.closeDrawer(GravityCompat.START)
        }

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

        // Show Profile
        binding.navigationMain.getHeaderView(0).findViewById<TextView>(R.id.text_main_drawer_header_profile_email).text=pref.getString("email", "null")
        if (accountType==Google) {binding.navigationMain.getHeaderView(0).findViewById<TextView>(R.id.text_main_drawer_header_profile_usertype).text="Google Account"}
        else {binding.navigationMain.getHeaderView(0).findViewById<TextView>(R.id.text_main_drawer_header_profile_usertype).text="Normal Account"}

        // Show test
        with(pref.edit()){
            binding.accessToken.text=pref.getString("accessToken","Null")
            binding.refreshToken.text=pref.getString("refreshToken", "Null")
            binding.googletoken.text=pref.getString("googleToken", "Null")
            binding.email.text=pref.getString("tokenIssuedDateTime","Null")
        }
        Snackbar.make(binding.root, "환영합니다 "+pref.getString("email", "Null")+" 님!", Snackbar.LENGTH_LONG).show()

        // Enter chat queue button
        binding.buttonMainEnqueue.setOnClickListener {
            service.enterChatQueue("Bearer "+pref.getString(accountType, "Null").toString()).enqueue(object : Callback<joinQueueResponseModel>{
                override fun onResponse(call: Call<joinQueueResponseModel>, response: Response<joinQueueResponseModel>) {
                    Log.d("Retrofit", "Entered queue : "+pref.getString(accountType, "Null")+"Response code : "+ response.code().toString())
                    // Enter chat room with chatroomid and sender session id
                    if (response.code().toString().equals("200")) { startActivity(Intent(applicationContext, ChatActivity::class.java)
                        .putExtra("chatRoomId", response.body()?.roomId)
                        .putExtra("senderSessionId", response.body()?.sessionId)) }
                    else if (response.code().toString().equals("408")) { Snackbar.make(binding.root, getString(R.string.text_main_no_chat_user), Snackbar.LENGTH_LONG).show() }
                    else { Snackbar.make(binding.root, getString(R.string.text_main_enter_queue_failed), Snackbar.LENGTH_LONG).show() }
                }

                // If there's no user to chat or connection failure
                override fun onFailure(call: Call<joinQueueResponseModel>, t: Throwable) {
                    Snackbar.make(binding.root, getString(R.string.text_main_enter_queue_failed), Snackbar.LENGTH_LONG).show()
                    Log.d("Retrofit", "Failed to enter queue : "+t.message.toString())
                }

            })
        }
    }

    override fun onBackPressed() {
        // Navigation Drawer
        if(binding.layoutMainDrawer.isDrawerOpen(GravityCompat.START)){
            binding.layoutMainDrawer.closeDrawers()
        }else{
            if(System.currentTimeMillis() - backPressWaitTime >=2000 ) {
                backPressWaitTime = System.currentTimeMillis()
                Snackbar.make(binding.root,getString(R.string.text_hint_on_backpressed), Snackbar.LENGTH_LONG).show()
            } else {
                finish()
            }
        }

    }

}