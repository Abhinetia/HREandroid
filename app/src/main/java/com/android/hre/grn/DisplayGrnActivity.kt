package com.android.hre.grn

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.Constants
import com.android.hre.R
import com.android.hre.adapter.GRNAdapter
import com.android.hre.adapter.SearchMaterialIndentAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityDisplayGrnBinding
import com.android.hre.response.Getmaterials
import com.android.hre.response.grn.GrnList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DisplayGrnActivity : AppCompatActivity() {

    private lateinit var binding :ActivityDisplayGrnBinding
    var userid : String = ""
    private lateinit var grnAdapter: GRNAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityDisplayGrnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        grnAdapter = GRNAdapter()

        fetchTheGRNDetails()

    }

    private fun fetchTheGRNDetails() {

                val call = RetrofitClient.instance.getGRnDetails(userid)
                call.enqueue(object : Callback<GrnList> {
                    override fun onResponse(call: Call<GrnList>, response: Response<GrnList>) {
                        if (response.isSuccessful) {
                            val apiResponse = response.body()
                            val dataList = apiResponse?.data
                           // Log.v("dat",dataList.toString())
                            grnAdapter.differ.submitList(dataList)
                            Log.v("dat", grnAdapter.differ.submitList(dataList).toString())

                            binding.rvRecylergrndata.apply {
                                    layoutManager = LinearLayoutManager(this@DisplayGrnActivity)
                                    adapter = grnAdapter
                                }
                        }
                    }

                    override fun onFailure(call: Call<GrnList>, t: Throwable) {
                        // handle error
                    }
                })

    }

}