package com.stepanf.firenotes

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.stepanf.firenotes.auth.Login
import com.stepanf.firenotes.auth.Register
import com.stepanf.firenotes.auth.Splash
import com.stepanf.firenotes.model.Note
import com.stepanf.firenotes.note.AddNote
import com.stepanf.firenotes.note.EditNote
import com.stepanf.firenotes.note.NoteDetails
import kotlin.random.Random

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {
    lateinit var drawerLayout: DrawerLayout
    lateinit var nav_view: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var noteLists: RecyclerView
    lateinit var fStore: FirebaseFirestore
    lateinit var fAuth: FirebaseAuth
    lateinit var noteAdapter: FirestoreRecyclerAdapter<Note, NoteViewHolder>
    lateinit var query: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fStore = FirebaseFirestore.getInstance()
        fAuth = FirebaseAuth.getInstance()

        query = fStore.collection("notes").document(fAuth.currentUser?.uid.toString()).collection("userNotes")
        val allNotes = FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note::class.java)
                .build()

        noteAdapter = NoteFirestoreRecyclerAdapter(allNotes)

        noteLists = findViewById(R.id.notelist)

        drawerLayout = findViewById(R.id.drawer)
        nav_view = findViewById(R.id.nav_view)
        nav_view.setNavigationItemSelectedListener(this)
        toolbar = findViewById(R.id.toolbar)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.setDrawerIndicatorEnabled(true)
        toggle.syncState()

        val titles = arrayListOf<String>()
        val content = arrayListOf<String>()

        noteLists.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        noteLists.adapter = noteAdapter

        val headerView: View = nav_view.getHeaderView(0)
        val userName: TextView = headerView.findViewById(R.id.userDisplayName)
        val userEmail: TextView = headerView.findViewById(R.id.userDisplayEmail)
        val currentUser = fAuth.currentUser
        if (currentUser != null)
            if(!currentUser.isAnonymous) {
                userName.text = fAuth.currentUser?.displayName
                userEmail.text = fAuth.currentUser?.email
            }
            else {
                userName.text = "You are not logged in"
                userEmail.visibility = View.GONE
            }

        val addNoteFloat = findViewById<FloatingActionButton>(R.id.addNoteFloat)
        addNoteFloat.setOnClickListener {
            view ->
                startActivity(Intent(view.context, AddNote().javaClass))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addNote ->
                startActivity(Intent(this, AddNote().javaClass))
            R.id.sync-> {
                val currentUser = fAuth.currentUser
                if (currentUser != null)
                    if(currentUser.isAnonymous)
                        startActivity(Intent(this, Login().javaClass))
                    else
                        Toast.makeText(this, "You are connected.", Toast.LENGTH_SHORT).show()
            }
            R.id.logout -> {
                checkUser()
            }
            else ->
                Toast.makeText(this, "Coming soon.", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun checkUser() {
        val currentUser = fAuth.currentUser
        if (currentUser != null) {
            if(currentUser.isAnonymous) {
                displayAlert(LogoutWarningTypes.USER_IS_ANONYMOUS)
            }
            else {
                fAuth.signOut()
                startActivity(Intent(applicationContext, Splash::class.java))
                finish()
            }
        }
        else
            displayAlert(LogoutWarningTypes.USER_IS_NULL)
    }

    private fun displayAlert(warningType: LogoutWarningTypes) {
        when (warningType) {
            LogoutWarningTypes.USER_IS_ANONYMOUS -> {
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Are you sure?")
                        .setMessage("You are logged in with Temporary Account. Logging out will Delete All the notes!")
                        .setPositiveButton("Sync Note") {
                            dialog, which ->
                            startActivity(Intent(applicationContext, Register::class.java))
                            finish()
                        }.setNegativeButton("Logout anyway") {
                            dialog, which ->
                            //TODO: delete all the notes created by the Amon user
                            //TODO: delete the anon user
                            val fUser = fAuth.currentUser
                            fUser?.delete()?.addOnSuccessListener {
                                startActivity(Intent(applicationContext, Splash::class.java))
                                finish()
                            }
                        }.show()
            }
            LogoutWarningTypes.USER_IS_NULL -> {
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Error")
                        .setMessage("Restart application and try again.")
                        .show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.settings -> Toast.makeText(this, "Setting menu is clicked.", Toast.LENGTH_SHORT)
        }
        return super.onOptionsItemSelected(item)
    }

    private enum class LogoutWarningTypes {USER_IS_NULL, USER_IS_ANONYMOUS}

    public inner class NoteViewHolder internal constructor(public val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var noteTitle: TextView
        lateinit var noteContent: TextView
        lateinit var mCardView: CardView
        init {
            noteTitle = itemView.findViewById(R.id.titles)
            noteContent = itemView.findViewById(R.id.content)
            mCardView = itemView.findViewById(R.id.cardView)
        }
    }

    private inner class NoteFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Note>) :
            FirestoreRecyclerAdapter<Note, NoteViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.note_view, parent, false)
            return NoteViewHolder(view)
        }

        override fun onBindViewHolder(holder: NoteViewHolder, i: Int, note: Note) {
            holder.noteTitle.setText(note.title)
            holder.noteContent.setText(note.content)
            val colorCode: Int = getRandomColor()
            holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(holder.view.context, colorCode))
            val docId: String = noteAdapter.snapshots.getSnapshot(i).id

            holder.view.setOnClickListener {
                view ->
                val i: Intent = Intent(view.context, NoteDetails().javaClass)
                i.putExtra("title", note.title)
                i.putExtra("content", note.content)
                i.putExtra("colorCode", colorCode)
                i.putExtra("noteId", docId)
                view.context.startActivity(i)
            }

            val optionsIcon = holder.view.findViewById<ImageView>(R.id.optionsIcon)
            optionsIcon.setOnClickListener { view ->
                val popup: PopupMenu = PopupMenu(view.context, view)
                popup.menu.add("Edit").setOnMenuItemClickListener { item: MenuItem? ->
                    val i: Intent = Intent(view.context, EditNote::class.java)
                    i.putExtra("title", note.title)
                    i.putExtra("content", note.content)
                    i.putExtra("noteId", docId)
                    startActivity(i)
                    true
                }
                popup.menu.add("Delete").setOnMenuItemClickListener { item: MenuItem? ->
                    val docRef: DocumentReference = query.document(docId)
                    docRef.delete().addOnSuccessListener {
                        // note Deleted
                    }.addOnFailureListener {
                        Toast.makeText(this@MainActivity, "Error! Try again.", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                popup.show()
            }
        }
    }

    private fun getRandomColor(): Int {
        val colorIds = arrayOf<Int>(
                R.color.blue, R.color.yellow, R.color.lightGreen,
                R.color.pink, R.color.lightPurple, R.color.skyblue,
                R.color.gray, R.color.red, R.color.greenlight, R.color.notgreen
        )
        val randomIndex: Int = Random.nextInt(colorIds.size)
        return colorIds[randomIndex]
    }

    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        noteAdapter.stopListening()
    }
}


