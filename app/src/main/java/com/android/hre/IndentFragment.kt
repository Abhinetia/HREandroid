package com.android.hre

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.HomeAdapter
import com.android.hre.adapter.HomeAdapterNew
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentHomeBinding
import com.android.hre.databinding.FragmentIndentBinding
import com.android.hre.response.homeindents.GetIndentsHome
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class IndentFragment : Fragment() {

    private var _binding: FragmentIndentBinding? = null

    private val binding get() = _binding!!
    var userid : String = ""
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var homeAdapterNew: HomeAdapterNew
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentIndentBinding.inflate(inflater, container, false)
        val root: View = binding.root




        sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
        var name = sharedPreferences?.getString("username", "")
        userid = sharedPreferences?.getString("user_id", "")!!
        homeAdapter = HomeAdapter()
        // homeAdapterNew = HomeAdapterNew()


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
                    if (dataList.isNullOrEmpty()) {
                        // The list is empty
                        binding.tvShowPening.visibility = View.VISIBLE
                        binding.tvShowPening.text = "No Pending Indent has Been Created"
                    } else {
                        // The list is not empty
                        homeAdapter.differ.submitList(dataList.reversed())

                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = homeAdapter
                        }
                    }
//                    homeAdapter.differ.submitList(dataList?.reversed())
//
//                    binding.rvRecylergrndata.apply {
//                        layoutManager = LinearLayoutManager(context)
//                        adapter = homeAdapter
//                    }
//
//                    // var dataList = dataList?.reversed()
////                        homeAdapterNew = HomeAdapterNew(context!!,dataList?.reversed())
////                    binding.rvRecylergrndata.adapter = homeAdapterNew
//
//                } else if (response.isSuccessful) {
//                    val indentResponse = response.body()
//                    val dataList = indentResponse?.data
//                    if (dataList!!.isEmpty()){
//                        // Texxtview visible
//                    }
//                    // Handle error response
                }
            }

            override fun onFailure(call: Call<GetIndentsHome>, t: Throwable) {
                // Handle network error
            }
        })

    }


}