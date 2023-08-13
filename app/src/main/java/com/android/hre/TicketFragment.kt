package com.android.hre

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.TicketAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentTicketBinding
import com.android.hre.response.getappdata.AppDetails
import com.android.hre.response.newticketReponse.TikcetlistNew
import com.android.hre.response.tickets.TicketList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TicketFragment : Fragment() {
    private lateinit var binding: FragmentTicketBinding

    lateinit var cretaeTicket: TextView
    lateinit var createviewReplies :TextView
    var userid :String = ""
    private lateinit var ticketAdapter: TicketAdapter


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  return inflater.inflate(R.layout.fragment_ticket, container, false)


        binding = FragmentTicketBinding.inflate(layoutInflater)
        val root: View = binding.root

        val sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        fetchtheTicketList()
     ticketAdapter = TicketAdapter()

        if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
            fetchtheappData()
        }

        fetchtheTicketList()
            return root
    }

    private fun fetchtheTicketList() {
        val call = RetrofitClient.instance.getTickets(userid)
        call.enqueue(object : Callback<TikcetlistNew> {
            override fun onResponse(call: Call<TikcetlistNew>, response: Response<TikcetlistNew>) {


                if (response.isSuccessful) {
                    val indentResponse = response.body()

                    if (indentResponse != null && indentResponse.status == 1) {
                        val myIndents = indentResponse.data.tickets

                        ticketAdapter.differ.submitList(myIndents)   //now added reverse function in android @5.53 pm need to check while debugging

                        binding.tvCount.text = myIndents?.size.toString()
                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = ticketAdapter
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

/*
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    Log.v("dat", dataList.toString())

                    ticketAdapter.differ.submitList(dataList)   //now added reverse function in android @5.53 pm need to check while debugging

                    binding.tvCount.text = dataList?.size.toString()
                    binding.rvRecylergrndata.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = ticketAdapter
                    }

                } else  {

                    // Handle error response
                }
*/
            }

            override fun onFailure(call: Call<TikcetlistNew>, t: Throwable) {
                // Handle network error
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity?.let{
            cretaeTicket = it.findViewById(R.id.btn_cretae_ticket)
          //  createviewReplies = it.findViewById(R.id.tv_viewmore)


            cretaeTicket.setOnClickListener {
                val intent = Intent(
                    context,
                    CreateTicketActivity::class.java
                )
                mStartForResult.launch(intent)

            }

//            createviewReplies.setOnClickListener {
//                val Intent = Intent(view.context,ViewTicketActivity::class.java)
//                startActivity(Intent)
//            }

            binding.ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }


        }

    }

    var mStartForResult = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            fetchtheTicketList()
        }
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
