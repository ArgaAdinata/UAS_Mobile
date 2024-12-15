package com.example.uas_mobile_argaadinata_514333

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.uas_mobile_argaadinata_514333.databinding.FragmentProfileBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val prefManager = PrefManager.getInstance(requireContext())

        val username = prefManager.getUsername()

        if (username.isEmpty()) {
            binding.apply {
                profileContainer.visibility = View.GONE
                loginButton.visibility = View.VISIBLE
            }

            binding.loginButton.setOnClickListener {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
            }
        } else {
            val name = prefManager.getName()
            val address = prefManager.getAddress()
            val phone = prefManager.getPhone()

            binding.apply {
                profileContainer.visibility = View.VISIBLE
                loginButton.visibility = View.GONE

                nameTextView.text = name
                usernameTextView.text = username
                addressTextView.text = address
                phoneTextView.text = phone
            }

            binding.logoutButton.setOnClickListener {
                prefManager.clear()

                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}