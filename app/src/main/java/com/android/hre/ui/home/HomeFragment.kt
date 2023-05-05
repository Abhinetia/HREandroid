package com.android.hre.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.Constants
import com.android.hre.R
import com.android.hre.adapter.GRNAdapter
import com.android.hre.adapter.HomeAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentHomeBinding
import com.android.hre.grn.DisplayGrnActivity
import com.android.hre.response.homeindents.GetIndentsHome
import com.android.hre.storage.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    var userid : String = ""
    private lateinit var homeAdapter: HomeAdapter


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
        userid = sharedPreferences?.getString("user_id", "")!!
        binding.tvDisplay.text = name

        Log.v("Sharedpref", sharedPreferences?.getBoolean(Constants.ISLOGGEDIN,false).toString())

        homeAdapter = HomeAdapter()
        fetchTheIndentList()


        return root
    }

    private fun fetchTheIndentList() {
        val call = RetrofitClient.instance.getIndents(userid)
        call.enqueue(object : Callback<GetIndentsHome> {
            override fun onResponse(call: Call<GetIndentsHome>, response: Response<GetIndentsHome>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    Log.v("dat", dataList.toString())

                    homeAdapter.differ.submitList(dataList)

                    binding.rvRecylergrndata.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = homeAdapter
                    }

                } else if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    if (dataList!!.isEmpty()){
                         // Texxtview visible
                    }
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<GetIndentsHome>, t: Throwable) {
                // Handle network error
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivnotificatn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_ticketFragment)
        }

        binding.logo.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_indentFragment2)
        }

        binding.tvGrn.setOnClickListener {
            val Intent = Intent(context, DisplayGrnActivity::class.java)
            startActivity(Intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}