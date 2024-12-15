package com.example.uas_mobile_argaadinata_514333

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_mobile_argaadinata_514333.databinding.ItemShareNoteBinding
import com.example.uas_mobile_argaadinata_514333.network.NoteResponse

class ShareNoteAdapter(
    private val notes: List<Triple<String, NoteResponse, String>>,
    private val onItemClick: (Triple<String, NoteResponse, String>) -> Unit
) : RecyclerView.Adapter<ShareNoteAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemShareNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Triple<String, NoteResponse, String>, onItemClick: (Triple<String, NoteResponse, String>) -> Unit) {
            binding.apply {
                textTitle.text = note.second.title
                textContent.text = note.second.content
                textAuthor.text = "By: ${note.first}"

                root.setOnClickListener { onItemClick(note) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShareNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notes[position], onItemClick)
    }

    override fun getItemCount() = notes.size
}