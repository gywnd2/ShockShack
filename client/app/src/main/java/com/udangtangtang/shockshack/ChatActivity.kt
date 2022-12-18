package com.udangtangtang.shockshack

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.udangtangtang.shockshack.databinding.ActivityChatBinding
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class ChatActivity : AppCompatActivity() {
    private lateinit var binding:ActivityChatBinding
    private lateinit var stompClient:StompClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
    }

    @SuppressLint("CheckResult")
    fun runStomp(){
        stompClient=Stomp.over(Stomp.ConnectionProvider.OKHTTP, BuildConfig.SERVER_ADDRESS)
        stompClient.connect()

        stompClient.topic("/chat-websocket").subscribe(){ topicMessage ->
            Toast.makeText(applicationContext, topicMessage.payload, Toast.LENGTH_SHORT).show()
        }

        stompClient.lifecycle().subscribe(){ lifecycleEvent ->
            when(lifecycleEvent.type){
                LifecycleEvent.Type.OPENED -> {

                }
                LifecycleEvent.Type.CLOSED -> {

                }
                LifecycleEvent.Type.ERROR -> {

                }
                else -> {

                }
            }
        }

        stompClient.send("/topic/채팅방 아이디", 채팅 내용.toString()).subscribe()

    }


}