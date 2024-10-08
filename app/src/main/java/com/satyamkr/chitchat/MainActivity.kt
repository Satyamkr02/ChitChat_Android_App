package com.satyamkr.chitchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.satyamkr.chitchat.activity.LoginActivity
import com.satyamkr.chitchat.adapter.ViewPagerAdapter
import com.satyamkr.chitchat.databinding.ActivityMainBinding
import com.satyamkr.chitchat.ui.CallFragment
import com.satyamkr.chitchat.ui.ChatFragment
import com.satyamkr.chitchat.ui.StatusFragment

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var auth : FirebaseAuth

    // Check if the user is already authenticated
    override fun onStart() {
        super.onStart()

        // Check if the user is already authenticated
        val currentUser : FirebaseUser? = auth.currentUser
        if( currentUser == null ){
            startActivity(Intent(this@MainActivity, LoginActivity::class.java) )
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Ensure Firebase is initialized
        auth = FirebaseAuth.getInstance()

        if( auth.currentUser == null ){ // if user is not logged in then go to login activity
            startActivity( Intent(this, LoginActivity::class.java ) )
            finish()
            return
        }

//        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val currentUser = auth.currentUser
        if (currentUser != null) {
            val currentUserName = currentUser.displayName
            if (!currentUserName.isNullOrEmpty()) {
                binding.tvUserNameMainActivity.text = currentUserName
                binding.tvUserNameMainActivity.visibility = View.VISIBLE
                Log.d("MainActivity", "Current user name: $currentUserName")
            } else {
                Log.d("MainActivity", "Display name is not set")
            }
        } else {
            Log.e("MainActivity", "User is not logged in")
        }



        try {
            val fragmentArrayList = ArrayList<Fragment>()
            fragmentArrayList.add(ChatFragment())
            fragmentArrayList.add(StatusFragment())
            fragmentArrayList.add(CallFragment())

            val adapter = ViewPagerAdapter(this, supportFragmentManager, fragmentArrayList)
            binding.viewPagerMainActivity.adapter = adapter
            binding.tabLayoutMainActivity.setupWithViewPager(binding.viewPagerMainActivity)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing ViewPager or adapter", e)
        }


        // Handle logout button click
        binding.btnIvLogoutMainActivity.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    // Redirect to LoginActivity after successful logout
                    DynamicToast.makeSuccess(this,"Logout Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
        }

//        binding.btnIvProfileIconMainActivity.setOnClickListener {
//
//            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
//
//        }

    }
}