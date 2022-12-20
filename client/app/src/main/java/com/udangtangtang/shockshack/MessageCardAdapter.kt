package com.udangtangtang.shockshack

import android.provider.Telephony.Sms.Sent
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews.RemoteCollectionItems
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.LinkedList

class MessageCardAdapter(private val messageList: LinkedList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val RECEIVED =0;
    val SENT=1;

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==RECEIVED){
            val view=LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_chat_opposite, viewGroup, false)

            return ReceivedMessageHolder(view)
        }else if(viewType==SENT){
            val view=LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_chat_user, viewGroup, false)

            return SentMessageHolder(view)
        }else{
            val view=LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_chat_user, viewGroup, false)
            return SentMessageHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType==RECEIVED){
            holder as ReceivedMessageHolder
            holder.message.text=messageList[position].substring(1)

        }else if(holder.itemViewType==SENT){
            holder as SentMessageHolder
            holder.message.text=messageList[position].substring(1)
        }
    }

    override fun getItemCount()=messageList.size

    override fun getItemViewType(position: Int): Int {
        if (Character.getNumericValue(messageList[position][0])==RECEIVED){
            return RECEIVED
        }else{
            return SENT
        }
    }

    private class SentMessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView

        init{
            message=view.findViewById(R.id.text_item_chat_message_user)
        }
    }

    private class ReceivedMessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView

        init{
            message=view.findViewById(R.id.text_item_chat_message_opposite)
        }
    }


}