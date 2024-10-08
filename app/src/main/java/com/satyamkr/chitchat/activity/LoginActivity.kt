package com.satyamkr.chitchat.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.satyamkr.chitchat.MainActivity
import com.satyamkr.chitchat.R
import com.satyamkr.chitchat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val binding : ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth

    // Check if the user is already authenticated
    override fun onStart() {
        super.onStart()

        // Check if the user is already authenticated
        val currentUser : FirebaseUser? = auth.currentUser
        if( currentUser != null ){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java) )
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding = ActivityLoginBinding.inflate(layoutInflater)

        // Initialize Firebase Auth instance
        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvRegisterLoginActivity.setOnClickListener{
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java) )
            finish()
        }

        binding.btnLoginLoginActivity.setOnClickListener {

            val emailID = binding.etEmailLoginActivity.text.toString()
            val password = binding.etPasswordLoginActivity.text.toString()

            if( emailID.isEmpty() || password.isEmpty() ){
                DynamicToast.makeWarning(this,"Please fill all the fields",Toast.LENGTH_SHORT).show()
            }else{
                auth.signInWithEmailAndPassword( emailID , password )
                    .addOnCompleteListener { task ->
                        if( task.isSuccessful ) {
                            DynamicToast.makeSuccess(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }else {
                            val exception = task.exception
                            Log.e("LoginActivity", "Login Failed: ${exception?.message}", exception)
                            when (exception) {
                                is FirebaseAuthInvalidUserException -> {
                                    DynamicToast.makeError(this, "This email is not registered. Please sign up.", Toast.LENGTH_SHORT).show()
                                }
                                is FirebaseAuthInvalidCredentialsException -> {
                                    DynamicToast.makeError(this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    DynamicToast.makeError(this, "Login Failed: ${exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }



                    }
            }

        }




    }
}