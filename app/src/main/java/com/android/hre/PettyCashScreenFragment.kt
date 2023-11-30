package com.android.hre

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.StatementAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentPettyCashScreenBinding
import com.android.hre.response.pettycashfirstscreen.PettyCashFirstScreen
import com.android.hre.response.statementNew.NewStatment
import com.android.hre.response.statment.StatementListData
import com.android.hre.response.transcationinfo.TranscationInfoDetails
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale




class PettyCashScreenFragment : Fragment() {
    private lateinit var binding: FragmentPettyCashScreenBinding
    var userid :String = ""
    var name :String = ""

    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    private var fromDate: Calendar = Calendar.getInstance()
    private var toDate: Calendar = Calendar.getInstance()
    var frommdate :String = ""
    var toodate :String = ""
    private lateinit var statmentadapter: StatementAdapter

    var issuedAm : String = ""
    var balancecash :String = ""
    var statemnetlistData: ArrayList<StatementListData.Data> = arrayListOf()
    var statemnetlistNewData: ArrayList<NewStatment.Data> = arrayListOf()

    var transList :List<TranscationInfoDetails.Data> = arrayListOf()



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

        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.linerUploadbill.setOnClickListener {
            binding.linedate.visibility = View.GONE
            binding.rvRecylergrndata.visibility = View.GONE
            binding.etDate.setText("")
            binding.etMonth.setText("")
           // val intent = Intent(context, UploadExpensesActivity::class.java)
            val intent = Intent(context, UploadExpenseActivityNew::class.java)

            startActivity(intent)
        }

        binding.lnStatment.setOnClickListener {
           binding.linedate.visibility = View.VISIBLE

//            binding.stratdate.setOnClickListener {
//                showDatePickerDialog(true)
//            }
            binding.etDate.setOnClickListener {
               showDatePickerDialog(true)
           }
//            binding.crdmonthg.setOnClickListener {
//                showDatePickerDialog(false)
//            }
            binding.etMonth.setOnClickListener {
                showDatePickerDialog(false)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val todaydate = LocalDate.now()
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val currentDate = sdf.format(Date())
            println("Months first date in yyyy-mm-dd: " + todaydate.withDayOfMonth(1) + "  " + currentDate)

            val fdate = todaydate.withDayOfMonth(1)

            fetchtheattendanceListt(fdate.toString(),currentDate)
            fetchtheattendanceListtNew(fdate.toString(),currentDate)

        }



        pettycashFromServer()
        pettytransInfoDatails()


        return root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchtheattendanceListt(fdate: String, currentDate: String) {
        statemnetlistData.clear()
        val call = RetrofitClient.instance.getStatmnet(userid,fdate,currentDate)
        call.enqueue(object : Callback<StatementListData> {
            override fun onResponse(call: Call<StatementListData>, response: Response<StatementListData>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    statemnetlistData = ArrayList(indentResponse!!.data)
                    Log.v("dat", statemnetlistData.toString())

                    if (statemnetlistData != null) {
                        // Set up the adapter and RecyclerView
                        statmentadapter.differ.submitList(statemnetlistData)

                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = statmentadapter
                        }

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

    private fun fetchtheattendanceListtNew(fdate: String, currentDate: String) {
        statemnetlistNewData.clear()
        val call = RetrofitClient.instance.getStatmnetNew(userid,fdate,currentDate)
        call.enqueue(object : Callback<NewStatment> {
            override fun onResponse(call: Call<NewStatment>, response: Response<NewStatment>) {
                if (response.isSuccessful) {
                    val responseData = response.body()?.data
//
                    Log.v("dat", statemnetlistNewData.toString())

                    val opening = responseData!!.opening
                    val closing = responseData!!.closing

                    binding.tvopening.text= "Opening Bal :" + " " +opening.toString()
                    binding.tvclosing.text= "Closing Bal :" + " " + closing.toString()

                  //  val checkDaata = responseData.summary

/*
                    if (statemnetlistNewData != null) {
                        // Set up the adapter and RecyclerView
                        statmentadapternew.differ.submitList(statemnetlistNewData)

                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = statmentadapternew
                        }

                    } else {

                        // Handle error response
                    }
*/
                }
            }

            override fun onFailure(call: Call<NewStatment>, t: Throwable) {
                // Handle network error
            }
        })
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

                    if(!arrayList_details!!.isEmpty()){
                        val dataString: PettyCashFirstScreen.Data = arrayList_details!!.get(0)
                        binding.pcnClinet.text = "₹" +""+ dataString.issued_amount.toString()
//                        binding.tvexpense.text =  "₹" + ""+dataString.balance_amount.toString()
//                        binding.tvsepend.text =  "₹" + ""+ dataString.my_spend.toString()


                      //  binding.tvexpense.text =  "₹" + ""+dataString.my_spend.toString()
                       // binding.tvsepend.text =  "₹" + ""+ dataString.balance_amount.toString()


                        val num1issued : Int = dataString.issued_amount.toInt()
                        val num2mySpend : Int = dataString.my_spend.toInt()

                        val balancetotal = num1issued - num2mySpend

                        binding.tvsepend.text = balancetotal.toString()


                    }



/*
                    if (arrayList_details!!.isEmpty()){
                        binding.pcnClinet.visibility = View.GONE

                        val dataString: PettyCashFirstScreen.Data = arrayList_details.get(0)
                        binding.pcnClinet.text = dataString.issued_amount.toString()
                        binding.tvexpense.text = dataString.balance_amount.toString()
                       */
/* for (i in 0 until arrayList_details?.size!!) {
                            val dataString: PettyCashFirstScreen.Data = arrayList_details.get(i)
                            binding.pcnClinet.text = dataString.issued_amount.toString()
                            binding.tvexpense.text = dataString.balance_amount.toString()

                        }*//*


                    }
*/
                }

            })
             binding.lnTranscation.setOnClickListener {
                binding.linedate.visibility = View.GONE
                binding.rvRecylergrndata.visibility = View.GONE
                binding.etDate.setText("")
                binding.etMonth.setText("")
               // val intent = Intent(context,TranscationInfoActivity::class.java)

                 if(transList.size != 0){
                     val gson = Gson()
                     val jsonString = gson.toJson(transList)
                     editor.putString("transcation_key",jsonString)
                     editor.apply()
                     val intent = Intent(context, TranscationInfoActivity::class.java)
                     startActivity(intent)
                 }else{

                 }


        }


    }

    fun Intent.putParcelableListExtra(key: String, list: List<Parcelable>) {
        this.putParcelableArrayListExtra(key, ArrayList(list))
    }

    fun Intent.getParcelableListExtra(key: String): List<Parcelable>? {
        return this.getParcelableArrayListExtra(key)
    }

    private fun pettytransInfoDatails() {
        RetrofitClient.instance.getTranscationInfo(userid)
            .enqueue(object: retrofit2.Callback<TranscationInfoDetails> {
                override fun onFailure(call: Call<TranscationInfoDetails>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<TranscationInfoDetails>, response: Response<TranscationInfoDetails>) {
                    Log.v("Sucess", response.body().toString())
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        transList = apiResponse?.data!!
                        if(transList!!.size != 0){
                            setApprovedAmount(transList)
                        }

                    } else {
                        // Handle the error
                    }

                }

            })

    }

    private fun setApprovedAmount(pettyCashBills: List<TranscationInfoDetails.Data>?) {
            var value : Int =  0
            for (i in 0 until pettyCashBills!!.size){

                    val data =  pettyCashBills.get(i)
                    if(!TextUtils.isEmpty(data.utilised_amount) && data.isapproved.equals("Accepted")) {
                        value = value + data.utilised_amount.toInt()
                    }
            }

        binding.tvexpense.text =  "₹" + ""+value

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

        if (frommdate.isNotBlank() && toodate.isNotBlank()) {
            if (checkDates(frommdate, toodate)) {
                fetchthestatmentList()
                fetchtheattendanceListtNew(frommdate,toodate)
            } else {
                showAlertDialogOkAndCloseAfter("End date should be after start date!")
            }
        }
    }

    fun checkDates(d1: String, d2: String): Boolean {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        var b = false

        try {
            val date1 = dateFormat.parse(d1)
            val date2 = dateFormat.parse(d2)

            if (date1 != null && date2 != null) {
                when {
                    date1.before(date2) -> b = true // Start date is before end date
                    date1 == date2 -> b = true // Two dates are equal
                    else -> b = false // Start date is after the end date
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return b
    }

    private fun fetchthestatmentList() {
        statemnetlistData.clear()
        val call = RetrofitClient.instance.getStatmnet(userid,frommdate,toodate)
        call.enqueue(object : Callback<StatementListData> {
            override fun onResponse(call: Call<StatementListData>, response: Response<StatementListData>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    statemnetlistData = ArrayList(indentResponse!!.data)
                    Log.v("dat", statemnetlistData.toString())

                    if (statemnetlistData != null) {
                        // Set up the adapter and RecyclerView
                        statmentadapter.differ.submitList(statemnetlistData)

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


    private fun showAlertDialogOkAndCloseAfter(alertMessage: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->  }  // LoginActivity::class.java
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }


}