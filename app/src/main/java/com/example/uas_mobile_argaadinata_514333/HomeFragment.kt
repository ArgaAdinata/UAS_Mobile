package com.example.uas_mobile_argaadinata_514333

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas_mobile_argaadinata_514333.database.NoteRoomDatabase
import com.example.uas_mobile_argaadinata_514333.databinding.FragmentHomeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var database: NoteRoomDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = NoteRoomDatabase.getDatabase(requireContext())

        setupRecyclerView()
        setupAddButton()
    }

    private fun setupRecyclerView() {
        binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())

        database?.noteDao()?.getAllNotes()?.observe(viewLifecycleOwner) { notes ->
            val adapter = NoteAdapter(
                listNote = notes,
                onClickNote = { note ->
                    val intent = Intent(requireContext(), EditNoteActivity::class.java)
                    intent.putExtra("note_id", note.id)
                    startActivity(intent)
                }
            )
            binding.rvNotes.adapter = adapter
        }
    }

    private fun setupAddButton() {
        binding.btnAddNote.setOnClickListener {
            startActivity(Intent(requireContext(), EditNoteActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}