package com.android.hre

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.HomeAdapter
import com.android.hre.adapter.HomeAdapterNew
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityIndentBinding
import com.android.hre.databinding.ActivityViewTicketBinding
import com.android.hre.response.getappdata.AppDetails
import com.android.hre.response.newindentrepo.NewIndents
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IndentActivity : AppCompatActivity() {

    private lateinit var binding : ActivityIndentBinding

    var userid : String = ""
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var homeAdapterNew: HomeAdapterNew
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var activeList :ArrayList<NewIndents.Myindent> = arrayListOf()
    var completedList :ArrayList<NewIndents.Myindent> = arrayListOf()
    var isActive : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
       // setContentView(R.layout.activity_indent)

        binding = ActivityIndentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
        var name = sharedPreferences?.getString("username", "")
        userid = sharedPreferences?.getString("user_id", "")!!
        homeAdapter = HomeAdapter()

        if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
            fetchtheappData()
        }

        fetchTheIndentList()

        binding.ivBack.setOnClickListener {
           onBackPressed()

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

    }
    private fun fetchTheIndentList() {
        val call = RetrofitClient.instance.getIndents(userid)
        call.enqueue(object : Callback<NewIndents> {
            override fun onResponse(call: Call<NewIndents>, response: Response<NewIndents>) {
//

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

                    val version = getAppVersion(this@IndentActivity)
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
        var intent =  Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun openDataLogin(){
        var intent =  Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showAlertDialogOkAndCloseAfter(alertMessage: String) {
        val builder = AlertDialog.Builder(this)
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