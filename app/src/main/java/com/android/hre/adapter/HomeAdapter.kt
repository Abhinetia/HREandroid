package com.android.hre.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.Constants
import com.android.hre.LoginActivity
import com.android.hre.MainActivity
import com.android.hre.R
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.HomeindentlistBinding
import com.android.hre.response.getappdata.AppDetails
import com.android.hre.response.newindentrepo.NewIndents
import com.android.hre.viewmoreindent.ViewMoreIndentActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class HomeAdapter  : RecyclerView.Adapter<HomeAdapter.ViewHolder>(){

    private lateinit var binding : HomeindentlistBinding
    private lateinit var context: Context
    var userid : String = ""



    inner class ViewHolder :RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n", "SuspiciousIndentation")
        fun bind(dataX: NewIndents.Myindent?) {
            binding.apply {
                if (dataX != null){

                    tvdisplaypcn.text = dataX.pcn
                    tvIndentstatus.text = dataX.status
                    tvDisplayindent.text = dataX.indent_no
                    tvdpcndatapcn.text = dataX.pcn_detail
                    //tvDisplaydate.text = dataX.created_on
                    tvdpcndatapcn.isSelected = true

                    if (dataX.status.equals("Completed")){
                        tvIndentstatus.setBackgroundResource(R.drawable.ic_greenbaby)
                    }


                    val inputDateString = dataX.created_on
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())


                    val date = inputFormat.parse(inputDateString)
                    val outputDateString = outputFormat.format(date)
                    val outputTimeString = outputTimeFormat.format(date)

                    tvDisplaydate.text  = outputDateString +" "+ outputTimeString


                    binding.tvViewindents.setOnClickListener {
                        Log.v("TAG",dataX.indent_id.toString())
                        val intent = Intent(context,ViewMoreIndentActivity::class.java)
                        intent.putExtra("indentid",dataX.indent_id.toString())
                        intent.putExtra("pcn",dataX.pcn)
                        intent.putExtra("pcndetails",dataX.pcn_detail)
                        context.startActivity(intent)
                    }
                    binding.carviewdata.setOnClickListener {
                        Log.v("TAG",dataX.indent_id.toString())
                        val intent = Intent(context,ViewMoreIndentActivity::class.java)
                        intent.putExtra("indentid",dataX.indent_id.toString())
                        intent.putExtra("pcn",dataX.pcn)
                        intent.putExtra("pcndetails",dataX.pcn_detail)
                        context.startActivity(intent)
                    }

              }

            }
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
        context.startActivity(intent)
    }

    fun openDataLogin(){
        var intent =  Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
    }

    private fun showAlertDialogOkAndCloseAfter(alertMessage: String) {
        val builder = AlertDialog.Builder(context)
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




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = HomeindentlistBinding.inflate(inflater, parent, false)
        context = parent.context


        val sharedPreferences = context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!

//        if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
//            fetchtheappData()
//        }
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size


    private val differCallback = object : DiffUtil.ItemCallback<NewIndents.Myindent>(){
        override fun areItemsTheSame(oldItem: NewIndents.Myindent, newItem: NewIndents.Myindent): Boolean {
            return oldItem.indent_no == newItem.indent_no
        }

        override fun areContentsTheSame(oldItem: NewIndents.Myindent, newItem: NewIndents.Myindent): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun getItemViewType(position: Int): Int = position  // Shuffling need to be added

}