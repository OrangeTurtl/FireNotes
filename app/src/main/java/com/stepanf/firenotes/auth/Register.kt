package com.stepanf.firenotes.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.stepanf.firenotes.MainActivity
import com.stepanf.firenotes.R

class Register : AppCompatActivity() {
    lateinit var rUserName: EditText
    lateinit var rUserEmail: EditText
    lateinit var rUserPass: EditText
    lateinit var rUserConfPass: EditText
    lateinit var btnSyncAccount: Button
    lateinit var loginAct: TextView
    lateinit var progressBar: ProgressBar

    lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        fAuth = FirebaseAuth.getInstance()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Connect to FireNotes")

        rUserName = findViewById(R.id.userName)
        rUserEmail = findViewById(R.id.userEmail)
        rUserPass = findViewById(R.id.password)
        rUserConfPass = findViewById(R.id.passwordConfirm)

        btnSyncAccount = findViewById(R.id.createAccount)
        loginAct = findViewById(R.id.login)
        progressBar = findViewById(R.id.progressBar4)

        loginAct.setOnClickListener {
            startActivity(Intent(applicationContext, Login::class.java))
        }

        btnSyncAccount.setOnClickListener { v: View ->
            val uUserName = rUserName.text.toString()
            val uUserEmail = rUserEmail.text.toString()
            val uUserPass = rUserPass.text.toString()
            val uUserConfPass = rUserConfPass.text.toString()

            if(uUserName.isBlank() || uUserEmail.isBlank() || uUserPass.isBlank() || uUserConfPass.isBlank()) {
                Toast.makeText(this, "All field are required!", Toast.LENGTH_SHORT).show()
                cleanPasswordField()
                return@setOnClickListener
            }

            if(!uUserPass.equals(uUserConfPass)) {
                rUserConfPass.setError("Passwords don't match!")
                cleanPasswordField()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            val credential: AuthCredential = EmailAuthProvider.getCredential(uUserEmail, uUserPass)
            val currentUser = fAuth.currentUser
            if (currentUser != null) {
                currentUser.linkWithCredential(credential).addOnSuccessListener {
                    val request: UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                        .setDisplayName(uUserName)
                        .build()
                    currentUser.updateProfile(request).addOnCompleteListener {
                        Toast.makeText(this, "Notes are synced.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(applicationContext, MainActivity()::class.java))
                        finish()
                    }

                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to Connect. Try again.", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }
            else {
                Toast.makeText(this, "Restart the application and try again.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun cleanPasswordField() {
        rUserPass.text.clear()
        rUserConfPass.text.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(applicationContext, MainActivity()::class.java))
        finish()
        return super.onOptionsItemSelected(item)
    }
}