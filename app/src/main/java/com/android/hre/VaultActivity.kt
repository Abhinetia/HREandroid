package com.android.hre

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.TransInfoAdapter
import com.android.hre.adapter.VaultAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityTranscationInfoBinding
import com.android.hre.databinding.ActivityVaultBinding
import com.android.hre.response.transcationinfo.TranscationInfoDetails
import com.android.hre.response.vaults.VaultDetails
import retrofit2.Call
import retrofit2.Response

class VaultActivity : AppCompatActivity() {

    private lateinit var binding:ActivityVaultBinding
    var userid :String = ""
    private lateinit var vaultAdapter: VaultAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        //setContentView(R.layout.activity_vault)

        binding = ActivityVaultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!

        vaultAdapter = VaultAdapter()
        vaultInfoDatails()

    }

    private fun vaultInfoDatails() {

        RetrofitClient.instance.getVault(userid)
            .enqueue(object: retrofit2.Callback<VaultDetails> {
                override fun onFailure(call: Call<VaultDetails>, t: Throwable) {
                    Toast.makeText(this@VaultActivity, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<VaultDetails>, response: Response<VaultDetails>) {
                    Log.v("Sucess", response.body().toString())
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        val pettyCashBills = apiResponse?.data

                        if (pettyCashBills != null) {
                            // Set up the adapter and RecyclerView
                            vaultAdapter.differ.submitList(pettyCashBills)

                            binding.rvRecylergrndata.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = vaultAdapter
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