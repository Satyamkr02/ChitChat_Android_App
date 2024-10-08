package com.satyamkr.chitchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.satyamkr.chitchat.R
import com.satyamkr.chitchat.databinding.ReceiveMessageItemLayoutBinding
import com.satyamkr.chitchat.databinding.SendMessageItemLayoutBinding
import com.satyamkr.chitchat.model.MessageModel

class MessageAdapter( var context: Context , var list : ArrayList<MessageModel> ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var Item_Send = 1
    var Item_Receive = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if( viewType == Item_Send )
            SenderViewHolder(LayoutInflater.from(context).inflate(R.layout.send_message_item_layout,parent,false))
            else
            ReceiverViewHolder(LayoutInflater.from(context).inflate(R.layout.receive_message_item_layout, parent, false))

    }

    override fun getItemViewType(position: Int): Int {
        return if( FirebaseAuth.getInstance().uid == list[position].senderId ) Item_Send else Item_Receive
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val message = list[position]
        if ( holder.itemViewType == Item_Send ){
            val viewHolder = holder as SenderViewHolder
            viewHolder.binding.tvSentMessage.text = message.message
        }else{
            val viewHolder = holder as ReceiverViewHolder
            viewHolder.binding.tvReceivedMessage.text = message.message
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SendMessageItemLayoutBinding.bind(itemView)
    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ReceiveMessageItemLayoutBinding.bind(itemView)
    }


}