package com.udangtangtang.shockshack

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.udangtangtang.shockshack.databinding.ActivityChatBinding
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.LinkedList

class ChatActivity : AppCompatActivity() {
    private lateinit var binding:ActivityChatBinding
    private lateinit var stompClient:StompClient
    private lateinit var chatRoomId : String
    private lateinit var senderSessionId : String
    private var messageList =LinkedList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set title
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title=getString(R.string.text_chat_title)

        // Set message card layout
        binding.textChatMainview.layoutManager=LinearLayoutManager(this)
        messageList.add("1hello")
        messageList.add("0thank you")
        binding.textChatMainview.adapter=MessageCardAdapter(messageList)

        // If failed to receive chatroomid
        if(!intent.hasExtra("chatRoomId")){
            Snackbar.make(binding.root, getString(R.string.text_chat_chatroomid_receive_failed), Snackbar.LENGTH_LONG).show()
        }else{
            chatRoomId=intent.getStringExtra("chatRoomId").toString()
            senderSessionId=intent.getStringExtra("senderSessionId").toString()
        }

        // Send button action
        binding.buttonChatSend.setOnClickListener {
            runStomp()

            // show message

        }

    }

    @SuppressLint("CheckResult")
    fun runStomp(){
        stompClient=Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://"+BuildConfig.SERVER_IP+"/chat-websocket")
        val headerList= arrayListOf<StompHeader>()
        headerList.add(StompHeader("chatRoomId", chatRoomId))
        stompClient.connect(headerList)

        stompClient.topic("/topic/"+chatRoomId).subscribe(){ topicMessage ->
            Log.d("StompLog", topicMessage.payload)
        }

        stompClient.lifecycle().subscribe(){ lifecycleEvent ->
            when(lifecycleEvent.type){
                LifecycleEvent.Type.OPENED -> {
                    Log.d("StompLog", "connection opened")
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.d("StompLog", "connection closed")
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.d("StompLog", "connection error occured")
                }
                else -> {
                    Log.d("Stomp", "unknown")
                }
            }

        }

        Log.d("Retrofit", "Send message : /topic/"+chatRoomId+" with message : "+binding.inputTextChatSendMessage.text.toString())
        // Create message data json object
        val data=JSONObject()
        data.put("senderSessionId", senderSessionId)
        data.put("message", binding.inputTextChatSendMessage.text.toString())
        data.put("messageType", "CHAT")

        stompClient.send("/"+chatRoomId, data.toString()).subscribe()
    }


}