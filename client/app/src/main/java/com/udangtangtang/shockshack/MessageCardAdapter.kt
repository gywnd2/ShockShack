package com.udangtangtang.shockshack

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.LinkedList

class MessageCardAdapter(private val messageList: LinkedList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val chatStartDate : String=SimpleDateFormat("yyyy년 MM월 dd일").format(Date(System.currentTimeMillis()))
    val RECEIVED =0;
    val SENT=1;

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==RECEIVED){
            val view=LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_chat_opponent, viewGroup, false)

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
        val nowDate : String= SimpleDateFormat("yyyy년 MM월 dd일").format(Date(System.currentTimeMillis()))

        if(holder.itemViewType==RECEIVED){
            holder as ReceivedMessageHolder
            holder.message.text=messageList[position].substring(1)
            // Show message date only if date changed
            if(chatStartDate.equals(nowDate)){
                if (messageList.size==1){
                    holder.date.text=nowDate
                    holder.date.visibility=View.VISIBLE
                }else{
                    holder.date.visibility=View.INVISIBLE
                }
            }else{
                holder.date.text=nowDate
                holder.date.visibility=View.VISIBLE
            }

        }else if(holder.itemViewType==SENT){
            holder as SentMessageHolder
            holder.message.text=messageList[position].substring(1)
            // Show message date only if date changed
            if(chatStartDate.equals(nowDate)){
                if (messageList.size==1){
                    holder.date.text=nowDate
                    holder.date.visibility=View.VISIBLE
                }else{
                    holder.date.text=nowDate
                    holder.date.visibility=View.INVISIBLE
                }
            }else{
                holder.date.text=nowDate
                holder.date.visibility=View.VISIBLE
            }
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
        val date : TextView


        init{
            message=view.findViewById(R.id.text_item_chat_message_user)
            date=view.findViewById(R.id.text_item_chat_date_user)
        }
    }

    private class ReceivedMessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView
        val date : TextView

        init{
            message=view.findViewById(R.id.text_item_chat_message_opponent)
            date=view.findViewById(R.id.text_item_chat_date_opponent)
        }
    }


}