package com.android.hre

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.HomeAdapter
import com.android.hre.adapter.HomeAdapterNew
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentHomeBinding
import com.android.hre.databinding.FragmentIndentBinding
import com.android.hre.response.getappdata.AppDetails
import com.android.hre.response.homeindents.GetIndentsHome
import com.android.hre.response.newindentrepo.NewIndents
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
    var activeList :ArrayList<NewIndents.Myindent> = arrayListOf()
    var completedList :ArrayList<NewIndents.Myindent> = arrayListOf()
    var isActive : Boolean = true

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

        if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
            fetchtheappData()
        }

        fetchTheIndentList()

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()

        }
        binding.tvActive.setOnClickListener {
            if(!isActive){
                isActive = true
                homeAdapter.differ.submitList(activeList.reversed())
            }
            if(activeList.size == 0){
                binding.tvShowPening.text = "No Active intend"
                binding.tvShowPening.visibility = View.VISIBLE
            }else{
                binding.tvShowPening.text = ""
                binding.tvShowPening.visibility = View.GONE
            }

        }
        binding.tvComplted.setOnClickListener {
            if(isActive){
                isActive = false
                homeAdapter.differ.submitList(completedList.reversed())
            }
            if(completedList.size == 0){
                binding.tvShowPening.text = "No Completed intend"
                binding.tvShowPening.visibility = View.VISIBLE
            }else{
                binding.tvShowPening.text = ""
                binding.tvShowPening.visibility = View.GONE
            }
        }


        return root
    }

    private fun fetchTheIndentList() {
        val call = RetrofitClient.instance.getIndents(userid)
        call.enqueue(object : Callback<NewIndents> {
            override fun onResponse(call: Call<NewIndents>, response: Response<NewIndents>) {
//                    val indentResponse = response.body()
//                    val dataList = indentResponse?.data
//                    Log.v("dat", dataList.toString())
                  /*  if (dataList.isNullOrEmpty()) {
                        // The list is empty
                        binding.tvShowPening.visibility = View.VISIBLE
                        binding.tvShowPening.text = "No Pending Indent has Been Created"
                    } else {
                        // The list is not empty
                        homeAdapter.differ.submitList(dataList.reversed())
                        binding.countof.text = dataList?.size.toString()
                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = homeAdapter
                        }
                    }*/

                    if (response.isSuccessful) {
                        val indentResponse = response.body()

                        if (indentResponse != null && indentResponse.status == 1) {
                            val myIndents = indentResponse.data.myindents

                            for (i in 0 until myIndents.size ){
                                val myindent = myIndents.get(i)
                                if(myindent.status.equals("Active")){
                                    activeList.add(myindent)
                                }else{
                                    completedList.add(myindent)
                                }
                            }
                            homeAdapter.differ.submitList(activeList.reversed())


                            binding.countof.text = indentResponse.data.counts.Active.toString()
                            binding.countcompl.text = indentResponse.data.counts.Completed.toString()

                            binding.rvRecylergrndata.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = homeAdapter
                            }

                        }
                        else {
                            binding.tvShowPening.visibility = View.VISIBLE
                            binding.tvShowPening.text = "No Pending Indents"
                            // Handle error or unexpected response
                        }
                    } else {
                        // Handle API call failure
                    }
                 /*   homeAdapter.differ.submitList(dataList?.reversed())

                    binding.rvRecylergrndata.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = homeAdapter
                    }

                    // var dataList = dataList?.reversed()
//                        homeAdapterNew = HomeAdapterNew(context!!,dataList?.reversed())
//                    binding.rvRecylergrndata.adapter = homeAdapterNew

                } else if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    if (dataList!!.isEmpty()){
                        // Texxtview visible
                    }
                    // Handle error response
*/

            }

            override fun onFailure(call: Call<NewIndents>, t: Throwable) {
                // Handle network error
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