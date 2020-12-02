package com.stepanf.firenotes.note

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.stepanf.firenotes.R

class NoteDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data: Intent = intent

        val content: TextView = findViewById(R.id.noteDetailsContent)
        content.movementMethod = ScrollingMovementMethod()
        val title: TextView = findViewById(R.id.noteDetailsTitle)

        content.text = data.getStringExtra("content")
        title.text = data.getStringExtra("title")
        val colorCode: Int = data.getIntExtra("colorCode", 0)
        content.setBackgroundColor(ContextCompat.getColor(this, colorCode))


        findViewById<FloatingActionButton>(R.id.editNoteFloat).setOnClickListener { view ->
            val i: Intent = Intent(view.context, EditNote::class.java)
            i.putExtra("title", data.getStringExtra("title"))
            i.putExtra("content", data.getStringExtra("content"))
            i.putExtra("noteId", data.getStringExtra("noteId"))
            startActivity(i)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}