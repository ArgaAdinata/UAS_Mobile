package com.example.uas_mobile_argaadinata_514333

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_mobile_argaadinata_514333.database.NoteEntity
import com.example.uas_mobile_argaadinata_514333.database.NoteRoomDatabase
import com.example.uas_mobile_argaadinata_514333.databinding.ActivityEditNoteBinding
import com.example.uas_mobile_argaadinata_514333.network.ApiClient
import com.example.uas_mobile_argaadinata_514333.network.MessageRes
import com.example.uas_mobile_argaadinata_514333.network.NoteRequest
import com.example.uas_mobile_argaadinata_514333.network.NoteResponse
import retrofit2.Call
import retrofit2.Response

class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private var database: NoteRoomDatabase? = null
    private var noteId: Int = 0
    private var apiId: String? = null
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        database = NoteRoomDatabase.getDatabase(this)

        noteId = intent.getIntExtra("note_id", 0)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (noteId == 0) "Add Note" else "Edit Note"

        Thread {
            database?.noteDao()?.getNoteById(noteId)?.let { note ->
                apiId = note.apiId
            }
        }.start()

        if (noteId != 0) {
            Thread {
                database?.noteDao()?.getNoteById(noteId)?.let { note ->
                    runOnUiThread {
                        binding.editTextTitle.setText(note.title)
                        binding.editTextContent.setText(note.content)
                    }
                }
            }.start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_note_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_save -> {
                saveNote()
                true
            }
            R.id.action_delete -> {
                deleteNote()
                true
            }
            R.id.action_upload -> {
                uploadNote()
                true
            }
            R.id.action_copy -> {
                copyIdNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveNote() {
        val title = binding.editTextTitle.text.toString()
        val content = binding.editTextContent.text.toString()

        if (title.isNotEmpty() && content.isNotEmpty()) {
            Thread {
                val existingNote = database?.noteDao()?.getNoteById(noteId)

                val note = NoteEntity(
                    id = noteId,
                    apiId = existingNote?.apiId,
                    title = title,
                    content = content
                )

                if (noteId == 0) {
                    database?.noteDao()?.insert(note)
                } else {
                    database?.noteDao()?.update(note)
                }

                runOnUiThread {
                    Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.start()
        } else {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteNote() {
        if (noteId != 0) {
            Thread {
                database?.noteDao()?.getNoteById(noteId)?.let { note ->
                    if (note.apiId != null) {
                        ApiClient.getInstance().deleteNote(note.apiId).enqueue(object : retrofit2.Callback<MessageRes> {
                            override fun onResponse(call: Call<MessageRes>, response: Response<MessageRes>) {
                                runOnUiThread {
                                    if (response.isSuccessful) {
                                        Thread {
                                            database?.noteDao()?.delete(note)
                                            runOnUiThread {
                                                Toast.makeText(this@EditNoteActivity, "Note deleted", Toast.LENGTH_SHORT).show()
                                                finish()
                                            }
                                        }.start()
                                    } else {
                                        Toast.makeText(this@EditNoteActivity, "API deletion failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MessageRes>, t: Throwable) {
                                runOnUiThread {
                                    Toast.makeText(this@EditNoteActivity, "Deletion failed: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    } else {
                        database?.noteDao()?.delete(note)
                        runOnUiThread {
                            Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }.start()
        } else {
            Toast.makeText(this, "Cannot delete a new note", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadNote() {
        prefManager = PrefManager.getInstance(this)

        val userId = prefManager.getId()
        if (userId.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            return
        }

        val title = binding.editTextTitle.text.toString()
        val content = binding.editTextContent.text.toString()

        val request = NoteRequest(userId, title, content)

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            val existingNote = database?.noteDao()?.getNoteById(noteId)

            if (existingNote != null && existingNote.apiId != null) {
                ApiClient.getInstance().updateNote(existingNote.apiId, request).enqueue(object : retrofit2.Callback<NoteResponse> {
                    override fun onResponse(call: Call<NoteResponse>, response: Response<NoteResponse>) {
                        runOnUiThread {
                            if (response.isSuccessful) {
                                Thread {
                                    val updatedNote = NoteEntity(
                                        id = existingNote.id,
                                        apiId = existingNote.apiId,
                                        title = title,
                                        content = content
                                    )
                                    database?.noteDao()?.update(updatedNote)
                                }.start()

                                Toast.makeText(this@EditNoteActivity, "Note updated", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@EditNoteActivity, "Update failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<NoteResponse>, t: Throwable) {
                        runOnUiThread {
                            Toast.makeText(this@EditNoteActivity, "Update failed: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                ApiClient.getInstance().addNote(request).enqueue(object : retrofit2.Callback<NoteResponse> {
                    override fun onResponse(call: Call<NoteResponse>, response: Response<NoteResponse>) {
                        runOnUiThread {
                            if (response.isSuccessful) {
                                val apiId = response.body()?._id

                                ApiClient.getInstance().getAllNotes().enqueue(object : retrofit2.Callback<List<NoteResponse>> {
                                    override fun onResponse(call: Call<List<NoteResponse>>, getAllResponse: Response<List<NoteResponse>>) {
                                        if (getAllResponse.isSuccessful) {
                                            val matchingNote = getAllResponse.body()?.find { note ->
                                                note.title == title &&
                                                        note.content == content &&
                                                        note.userId == userId
                                            }

                                            val finalApiId = matchingNote?._id ?: apiId

                                            Thread {
                                                val noteToSave = NoteEntity(
                                                    id = existingNote?.id ?: 0,
                                                    apiId = finalApiId,
                                                    title = title,
                                                    content = content
                                                )

                                                if (noteId == 0) {
                                                    database?.noteDao()?.insert(noteToSave)
                                                } else {
                                                    database?.noteDao()?.update(noteToSave)
                                                }
                                            }.start()

                                            Toast.makeText(this@EditNoteActivity, "Note uploaded", Toast.LENGTH_SHORT).show()
                                            finish()
                                        } else {
                                            Toast.makeText(this@EditNoteActivity, "Failed to retrieve notes", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<List<NoteResponse>>, t: Throwable) {
                                        Toast.makeText(this@EditNoteActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            } else {
                                Toast.makeText(this@EditNoteActivity, "Upload failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<NoteResponse>, t: Throwable) {
                        runOnUiThread {
                            Toast.makeText(this@EditNoteActivity, "Upload failed: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }.start()
    }

    private fun copyIdNote() {
        if (apiId != null) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("API ID", apiId)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "API ID copied", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "API ID not available", Toast.LENGTH_SHORT).show()
        }
    }
}