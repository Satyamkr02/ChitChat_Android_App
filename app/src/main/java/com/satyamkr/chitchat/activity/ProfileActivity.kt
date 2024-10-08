package com.satyamkr.chitchat.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.satyamkr.chitchat.MainActivity
import com.satyamkr.chitchat.R
import com.satyamkr.chitchat.databinding.ActivityProfileBinding
import com.satyamkr.chitchat.model.UserModel
import java.util.Date

class ProfileActivity : AppCompatActivity() {

    private val binding : ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private  var selectedImg: Uri? = null
    private lateinit var alertDialog: AlertDialog
    private lateinit var username: String
    private lateinit var emailID: String

    private fun navigateToMainActivity() {
        Log.d("ProfileActivity", "Navigating to MainActivity")
        startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
        finish()
    }

    private fun navigateToLoginActivity() {
        Log.d("LoginActivity", "Navigating to LoginActivity")
        startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
        finish()
    }






    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        if (auth.currentUser == null) {
            DynamicToast.makeError(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish() // Finish the activity if the user is not logged in
            return
        }

        // Check for permission to read external storage
        checkStoragePermission()

        // Set up the image picker
        binding.editBtnCivProfileImageProfileActivity.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with image selection
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, 1)
            } else {
                // Request the permission if not granted
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }


        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Handle logout button click
//        binding.btnIvLogoutProfileActivity.setOnClickListener {
//            AuthUI.getInstance()
//                .signOut(this@ProfileActivity)
//                .addOnCompleteListener {
//                    // Redirect to LoginActivity after successful logout
//                    DynamicToast.makeSuccess(this,"Logout Successful", Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this, LoginActivity::class.java))
//                    finish()
//                }
//        }

        // Alert Dialog for Profile Update Status and Progress
        alertDialog = AlertDialog.Builder(this)
            .setTitle("Profile Update")
            .setMessage("Updating Profile...")
            .setCancelable(false)
            .setIcon(R.drawable.profile_user_icon)
            .create()
//            .setPositiveButton("Ok", null)
//            .setNegativeButton("Cancel", null)


        binding.editBtnCivProfileImageProfileActivity.setOnClickListener {

            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)


        }

        binding.btnSaveProfileActivity.setOnClickListener {

            if( binding.etEnterYourNameProfileActivity.text.toString().isEmpty() ){
                DynamicToast.makeError(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show()
            }else if( selectedImg == null ){
                DynamicToast.makeError(this, "Please Select Profile Image", Toast.LENGTH_SHORT).show()
            }else{

                alertDialog.show()
                uploadData()
                uploadDisplayName()
//                navigateToMainActivity()
                navigateToLoginActivity()
            }

        }

//        // Retrieve the email ID from the Intent  extra data passed from LoginActivity
//        val emailID = intent.getStringExtra("EMAIL_ID")
//
//        // Use the email ID in ProfileActivity (e.g., set it in a TextView)
//        binding.emailTextView.text = emailID

    }

    private fun uploadDisplayName() {
        // Assuming `auth` is initialized and points to FirebaseAuth instance
        val user = auth.currentUser
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(binding.etEnterYourNameProfileActivity.text.toString()) // Get user's entered name
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("ProfileActivity", "User profile updated with displayName")
                        // Proceed to main activity or wherever necessary
                    } else {
                        Log.e("ProfileActivity", "Failed to update profile", task.exception)
                    }
                }
        }

    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not already granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            DynamicToast.makeSuccess(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            // Permission denied
            DynamicToast.makeError(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadData() {
        Log.d("ProfileActivity", "Starting upload")
        if (selectedImg == null) {
            Log.e("ProfileActivity", "Image not selected")
            DynamicToast.makeError(this, "Image not selected", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
            return
        }

        val reference = storage.reference.child("Users Profile").child(Date().time.toString())
        reference.putFile(selectedImg!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ProfileActivity", "Image upload successful")
                    reference.downloadUrl.addOnSuccessListener { uri ->
                        uploadInfo(uri.toString())
                    }
                } else {
                    Log.e("ProfileActivity", "Failed to upload image", task.exception)
                    DynamicToast.makeError(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                }
            }
    }




    private fun uploadInfo(imgUrl: String) {
        Log.d("ProfileActivity", "Uploading user info to Firebase")
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("ProfileActivity", "User is null")
            DynamicToast.makeError(this, "User not logged in", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
            return
        }

        val user = UserModel(
            currentUser.uid,
            binding.etEnterYourNameProfileActivity.text.toString(),
            currentUser.email ?: "No email",
            imgUrl
        )

        database.reference.child("Users")
            .child(currentUser.uid)
            .setValue(user)
            .addOnSuccessListener {
                Log.d("ProfileActivity", "User info uploaded successfully")
                alertDialog.dismiss()
                DynamicToast.makeSuccess(this, "Profile Created Successfully", Toast.LENGTH_SHORT).show()
//                navigateToMainActivity()
                navigateToLoginActivity()
            }
            .addOnFailureListener {
                Log.e("ProfileActivity", "Failed to upload user info")
                DynamicToast.makeError(this, "Failed to save profile", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
    }




    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImg = data.data
            binding.civUserProfileImageProfileActivity.setImageURI(selectedImg)
        } else {
            DynamicToast.makeError(this, "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

}
