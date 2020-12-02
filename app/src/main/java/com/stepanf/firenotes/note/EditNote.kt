package com.stepanf.firenotes.note

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.stepanf.firenotes.MainActivity
import com.stepanf.firenotes.R

class EditNote : AppCompatActivity() {

    lateinit var data: Intent
    lateinit var title: EditText
    lateinit var content: EditText
    lateinit var fStore: FirebaseFirestore
    var fUser: FirebaseUser? = null
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)
        setSupportActionBar(findViewById(R.id.toolbar))

        val toolbar: Toolbar = findViewById(R.id.editNoteToolbar)
        setSupportActionBar(toolbar)

        progressBar = findViewById(R.id.editNoteProgressBar)

        fStore = FirebaseFirestore.getInstance()
        fUser = FirebaseAuth.getInstance().currentUser

        data = intent
        val noteTitle = data.getStringExtra("title")
        val noteContent = data.getStringExtra("content")
        title = findViewById(R.id.editNoteTitle)
        content = findViewById(R.id.editNoteContent)

        title.setText(noteTitle)
        content.setText(noteContent)

        findViewById<FloatingActionButton>(R.id.editNoteFloat).setOnClickListener { view ->
            val nTitle = title.text.toString()
            val nContent = content.text.toString()

            if(nTitle.isBlank() or nContent.isBlank()) {
                Toast.makeText(this, "Can not save note with Empty Field!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            val docref: DocumentReference = fStore.collection("notes").document(fUser?.uid.toString()).collection("userNotes").document(data.getStringExtra("noteId").toString())
            val note: HashMap<String, Any> = hashMapOf()
            note.put("title", nTitle)
            note.put("content", nContent)

            docref.update(note).addOnSuccessListener {
                Toast.makeText(this, "Note added.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }.addOnFailureListener {
                Toast.makeText(this, "Error, Try again.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.INVISIBLE
            }
        }
    }
}