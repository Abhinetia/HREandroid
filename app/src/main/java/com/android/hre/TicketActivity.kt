package com.android.hre

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.TicketAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityTicketBinding
import com.android.hre.response.getappdata.AppDetails
import com.android.hre.response.newticketReponse.TikcetlistNew
import com.android.hre.response.tickets.TicketList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TicketActivity : AppCompatActivity(),TicketAdapter.ViewMoreClickListener {

    private lateinit var binding: ActivityTicketBinding

    var userid :String = ""
    private lateinit var ticketAdapter: TicketAdapter
    lateinit var globalList :List<TikcetlistNew.Ticket>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        //setContentView(R.layout.activity_ticket)

        binding = ActivityTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        fetchtheTicketList()
        ticketAdapter = TicketAdapter(this)

        if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
            fetchtheappData()
        }

        fetchtheTicketList()

        binding.btnCretaeTicket.setOnClickListener {
            val intent = Intent(
                this,
                CreateTicketActivity::class.java
            )
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }
    private fun fetchtheTicketList() {
        val call = RetrofitClient.instance.getTickets(userid)
        call.enqueue(object : Callback<TikcetlistNew> {
            override fun onResponse(call: Call<TikcetlistNew>, response: Response<TikcetlistNew>) {


                if (response.isSuccessful) {
                    val indentResponse = response.body()

                    if (indentResponse != null && indentResponse.status == 1) {
                       // val myIndents = indentResponse.data.tickets
                        globalList = indentResponse.data.tickets
                        ticketAdapter.differ.submitList(globalList)   //now added reverse function in android @5.53 pm need to check while debugging

                        binding.tvCount.text = globalList?.size.toString()
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


            }

            override fun onFailure(call: Call<TikcetlistNew>, t: Throwable) {
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

                    val version = getAppVersion(this@TicketActivity)
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

    override fun onBtnClick(position: Int, dataX: TikcetlistNew.Ticket?) {
        val Intent = Intent(this@TicketActivity, ViewTicketActivity::class.java)
        if (dataX != null) {
            Intent.putExtra("TicketNo",dataX.ticket_no)
            Intent.putExtra("Subject",dataX.category)
            Intent.putExtra("Stauts",dataX.status)
            Intent.putExtra("TicketId",dataX.ticket_id)
            Intent.putExtra("pos",position)
            startForResult.launch(Intent)
        }

    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if(intent !=null){
                val position  = intent.getIntExtra("pos",0);
                val dataX = position.let { globalList.get(it) }
                //globalList.find { it.ticket_id == dataX.ticket_id }?.status == "Completed"
                globalList.filter { it.ticket_id == dataX.ticket_id }.forEach { it.status = "Completed" }
                ticketAdapter.notifyItemChanged(position)
               // position.let { ticketAdapter.notifyItemChanged(it) }
            }
            // Handle the Intent
        }
    }


}