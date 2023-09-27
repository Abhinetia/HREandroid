package com.android.hre

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.StatementAdapter
import com.android.hre.adapter.TransInfoAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityTranscationInfoBinding
import com.android.hre.response.transcationinfo.TranscationInfoDetails
import retrofit2.Call
import retrofit2.Response

class TranscationInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTranscationInfoBinding
    var userid :String = ""
    private lateinit var transInfoAdapter: TransInfoAdapter
    var issuedCash :String = ""
    var billaceepted :String = ""
    var billamount :String = ""





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = ActivityTranscationInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!


        transInfoAdapter= TransInfoAdapter()

        pettytransInfoDatails()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


        binding.tvIssuedamount.text= issuedCash
        binding.tvBillaccpeted.text = billaceepted
        binding.tvBalncanmount.text = billamount


    }

    private fun pettytransInfoDatails() {

        RetrofitClient.instance.getTranscationInfo(userid)
            .enqueue(object: retrofit2.Callback<TranscationInfoDetails> {
                override fun onFailure(call: Call<TranscationInfoDetails>, t: Throwable) {
                    Toast.makeText(this@TranscationInfoActivity, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<TranscationInfoDetails>, response: Response<TranscationInfoDetails>) {
                    Log.v("Sucess", response.body().toString())
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        val pettyCashBills = apiResponse?.data

                        if (pettyCashBills != null) {
                            // Set up the adapter and RecyclerView
                            transInfoAdapter.differ.submitList(pettyCashBills)

                            binding.rvRecylergrndata.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = transInfoAdapter
                            }

                        } else {
                            // Handle error response
                        }
                    } else {
                        // Handle the error
                    }

            }

            })


    }

}