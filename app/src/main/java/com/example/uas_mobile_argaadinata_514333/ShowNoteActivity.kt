package com.example.uas_mobile_argaadinata_514333

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_mobile_argaadinata_514333.database.BookmarkEntity
import com.example.uas_mobile_argaadinata_514333.database.NoteEntity
import com.example.uas_mobile_argaadinata_514333.database.NoteRoomDatabase
import com.example.uas_mobile_argaadinata_514333.databinding.ActivityShowNoteBinding
import com.example.uas_mobile_argaadinata_514333.network.ApiClient
import com.example.uas_mobile_argaadinata_514333.network.UserResponse
import retrofit2.Call
import retrofit2.Response

class ShowNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowNoteBinding
    private var database: NoteRoomDatabase? = null
    private var noteId: String? = null
    private var authorId: String? = null
    private var isBookmarked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = NoteRoomDatabase.getDatabase(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Show Note"

        loadNoteData()
        setupButtons()
        checkBookmarkStatus()
    }

    private fun checkBookmarkStatus() {
        Thread {
            val bookmarks = database?.bookmarkDao()?.getAllBookmarksSync() ?: listOf()
            isBookmarked = bookmarks.any { it.apiId == noteId }

            runOnUiThread {
                updateStarIcon()
            }
        }.start()
    }

    private fun updateStarIcon() {
        binding.btnStar.setImageResource(
            if (isBookmarked) R.drawable.baseline_star_24
            else R.drawable.baseline_star_border_24
        )
    }

    private fun setupButtons() {
        binding.btnCopy.setOnClickListener {
            Thread {
                val noteEntity = NoteEntity(
                    title = binding.TextTitle.text.toString(),
                    content = binding.TextContent.text.toString()
                )
                database?.noteDao()?.insert(noteEntity)

                runOnUiThread {
                    Toast.makeText(this, "Note copied to your notes", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.start()
        }

        binding.btnStar.setOnClickListener {
            Thread {
                if (isBookmarked) {
                    database?.bookmarkDao()?.deleteByApiId(noteId ?: "")
                    isBookmarked = false
                } else {
                    val bookmarkEntity = BookmarkEntity(
                        apiId = noteId ?: "",
                        authorId = authorId ?: ""
                    )
                    database?.bookmarkDao()?.insert(bookmarkEntity)
                    isBookmarked = true
                }

                runOnUiThread {
                    updateStarIcon()
                    Toast.makeText(
                        this,
                        if (isBookmarked) "Note bookmarked" else "Bookmark removed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.start()
        }
    }

    private fun loadNoteData() {
        noteId = intent.getStringExtra("note_id")
        authorId = intent.getStringExtra("author_id")
        binding.TextTitle.text = intent.getStringExtra("title")
        binding.TextContent.text = intent.getStringExtra("content")

        authorId?.let { id ->
            ApiClient.getInstance().getAllUsers().enqueue(object : retrofit2.Callback<List<UserResponse>> {
                override fun onResponse(call: Call<List<UserResponse>>, response: Response<List<UserResponse>>) {
                    if (response.isSuccessful) {
                        response.body()?.find { it._id == id }?.let { user ->
                            binding.TextAuthor.text = "By: ${user.name}"
                        }
                    }
                }

                override fun onFailure(call: Call<List<UserResponse>>, t: Throwable) {
                    Toast.makeText(this@ShowNoteActivity, "Error loading author: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}