package com.lira.liraapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.lira.liraapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityRegisterBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog, will show while creating account
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle back button click, goto previous screen
        binding.backBtn.setOnClickListener {
            onBackPressed() // goto previous screen
        }

        //handle click, begin register
        binding.registerBtn.setOnClickListener {
            validateData()
        }

    }

    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cPassword = binding.cPasswordEt.text.toString().trim()

        //validate data
        if (name.isEmpty()) {
            //empty name
            Toast.makeText(this, "Enter Your Name...", Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email pattern
            Toast.makeText(this, "Invalid Email Pattern...", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()){
            //empty password
            Toast.makeText(this, "Enter Password...", Toast.LENGTH_SHORT).show()
        }
        else if (cPassword.isEmpty()){
            Toast.makeText(this, "Confirm password...", Toast.LENGTH_SHORT).show()
        }
        else if (password != cPassword){
            Toast.makeText(this, "Password doesn't match...", Toast.LENGTH_SHORT).show()
        }
        else{
            creatUserAccount()
        }
    }

    private fun creatUserAccount() {
        //Create Account - Firebase Auth
        progressDialog.setMessage("Creating Account")
        progressDialog.show()

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //create account
                updataUserInfo()
            }
            .addOnFailureListener { e->
                //failed create
                progressDialog.dismiss()
                Toast.makeText(this, "Failed creating account due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updataUserInfo() {
        //save user info
        progressDialog.setMessage("Saving user info...")

        //timestamp
        val timestamp = System.currentTimeMillis()

        //get current user uid, sicne user is registered
        val uid = firebaseAuth.uid

        //setup data to add in db
        val hasMap: HashMap<String, Any?> = HashMap()
        hasMap["uid"] = uid
        hasMap["email"] = email
        hasMap["name"] = name
        hasMap["profileImage"] = ""
        hasMap["userType"] = "user"
        hasMap["timestamp"] = timestamp

        //set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hasMap)
            .addOnSuccessListener {
                //user info saved, open user dashboard
                progressDialog.dismiss()
                Toast.makeText(this, "Account Created...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->
                //failed adding data to db
                progressDialog.dismiss()
                Toast.makeText(this, "Failed saving user info due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }
}