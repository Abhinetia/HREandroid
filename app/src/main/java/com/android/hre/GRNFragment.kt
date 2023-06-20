package com.android.hre

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.GRNAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentGRNBinding
import com.android.hre.databinding.FragmentHomeBinding
import com.android.hre.response.grn.GrnList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GRNFragment : Fragment() {

    private lateinit var binding :FragmentGRNBinding
    var userid : String = ""
    private lateinit var grnAdapter: GRNAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentGRNBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        grnAdapter = GRNAdapter()

        fetchTheGRNDetails()

        binding.ivBack.setOnClickListener {
            activity?.finish()
        }

        return root
    }

    private fun fetchTheGRNDetails() {

        val call = RetrofitClient.instance.getGRnDetails(userid)
        call.enqueue(object : Callback<GrnList> {
            override fun onResponse(call: Call<GrnList>, response: Response<GrnList>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    val dataList = apiResponse?.data
                    // Log.v("dat",dataList.toString())

                    if (dataList.isNullOrEmpty()) {
                        // The list is empty
                        binding.tvShowPening.visibility = View.VISIBLE
                        binding.tvShowPening.text = "No Active GRN Available"
                    } else {
                        // The list is not empty
                        grnAdapter.differ.submitList(dataList)
                        Log.v("dat", grnAdapter.differ.submitList(dataList).toString())

                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = grnAdapter
                        }
                    }


                }
            }

            override fun onFailure(call: Call<GrnList>, t: Throwable) {
                // handle error
            }
        })

    }


}