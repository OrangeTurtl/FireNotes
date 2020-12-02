package com.stepanf.firenotes.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.stepanf.firenotes.MainActivity
import com.stepanf.firenotes.R

class Splash : AppCompatActivity() {
    lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        fAuth = FirebaseAuth.getInstance()

        val handler: Handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if(fAuth.currentUser != null) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
            else {
                fAuth.signInAnonymously().addOnSuccessListener {
                    Toast.makeText(this, "Logged in with temporary account.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    e ->
                        Log.e("Application", e.message.toString())
                        finish()
                }
            }
        }, 2000)
    }
}