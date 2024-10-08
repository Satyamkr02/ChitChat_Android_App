package com.satyamkr.chitchat.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.satyamkr.chitchat.R
import com.satyamkr.chitchat.adapter.MessageAdapter
import com.satyamkr.chitchat.databinding.ActivityChatBinding
import com.satyamkr.chitchat.model.MessageModel
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private val binding: ActivityChatBinding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }

    private lateinit var database: FirebaseDatabase

    private lateinit var senderUid: String
    private lateinit var receiverUid: String

    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String

    private lateinit var list: ArrayList<MessageModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Get the sender and receiver UIDs
        senderUid = FirebaseAuth.getInstance().uid.toString()
        receiverUid = intent.getStringExtra("uid")!!

        // Define senderRoom and receiverRoom based on UIDs
        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

        database = FirebaseDatabase.getInstance()

        // Set up RecyclerView with a LinearLayoutManager to display messages vertically
        binding.rvChatsChatActivity.layoutManager = LinearLayoutManager(this)

        // Initialize message list
        list = ArrayList()

        // Set up listener for send button
        binding.ivSendMessageChatActivity.setOnClickListener {
            val messageText = binding.etMessageBoxChatActivity.text.toString()
            if (messageText.isEmpty()) {
                // Show a warning toast if the message is empty
                DynamicToast.makeWarning(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            } else {
                // Create a new message model
                val message = MessageModel(messageText, senderUid, Date().time)

                // Generate a unique key for the message
                val randomKey = database.reference.push().key

                // Add the message to the sender's chat node in Firebase
                database.reference.child("chats")
                    .child(senderRoom).child("message").child(randomKey!!).setValue(message)
                    .addOnSuccessListener {
                        // Add the message to the receiver's chat node in Firebase
                        database.reference.child("chats").child(receiverRoom).child("message")
                            .child(randomKey!!).setValue(message).addOnSuccessListener {
                                // Scroll to the last position to display the latest message
                                binding.rvChatsChatActivity.smoothScrollToPosition(list.size - 1)

                                // Clear the message input field after sending
                                binding.etMessageBoxChatActivity.text.clear()

                                // Show success toast
                                DynamicToast.makeSuccess(this, "Message Sent", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
            }
        }

        // Load existing chat messages from Firebase
        loadChatMessages()

        // Handle keyboard adjustment for RecyclerView
        handleKeyboardVisibility()
    }

    /**
     * Loads chat messages from Firebase and updates the RecyclerView.
     */
    private fun loadChatMessages() {
        database.reference.child("chats").child(senderRoom).child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Clear the list before adding new data
                    list.clear()

                    // Add each message from the snapshot to the list
                    for (snapshot1 in snapshot.children) {
                        val data = snapshot1.getValue(MessageModel::class.java)
                        list.add(data!!)
                    }

                    // Set adapter to display the messages
                    val adapter = MessageAdapter(this@ChatActivity, list)
                    binding.rvChatsChatActivity.adapter = adapter

                    // Scroll to the last message if there are any
                    if (list.size > 0) {
                        binding.rvChatsChatActivity.post {
                            binding.rvChatsChatActivity.smoothScrollToPosition(list.size - 1)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error by showing an error toast
                    DynamicToast.makeError(this@ChatActivity, "Error: $error", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    /**
     * Adjusts the RecyclerView when the keyboard is shown, so that the chat input moves above the keyboard.
     */
    private fun handleKeyboardVisibility() {
        // Listen for window insets to detect when the keyboard is opened
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            if (imeVisible) {
                // If keyboard is visible, scroll to the last message
                if (list.size > 0) {
                    binding.rvChatsChatActivity.smoothScrollToPosition(list.size - 1)
                }
            }

            // Set padding to avoid overlap with system bars
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
