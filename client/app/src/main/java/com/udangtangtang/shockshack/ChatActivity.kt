package com.udangtangtang.shockshack

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.udangtangtang.shockshack.databinding.ActivityChatBinding
import io.reactivex.Observable
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.LinkedList
import java.util.Objects

class ChatActivity : AppCompatActivity() {
    private lateinit var binding:ActivityChatBinding
    private lateinit var stompClient:StompClient
    private lateinit var chatRoomId : String
    private lateinit var senderSessionId : String
    private lateinit var inputManager : InputMethodManager
    private var messageList =LinkedList<String>()

    @SuppressLint("CheckResult", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set title
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title=getString(R.string.text_chat_title)

        // Set message card layout
        binding.textChatMainview.layoutManager=LinearLayoutManager(this)
        binding.textChatMainview.adapter=MessageCardAdapter(messageList)

        // Scroll recyclerview to end when keyboard shows
        binding.textChatMainview.addOnLayoutChangeListener { view, _, _, _, bottom, _, _, _, oldBottom ->
            if(bottom<oldBottom){
                binding.textChatMainview.smoothScrollToPosition((binding.textChatMainview.adapter as MessageCardAdapter).itemCount)
            }
        }

        // Hide keyboard
        inputManager= getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        binding.inputTextChatSendMessage.setOnEditorActionListener { textView, action, event ->
            var handled=false

            if(action==EditorInfo.IME_ACTION_DONE){
                inputManager.hideSoftInputFromWindow(binding.inputTextChatSendMessage.windowToken, 0)
                // Scroll recyclerview to end
                binding.textChatMainview.smoothScrollToPosition((binding.textChatMainview.adapter as MessageCardAdapter).itemCount)
                handled=true
            }

            handled
        }

        // If failed to receive chatroomid
        if(!intent.hasExtra("chatRoomId")){
            Snackbar.make(binding.root, getString(R.string.text_chat_chatroomid_receive_failed), Snackbar.LENGTH_LONG).show()
        }else{
            chatRoomId=intent.getStringExtra("chatRoomId").toString()

            senderSessionId=intent.getStringExtra("senderSessionId").toString()
        }

        // Start receive message
        runStomp()

        // Send button action
        binding.buttonChatSend.setOnClickListener {
            if (!binding.inputTextChatSendMessage.text.isEmpty()){
                //Add user input to message list
                messageList.add("1"+binding.inputTextChatSendMessage.text)

                Log.d("Retrofit", "Send message : /topic/"+chatRoomId+" with message : "+binding.inputTextChatSendMessage.text.toString())
                // Create message data json object
                val sendData=JSONObject()
                sendData.put("senderSessionId", senderSessionId)
                sendData.put("message", binding.inputTextChatSendMessage.text.toString())
                sendData.put("messageType", "CHAT")

                stompClient.send("/"+chatRoomId, sendData.toString()).doOnError {
                    Log.d("StompLog", it.message.toString())
                }.subscribe()

                // Update recyclerview and clear input text view
                binding.textChatMainview.adapter?.notifyItemInserted((binding.textChatMainview.adapter as MessageCardAdapter).itemCount)
                binding.inputTextChatSendMessage.text.clear()
                // Scroll recyclerview to end
                binding.textChatMainview.smoothScrollToPosition((binding.textChatMainview.adapter as MessageCardAdapter).itemCount)
//                inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            }
        }

    }

    class ChatRoomExitDialogFragment : DialogFragment(){
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let{
                val builder= AlertDialog.Builder(it)
                builder.setMessage("정말 나가시겠습니까?")
                    .setPositiveButton("예", DialogInterface.OnClickListener{dialog, id ->
                        activity?.finish()
                    })
                    .setNegativeButton("아니오", DialogInterface.OnClickListener{dialog, id ->
                        dialog.dismiss()
                    })
                builder.create()
            }?:throw java.lang.IllegalStateException("Activity cannot be null")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stompClient.disconnect()
    }

    override fun onBackPressed() {
        // Ask user to quit
        val quitDialog=ChatRoomExitDialogFragment()
        quitDialog.show(supportFragmentManager, "quit")
    }


    @SuppressLint("CheckResult")
    fun runStomp(){
        stompClient=Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://"+BuildConfig.SERVER_IP+"/chat-websocket")
        val headerList= arrayListOf<StompHeader>()
        headerList.add(StompHeader("chatRoomId", chatRoomId))
        stompClient.connect(headerList)

        stompClient.topic("/topic/"+chatRoomId).doOnError{
                Log.d("StompLog", it.message.toString())
            }.subscribe{ topicMessage ->
            val receiveData=JSONObject(topicMessage.payload)
            // If senderSessionId of received data -> message of opponent
            if (!receiveData.get("senderSessionId").equals(senderSessionId) && receiveData.get("messageType").equals("CHAT")) {
                // Update recyclerview
                runOnUiThread{
                    binding.textChatMainview.adapter?.notifyItemInserted((binding.textChatMainview.adapter as MessageCardAdapter).itemCount)
                    binding.textChatMainview.smoothScrollToPosition((binding.textChatMainview.adapter as MessageCardAdapter).itemCount)
                }
                messageList.add("0" + receiveData.get("message").toString())
                Log.d("StompLog", messageList.toString())
            }
            else if (receiveData.get("messageType").equals("DISCONNECTED")){
                // Add disconnected message and notify
                // scroll to end
                messageList.add("2")
                runOnUiThread {
                    binding.textChatMainview.adapter?.notifyItemInserted((binding.textChatMainview.adapter as MessageCardAdapter).itemCount)
                    binding.textChatMainview.smoothScrollToPosition((binding.textChatMainview.adapter as MessageCardAdapter).itemCount)
//                    binding.textChatMainview.addOnLayoutChangeListener { view, _, _, _, bottom, _, _, _, oldBottom ->
//                        if(bottom<oldBottom){
//                            binding.textChatMainview.smoothScrollToPosition((binding.textChatMainview.adapter as MessageCardAdapter).itemCount)
//                        }
//                    }
                    binding.buttonChatSend.isEnabled=false
                }
            }
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
    }


}