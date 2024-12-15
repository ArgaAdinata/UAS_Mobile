package com.example.uas_mobile_argaadinata_514333

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_mobile_argaadinata_514333.database.NoteEntity
import com.example.uas_mobile_argaadinata_514333.databinding.ItemNoteBinding

class NoteAdapter(
    private val listNote: List<NoteEntity>,
    private val onClickNote: (NoteEntity) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: NoteEntity) {
            with(binding) {
                noteTitleTextView.text = data.title
                noteContentTextView.text = data.content

                itemView.setOnClickListener {
                    onClickNote(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NoteViewHolder(binding)
    }

    override fun getItemCount(): Int = listNote.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(listNote[position])
    }
}