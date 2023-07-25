package com.android.hre

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.AttendanceAdapter
import com.android.hre.adapter.StatementAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentPettyCashBinding
import com.android.hre.databinding.FragmentPettyCashScreenBinding
import com.android.hre.response.attendncelist.AttendanceListData
import com.android.hre.response.pettycashfirstscreen.PettyCashFirstScreen
import com.android.hre.response.statment.StatementListData
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class PettyCashScreenFragment : Fragment() {
    private lateinit var binding: FragmentPettyCashScreenBinding
    var userid :String = ""
    var name :String = ""
    var totalBalance : Int = 0
    var remaingCash :Int = 0
    var spendCash :Int = 0
    var PettyId : String = ""
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    private var fromDate: Calendar = Calendar.getInstance()
    private var toDate: Calendar = Calendar.getInstance()
    var frommdate :String = ""
    var toodate :String = ""
    private lateinit var statmentadapter: StatementAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPettyCashScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences =
            context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
        userid = sharedPreferences?.getString("user_id", "")!!
        name = sharedPreferences?.getString("username", "")!!


        statmentadapter = StatementAdapter()

        binding.btnMaterials.setOnClickListener {
            val inflater = LayoutInflater.from(context)
            val popupView = inflater.inflate(R.layout.popupcashrequest, null)

            val textViewtname = popupView.findViewById<TextInputEditText>(R.id.ti_qtyoperpr)

            textViewtname.setText(name.capitalize())


            val popupDialog = AlertDialog.Builder(context)
                .setView(popupView)
                .create()

            popupDialog.show()
        }

        binding.linerUploadbill.setOnClickListener {
            binding.linedate.visibility = View.GONE
            binding.rvRecylergrndata.visibility = View.GONE
            binding.etDate.setText("")
            binding.etMonth.setText("")
            val intent = Intent(context, UploadExpensesActivity::class.java)
            startActivity(intent)
        }

        binding.lnStatment.setOnClickListener {
           binding.linedate.visibility = View.VISIBLE

            binding.etDate.setOnClickListener {
               showDatePickerDialog(true)
           }
            binding.etMonth.setOnClickListener {
                showDatePickerDialog(false)
            }
        }

        binding.lnTranscation.setOnClickListener {
            binding.linedate.visibility = View.GONE
            binding.rvRecylergrndata.visibility = View.GONE
            binding.etDate.setText("")
            binding.etMonth.setText("")
            val Intent = Intent(context,TranscationInfoActivity::class.java)
            startActivity(Intent)

        }

        pettycashFromServer()


        return root
    }

    private fun pettycashFromServer() {

        RetrofitClient.instance.getpettycashDetails(userid)
            .enqueue(object: retrofit2.Callback<PettyCashFirstScreen> {
                override fun onFailure(call: Call<PettyCashFirstScreen>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<PettyCashFirstScreen>, response: Response<PettyCashFirstScreen>) {
                    Log.v("Sucess", response.body().toString())
                    var listMaterials: PettyCashFirstScreen? = response.body()


                    var arrayList_details: List<PettyCashFirstScreen.Data>? = listMaterials?.data

                    for (i in 0 until arrayList_details?.size!!) {
                        val dataString: PettyCashFirstScreen.Data = arrayList_details.get(i)
                        binding.pcnClinet.text = dataString.issued_amount.toString()
                        binding.tvexpense.text = dataString.balance_amount.toString()
                        //cashLogic(dataString)

                    }

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
            if (binding.etMonth.toString().isEmpty()){
                binding.rvRecylergrndata.visibility = View.GONE
            }else{
                binding.rvRecylergrndata.visibility = View.VISIBLE
            }
            Log.v("Date","$toodate")
        }

        fetchthestatmentList()
    }

    private fun fetchthestatmentList() {
        val call = RetrofitClient.instance.getStatmnet(userid,frommdate,toodate)
        call.enqueue(object : Callback<StatementListData> {
            override fun onResponse(call: Call<StatementListData>, response: Response<StatementListData>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    Log.v("dat", dataList.toString())

                    if (dataList != null) {
                        // Set up the adapter and RecyclerView
                        statmentadapter.differ.submitList(dataList)

                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = statmentadapter
                        }
                      //  statmentadapter.notifyDataSetChanged()

                    } else {

                        // Handle error response
                    }
                }
            }

            override fun onFailure(call: Call<StatementListData>, t: Throwable) {
                // Handle network error
            }
        })
    }

}