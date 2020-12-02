package com.stepanf.firenotes.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stepanf.firenotes.MainActivity
import com.stepanf.firenotes.R

class Login : AppCompatActivity() {
    lateinit var lEmail: EditText
    lateinit var lPassword: EditText

    lateinit var forgotPass: TextView
    lateinit var createAccount: TextView

    lateinit var loginNow: Button

    lateinit var fAuth: FirebaseAuth
    lateinit var fStore: FirebaseFirestore

    lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Login to FireNotes")

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        lEmail = findViewById(R.id.lEmail)
        lPassword = findViewById(R.id.lPassword)
        loginNow = findViewById(R.id.loginBtn)

        forgotPass = findViewById(R.id.forgotPasword)
        createAccount = findViewById(R.id.createAccount)
        spinner = findViewById(R.id.progressBar3)

        createAccount.setOnClickListener {
            startActivity(Intent(applicationContext, Register()::class.java))
        }

        val currentUser = fAuth.currentUser
        if(currentUser != null)
            if(currentUser.isAnonymous)
                showWarning()

        loginNow.setOnClickListener {
            val mEmail = lEmail.text.toString()
            val mPassword = lPassword.text.toString()

            if (mEmail.isBlank() || mPassword.isBlank()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            spinner.visibility = View.VISIBLE

            if (currentUser != null) {
                if (currentUser.isAnonymous) {
                    fStore.collection("notes").document(currentUser.uid.toString()).delete().addOnSuccessListener {
                        currentUser.delete().addOnSuccessListener {
                            fAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnSuccessListener {
                                Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(applicationContext, Splash()::class.java))
                                finish()
                            }.addOnFailureListener { e ->
                                Toast.makeText(this, "Login failed. " + e.message.toString(), Toast.LENGTH_SHORT).show()
                            }.addOnCompleteListener {
                                spinner.visibility = View.GONE
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, "Sorry, try again.", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Sorry, try again.", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, Splash()::class.java))
                    finish()
                }
            }
            else
                Toast.makeText(this, "Restart the application and try again.", Toast.LENGTH_SHORT).show()

        }
    }

    private fun showWarning() {
        AlertDialog.Builder(this@Login)
                .setTitle("Are you sure?")
                .setMessage("Linking existing account will delete all the temp notes! Create new account to save them?")
                .setPositiveButton("Create account") { dialog, which ->
                    startActivity(Intent(applicationContext, Register::class.java))
                    finish()
                }
                .setNegativeButton("No, thanks") { dialog, which ->
                    //do nothing
                }.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(applicationContext, MainActivity()::class.java))
        finish()
        return super.onOptionsItemSelected(item)
    }
}