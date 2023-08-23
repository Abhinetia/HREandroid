package com.android.hre

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.VaultAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityVaultBinding
import com.android.hre.databinding.FragmentVaultBinding
import com.android.hre.databinding.FragmentViewIndentBinding
import com.android.hre.response.vaults.VaultDetails
import retrofit2.Call
import retrofit2.Response


class VaultFragment : Fragment() {

    private lateinit var binding:FragmentVaultBinding
    var userid :String = ""
    private lateinit var vaultAdapter: VaultAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
     //   return inflater.inflate(R.layout.fragment_vault, container, false)

        binding = FragmentVaultBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!

        vaultAdapter = VaultAdapter()
        vaultInfoDatails()

        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        return root
    }

    private fun vaultInfoDatails() {

        RetrofitClient.instance.getVault(userid)
            .enqueue(object: retrofit2.Callback<VaultDetails> {
                override fun onFailure(call: Call<VaultDetails>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
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