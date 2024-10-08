package com.satyamkr.chitchat.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satyamkr.chitchat.R
import com.satyamkr.chitchat.activity.ChatActivity
import com.satyamkr.chitchat.databinding.UserChatItemLayoutBinding
import com.satyamkr.chitchat.model.UserModel

class ChatAdapter(var context: Context, private var list: ArrayList<UserModel>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = UserChatItemLayoutBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ChatViewHolder {
        return ChatViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.user_chat_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        val user = list[position]
        Glide.with(context).load(user.imageUrl).into(holder.binding.UserProfileImageChatItem)
        holder.binding.UserNameChatItem.text = user.name

        holder.itemView.setOnClickListener{

            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("uid", user.uid)
            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


}