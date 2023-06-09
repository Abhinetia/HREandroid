package com.android.hre.ui.notifications

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.Constants
import com.android.hre.R
import com.android.hre.adapter.AttendanceAdapter
import com.android.hre.adapter.TicketAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentNotificationsBinding
import com.android.hre.response.attendncelist.AttendanceListData
import com.android.hre.response.tickets.TicketList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    var userid :String = ""
    var frommdate :String = ""
    var toodate :String = ""
    private var fromDate: Calendar = Calendar.getInstance()
    private var toDate: Calendar = Calendar.getInstance()
    val attendanceListData: ArrayList<AttendanceListData.Data> = arrayListOf()


    private lateinit var attedanceadapter: AttendanceAdapter


    private val fromDateClickListener = View.OnClickListener {
        showDatePickerDialog(true)
    }

    private val toDateClickListener = View.OnClickListener {
        showDatePickerDialog(false)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!


       binding.etDate.setOnClickListener(fromDateClickListener)
        binding.etMonth.setOnClickListener(toDateClickListener)

        attedanceadapter = AttendanceAdapter()


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        val call = RetrofitClient.instance.getattendance(userid,frommdate,toodate)
        call.enqueue(object : Callback<AttendanceListData> {
            override fun onResponse(call: Call<AttendanceListData>, response: Response<AttendanceListData>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    Log.v("dat", dataList.toString())


                    for(i  in 0 until dataList!!.size){
                        val data  = dataList.get(i)
                        if (data.working_minutes.contains("---") || data.login.contains("---")||
                            data.logout.contains("---")){

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


}