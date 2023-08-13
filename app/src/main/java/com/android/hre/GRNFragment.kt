package com.android.hre

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.GRNAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentGRNBinding
import com.android.hre.databinding.FragmentHomeBinding
import com.android.hre.response.getappdata.AppDetails
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

        if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
            fetchtheappData()
        }


        fetchTheGRNDetails()

        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
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

    private fun fetchtheappData() {
        val call = RetrofitClient.instance.getappData(userid)
        call.enqueue(object : Callback<AppDetails> {
            override fun onResponse(call: Call<AppDetails>, response: Response<AppDetails>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    Log.v("dat", dataList.toString())

                    val version = getAppVersion(context!!)
                    println("App version: $version")

                    if (!dataList!!.need_update.equals("No")){
                        showAlertDialogOkAndCloseAfter("Please Use the latest Application of ARCHIVE")
                        return
                    }
                    if(!dataList!!.app_version.equals(version)){
                        showAlertDialogOkAndCloseAfter("Please Use the latest Application of ARCHIVE")
                        return
                    }

                    if (dataList!!.isloggedin.equals("true")){
                        // openDashboard()
                    } else {
                        openDataLogin()
                        /*showAlertDialogOkAndCloseAfter("Please contact your Super Admin for more information")
                        return*/
                    }

                    val isLoggedIn = dataList!!.isloggedin
                    val appVersion = dataList!!.app_version
                    val needUpdate = dataList!!.need_update

                } else  {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<AppDetails>, t: Throwable) {
                // Handle network error
            }
        })
    }
    fun openDashboard(){
        var intent =  Intent(context, MainActivity::class.java)
        startActivity(intent)
    }

    fun openDataLogin(){
        var intent =  Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showAlertDialogOkAndCloseAfter(alertMessage: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->  }  // LoginActivity::class.java
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }
    fun getAppVersion(context: Context): String {
        try {
            val packageName = context.packageName
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "Unknown"
    }



}