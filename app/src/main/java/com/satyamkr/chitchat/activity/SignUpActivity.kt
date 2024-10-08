package com.satyamkr.chitchat.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.satyamkr.chitchat.R
import com.satyamkr.chitchat.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private val binding : ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    private fun navigateToProfileActivity() {
        startActivity(Intent(this@SignUpActivity, ProfileActivity::class.java))
        finish()
    }

    private fun navigateToLoginActivity() {
        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        finish()
    }

//    private fun navigateToProfile(emailID: String) {
//        val intent = Intent(this@SignUpActivity, ProfileActivity::class.java)
//        intent.putExtra("EMAIL_ID", emailID)
//        startActivity(intent)
//        finish()
//    }

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

//        binding = ActivitySignUpBinding.inflate(layoutInflater)

        // Initialize Firebase Auth instance
        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.ivBackSignUpActivity.setOnClickListener {
            navigateToLoginActivity() // Navigate back to the login screen
        }

        binding.tvLoginSignUpActivity.setOnClickListener {
            navigateToLoginActivity() // Navigate to the login screen
        }

        binding.btnContinueSignUpActivity.setOnClickListener {

            // Get the entered username, email, password, and confirm password values from the corresponding EditText views in the layout.
            val username = binding.etUsernameSignUpActivity.text.toString()
            val emailID = binding.etEmailSignUpActivity.text.toString()
            val password = binding.etPasswordSignUpActivity.text.toString()
            val confirmPassword = binding.etConfirmPasswordSignUpActivity.text.toString()

            // Check if the entered username, email, password, and confirm password values are blank or not.
            if (username.isEmpty() || emailID.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                DynamicToast.makeWarning(this@SignUpActivity, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }else if ( password != confirmPassword) {
                DynamicToast.makeWarning(this@SignUpActivity, "Password and confirm password do not match", Toast.LENGTH_SHORT).show()
            }else{
                auth.createUserWithEmailAndPassword( emailID , password )
                    .addOnCompleteListener(this) { task ->
                        if ( task.isSuccessful){
                            DynamicToast.makeSuccess(this@SignUpActivity, "Account created successfully", Toast.LENGTH_SHORT).show()
                            navigateToProfileActivity() // Navigate to the login screen after successful account creation

                        }else{
                            DynamicToast.makeError(this@SignUpActivity, "Account creation failed :\n${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

            }




        }



    }
}