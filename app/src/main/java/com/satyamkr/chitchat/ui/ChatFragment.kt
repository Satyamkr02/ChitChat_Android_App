package com.satyamkr.chitchat.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.satyamkr.chitchat.R
import com.satyamkr.chitchat.adapter.ChatAdapter
import com.satyamkr.chitchat.databinding.FragmentChatBinding
import com.satyamkr.chitchat.model.UserModel

class ChatFragment : Fragment() {

    private val binding : FragmentChatBinding by lazy {
        FragmentChatBinding.inflate(layoutInflater)
    }

    private var database : FirebaseDatabase? = null
    lateinit var userList : ArrayList<UserModel>
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        database = FirebaseDatabase.getInstance()
        userList = ArrayList()

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = binding.swipeRefreshLayout

        // Set refresh listener
        swipeRefreshLayout.setOnRefreshListener {
            refreshChatList() // Method to reload chat data
        }

        // Initial load of data
        loadChatList()

        return binding.root
    }

    private fun loadChatList() {
        database!!.reference.child("Users")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (snapshot1 in snapshot.children) {
                        val user = snapshot1.getValue(UserModel::class.java)
                        if (user!!.uid != FirebaseAuth.getInstance().uid) {
                            userList.add(user)
                        }
                    }
                    binding.UserListRecyclerViewChatFragment.adapter = ChatAdapter(requireContext(), userList)
                    binding.progressBarLoading.visibility = View.GONE
                    binding.UserListRecyclerViewChatFragment.visibility = View.VISIBLE
                    swipeRefreshLayout.isRefreshing = false // Stop refreshing after data is loaded
                }

                override fun onCancelled(error: DatabaseError) {
                    swipeRefreshLayout.isRefreshing = false
                }
            })
    }

    private fun refreshChatList() {
        // Reload data (this can be the same logic as your initial data load)
        loadChatList()
    }
}
