package com.android.hre.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.hre.Constants
import com.android.hre.R
import com.android.hre.databinding.FragmentHomeBinding
import com.android.hre.storage.SharedPrefManager


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!


    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        val sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        var name = sharedPreferences?.getString("username", "")
        binding.tvDisplay.text = name

        Log.v("Sharedpref", sharedPreferences?.getBoolean(Constants.ISLOGGEDIN,false).toString())


        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivnotificatn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_ticketFragment)
        }

        binding.logo.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_indentFragment2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}