package com.android.hre

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.AttendanceAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentAttendanceBinding
import com.android.hre.databinding.FragmentNotificationsBinding
import com.android.hre.response.attendncelist.AttendanceListData
import com.android.hre.response.getappdata.AppDetails
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!
    var userid :String = ""
    var frommdate :String = ""
    var toodate :String = ""
    private var fromDate: Calendar = Calendar.getInstance()
    private var toDate: Calendar = Calendar.getInstance()

    private lateinit var attedanceadapter: AttendanceAdapter
    val attendanceListData: ArrayList<AttendanceListData.Data> = arrayListOf()


    private val fromDateClickListener = View.OnClickListener {
        showDatePickerDialog(true)
    }

    private val toDateClickListener = View.OnClickListener {
        showDatePickerDialog(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
     //   return inflater.inflate(R.layout.fragment_attendance, container, false)

        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!


        binding.etDate.setOnClickListener(fromDateClickListener)
        binding.etMonth.setOnClickListener(toDateClickListener)

        attedanceadapter = AttendanceAdapter()

        binding.ivBack.setOnClickListener {
            activity?.finish()
        }
//        if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
//            fetchtheappData()
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val todaydate = LocalDate.now()
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val currentDate = sdf.format(Date())
            println("Months first date in yyyy-mm-dd: " + todaydate.withDayOfMonth(1) + "  " + currentDate)

            val fdate = todaydate.withDayOfMonth(1)

            fetchtheattendanceListt(fdate.toString(),currentDate)

        } else{
            fetchtheattendanceList()
        }


    return root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchtheattendanceListt(fdate: String, currentDate: String) {
        attendanceListData.clear()
        val call = RetrofitClient.instance.getattendance(userid,fdate,currentDate)
        call.enqueue(object : Callback<AttendanceListData> {
            override fun onResponse(call: Call<AttendanceListData>, response: Response<AttendanceListData>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    Log.v("dat", dataList.toString())

                    for(i  in 0 until dataList!!.size){
                        val data  = dataList.get(i)
                        if ( data.login.contains("---")){

                        }else{
                            attendanceListData.add(data)
                        }
                    }
                    attedanceadapter.differ.submitList(attendanceListData)

                    binding.rvRecylergrndata.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = attedanceadapter
                    }

                } else  {

                    // Handle error response
                }
            }

            override fun onFailure(call: Call<AttendanceListData>, t: Throwable) {
                // Handle network error
            }
        })
    }


    private fun showDatePickerDialog(isFromDate: Boolean) {
        val calendar = if (isFromDate) fromDate else toDate

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)

            updateSelectedDate(isFromDate)
        }, year, month, day)

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
    private fun updateSelectedDate(isFromDate: Boolean) {
        val calendar = if (isFromDate) fromDate else toDate

        // val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val formattedDate = dateFormat.format(calendar.time)

        if (isFromDate) {
            binding.etDate.setText(formattedDate)
            frommdate = binding.etDate.text.toString()
            Log.v("Date","$frommdate")
        } else {
            binding.etMonth.setText(formattedDate)
            toodate = binding.etMonth.text.toString()
            Log.v("Date","$toodate")
        }

        fetchtheattendanceList()
    }
    private fun fetchtheattendanceList() {
        attendanceListData.clear()
        val call = RetrofitClient.instance.getattendance(userid,frommdate,toodate)
        call.enqueue(object : Callback<AttendanceListData> {
            override fun onResponse(call: Call<AttendanceListData>, response: Response<AttendanceListData>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    Log.v("dat", dataList.toString())

                    for(i  in 0 until dataList!!.size){
                        val data  = dataList.get(i)
                        if ( data.login.contains("---")){

                        }else{
                            attendanceListData.add(data)
                        }
                    }
                    attedanceadapter.differ.submitList(attendanceListData)

                    binding.rvRecylergrndata.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = attedanceadapter
                    }

                } else  {

                    // Handle error response
                }
            }

            override fun onFailure(call: Call<AttendanceListData>, t: Throwable) {
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