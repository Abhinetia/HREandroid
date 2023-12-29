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
import com.android.hre.adapter.AttendanceDataAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentVaultBinding
import com.android.hre.databinding.FragmentVaultMainBinding
import com.android.hre.response.attendncelist.AttendanceListData
import com.android.hre.response.newvault.NewVaultDetiailsMainFolder
import com.android.hre.response.vaults.VaultDetails
import com.google.gson.JsonObject
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Response


class VaultMainFragment : Fragment() {

    private lateinit var binding : FragmentVaultMainBinding
    var userid :String = ""
    val folderListData: ArrayList<NewVaultDetiailsMainFolder.Data> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_vault_main, container, false)

        binding = FragmentVaultMainBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!


        vaultInfoDatails()
        return root
    }


    private fun vaultInfoDatails() {
        folderListData.clear()
        RetrofitClient.instance.newVault(userid)
            .enqueue(object: retrofit2.Callback<NewVaultDetiailsMainFolder> {
                override fun onFailure(call: Call<NewVaultDetiailsMainFolder>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<NewVaultDetiailsMainFolder>, response: Response<NewVaultDetiailsMainFolder>) {
                    Log.v("Sucess", response.body().toString())
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        val pettyCashBills = apiResponse?.data!!.folders


                        for (i in 0 until pettyCashBills.size){

                            val datafolder = pettyCashBills.get(i)
                            Toast.makeText(context,"$datafolder",Toast.LENGTH_SHORT).show()
                            Log.v("Folders","$datafolder")

                           // folderListData.add(datafolder.)

                        }




//
                    }

                }

            })


    }


}