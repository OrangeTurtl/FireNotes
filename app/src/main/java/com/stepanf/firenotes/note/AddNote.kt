package com.stepanf.firenotes.note

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.stepanf.firenotes.R

class AddNote : AppCompatActivity() {

    lateinit var fStore: FirebaseFirestore
    var fUser: FirebaseUser? = null
    lateinit var noteTitle: EditText
    lateinit var noteContent: EditText
    lateinit var progressBarSave: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        setSupportActionBar(findViewById(R.id.toolbar))

        fStore = FirebaseFirestore.getInstance()
        fUser = FirebaseAuth.getInstance().currentUser

        noteTitle = findViewById(R.id.addNoteTitle)
        noteContent = findViewById(R.id.addNoteContent)

        progressBarSave = findViewById(R.id.saveNoteProgressBar)

        findViewById<FloatingActionButton>(R.id.editNoteFloat).setOnClickListener { view ->
            val nTitle = noteTitle.text.toString()
            val nContent = noteContent.text.toString()

            if(nTitle.isBlank() or nContent.isBlank()) {
                Toast.makeText(this, "Can not save note with Empty Field!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBarSave.visibility = View.VISIBLE

            val docref: DocumentReference = fStore.collection("notes").document(fUser?.uid.toString()).collection("userNotes").document()
            val note: HashMap<String, Any> = hashMapOf()
            note.put("title", nTitle)
            note.put("content", nContent)

            docref.set(note).addOnSuccessListener {
                Toast.makeText(this, "Note added.", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }.addOnFailureListener {
                Toast.makeText(this, "Error, Try again.", Toast.LENGTH_SHORT).show()
                progressBarSave.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.close_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.close -> {
                Toast.makeText(this, "Not saved.", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}