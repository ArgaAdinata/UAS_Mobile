package com.example.uas_mobile_argaadinata_514333

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas_mobile_argaadinata_514333.database.BookmarkEntity
import com.example.uas_mobile_argaadinata_514333.database.NoteRoomDatabase
import com.example.uas_mobile_argaadinata_514333.databinding.FragmentShareBinding
import com.example.uas_mobile_argaadinata_514333.network.ApiClient
import com.example.uas_mobile_argaadinata_514333.network.NoteResponse
import com.example.uas_mobile_argaadinata_514333.network.UserResponse
import retrofit2.Call
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShareFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ShareFragment : Fragment() {
    private var _binding: FragmentShareBinding? = null
    private val binding get() = _binding!!
    private var database: NoteRoomDatabase? = null
    private val loadedNotes = mutableListOf<Triple<String, NoteResponse, String>>()
    private var adapter: ShareNoteAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = NoteRoomDatabase.getDatabase(requireContext())
        setupSearch()
        setupRecyclerView()
        observeBookmarks()
    }

    private fun setupSearch() {
        binding.btnSearch.setOnClickListener {
            val noteId = binding.etSearch.text.toString()
            if (noteId.isNotEmpty()) {
                searchNote(noteId)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvSearchHistory.layoutManager = LinearLayoutManager(requireContext())
        adapter = ShareNoteAdapter(
            notes = loadedNotes,
            onItemClick = { note ->
                val intent = Intent(requireContext(), ShowNoteActivity::class.java)
                intent.putExtra("note_id", note.second._id)
                intent.putExtra("title", note.second.title)
                intent.putExtra("content", note.second.content)
                intent.putExtra("author_id", note.second.userId)
                startActivity(intent)
            }
        )
        binding.rvSearchHistory.adapter = adapter
    }

    private fun observeBookmarks() {
        database?.bookmarkDao()?.getAllBookmarks()?.observe(viewLifecycleOwner) { bookmarks ->
            loadedNotes.clear()
            if (bookmarks.isNotEmpty()) {
                loadBookmarkedNotes(bookmarks)
            } else {
                adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun loadBookmarkedNotes(bookmarks: List<BookmarkEntity>) {
        bookmarks.forEach { bookmark ->
            ApiClient.getInstance().getNoteById(bookmark.apiId).enqueue(object : retrofit2.Callback<NoteResponse> {
                override fun onResponse(call: Call<NoteResponse>, response: Response<NoteResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val note = response.body()!!
                        loadAuthorForNote(note, bookmark.apiId)
                    }
                }

                override fun onFailure(call: Call<NoteResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error loading note: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loadAuthorForNote(note: NoteResponse, bookmarkId: String) {
        ApiClient.getInstance().getUserById(note.userId).enqueue(object : retrofit2.Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val author = response.body()!!
                    loadedNotes.add(Triple(author.name, note, bookmarkId))
                    adapter?.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error loading author: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchNote(noteId: String) {
        ApiClient.getInstance().getNoteById(noteId).enqueue(object : retrofit2.Callback<NoteResponse> {
            override fun onResponse(call: Call<NoteResponse>, response: Response<NoteResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { note ->
                        val intent = Intent(requireContext(), ShowNoteActivity::class.java)
                        intent.putExtra("note_id", note._id)
                        intent.putExtra("title", note.title)
                        intent.putExtra("content", note.content)
                        intent.putExtra("author_id", note.userId)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(requireContext(), "Note not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NoteResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
