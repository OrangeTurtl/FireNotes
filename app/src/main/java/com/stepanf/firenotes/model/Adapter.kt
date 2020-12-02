package com.stepanf.firenotes.model

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.stepanf.firenotes.note.NoteDetails
import com.stepanf.firenotes.R
import kotlin.random.Random

class Adapter (val titles: ArrayList<String>, val content: ArrayList<String>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.noteTitle.setText(titles.get(position))
        holder.noteContent.setText(content.get(position))
        val colorCode: Int = getRandomColor()
        holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(holder.view.context, colorCode))

        holder.view.setOnClickListener {
            view ->
                val i: Intent = Intent(view.context, NoteDetails().javaClass)
                i.putExtra("title", titles[position])
                i.putExtra("content", content[position])
                i.putExtra("colorCode", colorCode)
                view.context.startActivity(i)
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

    override fun getItemCount(): Int {
        return titles.size
    }

    public class ViewHolder : RecyclerView.ViewHolder {
        lateinit var noteTitle: TextView
        lateinit var noteContent: TextView
        lateinit var view: View
        lateinit var mCardView: CardView
        constructor(itemView: View) : super(itemView) {
            noteTitle = itemView.findViewById(R.id.titles)
            noteContent = itemView.findViewById(R.id.content)
            mCardView = itemView.findViewById(R.id.cardView)
            view = itemView
        }
    }
}