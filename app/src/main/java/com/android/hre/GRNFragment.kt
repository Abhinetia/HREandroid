package com.android.hre

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.GRNAdapter
import com.android.hre.adapter.GRNAdapter2
import com.android.hre.adapter.HomeAdapter2
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
    private lateinit var grnAdapter2: GRNAdapter2

    private lateinit var handler: Handler
    private var timerRunnable : Runnable? = null
    private val delayMillis = 1500L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentGRNBinding.inflate(inflater, container, false)
        val root: View = binding.root


        handler = Handler(Looper.getMainLooper())

        val sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        grnAdapter = GRNAdapter()



//        if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
//            fetchtheappData()
//        }


        fetchTheGRNDetails()

        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.ivsearch.setOnClickListener {
            binding.carviewseacrh.visibility = View.VISIBLE
        }

        binding.etsearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().length == 0){
                    binding.ivProgressBar.visibility = View.GONE
                }else{
                    binding.ivProgressBar.visibility = View.VISIBLE
                }
                timerRunnable?.let { handler.removeCallbacks(it) }

                // Schedule a new timerRunnable with the specified delay
                timerRunnable = Runnable {

                    fetchTheGRNSearchList(s.toString())
//                    }else if(s.toString().length == 0){
//                        fetchTheIndentList()
//                    }
                }

                handler.postDelayed(timerRunnable!!, delayMillis)


            }

            override fun afterTextChanged(s: Editable?) {
                // Do Nothing
            }
        })

        fetchTheGRNDetails()
        return root
    }

    private fun fetchTheGRNSearchList(search:String) {

        val call = RetrofitClient.instance.searchGRNlits(userid,search)
        call.enqueue(object : Callback<GrnList> {
            override fun onResponse(call: Call<GrnList>, response: Response<GrnList>) {
/*
                if (response.isSuccessful) {
                    binding.ivProgressBar.visibility = View.GONE
                    val apiResponse = response.body()
                    val dataList = apiResponse?.data
                    // Log.v("dat",dataList.toString())

                    if (dataList.isNullOrEmpty()) {
                        // The list is empty
                      //  binding.tvShowPening.visibility = View.VISIBLE
                       // binding.tvShowPening.text = "No Active GRN Available"
                    } else {
                        // The list is not empty
                        grnAdapter.differ.submitList(dataList)
                        Log.v("dat", grnAdapter.differ.submitList(dataList).toString())

                      */
/*  homeAdapter2 = HomeAdapter2(activeList.reversed(),context!!)


                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = homeAdapter2
                        }*//*


                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = grnAdapter
                        }
                    }


                }
*/

                if (response.isSuccessful) {
                    val indentResponse = response.body()

                    if (indentResponse != null && indentResponse.status == 1) {
                        binding.ivProgressBar.visibility = View.GONE


                       val globalList = indentResponse.data
                        grnAdapter.differ.submitList(globalList)
                        grnAdapter.notifyDataSetChanged()


                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = grnAdapter
                        }
                        val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.windowToken, 0)

                    }
                    else {
                        binding.tvShowPening.visibility = View.VISIBLE
                        binding.tvShowPening.text = "No Pending Tickets"
                        // Handle error or unexpected response
                    }
                } else {
                    // Handle API call failure
                }

            }

            override fun onFailure(call: Call<GrnList>, t: Throwable) {
                // handle error
            }
        })

    }


    private fun fetchTheGRNDetails() {

        val call = RetrofitClient.instance.getGRnDetails(userid)
        call.enqueue(object : Callback<GrnList> {
            override fun onResponse(call: Call<GrnList>, response: Response<GrnList>) {
/*
                if (response.isSuccessful) {
                    binding.ivProgressBar.visibility = View.GONE
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
*/

                if (response.isSuccessful) {
                    val indentResponse = response.body()

                    if (indentResponse != null && indentResponse.status == 1) {
                        binding.ivProgressBar.visibility = View.GONE

                        val dataList = indentResponse.data
                        grnAdapter.differ.submitList(dataList.reversed())   //now added reverse function in android @5.53 pm need to check while debugging

                       // binding.tvCount.text = globalList?.size.toString()
                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = grnAdapter
                        }
                    }
                    else {
                        binding.tvShowPening.visibility = View.VISIBLE
                        binding.tvShowPening.text = "No Active GRN Available"
                        // Handle error or unexpected response
                    }
                } else {
                    // Handle API call failure
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