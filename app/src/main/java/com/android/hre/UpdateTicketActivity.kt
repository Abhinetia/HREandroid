package com.android.hre

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityUpdateTicketBinding
import com.android.hre.databinding.ActivityViewTicketBinding
import com.android.hre.response.departement.GetDepartment
import com.android.hre.response.pcns.PCN
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateTicketActivity : AppCompatActivity() {
    var ticketno :String = ""
    var status :String = ""
    var userid :String = ""
    var ticketid :String = ""
    var body :String = ""
    var subject :String = ""
    var pcn :String = ""
    val listdata: ArrayList<String> = arrayListOf()
    val listdata2 :ArrayList<String> = arrayListOf()
    var listDepartmetData :ArrayList<GetDepartment.Data> = arrayListOf()

    private lateinit var binding:ActivityUpdateTicketBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityUpdateTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!

        val intentUser = intent
        ticketno = intentUser!!.getStringExtra("TicketNo").toString()
        status = intentUser.getStringExtra("Stauts").toString()
        subject = intentUser.getStringExtra("Subject").toString()
        ticketid = intentUser.getStringExtra("TicketId").toString()
        body = intentUser.getStringExtra("Body").toString()
        pcn = intentUser.getStringExtra("PCN").toString()


//        binding.etTickettitle.text = ticketno
//        binding.tvstatsus.text = status
//        binding.tvsubject.text = "Subject : "  + subject
        binding.etTickettitle.setText(subject)
        binding.ticketno.text = "Ticket No:" + ticketno
        binding.etDescrtiption.setText(body)
        binding.etSelctpcn.setText(pcn)


        dropdownDepartmentDetails()
        dropdwonfromServer()


    }
    private fun dropdownDepartmentDetails() {
        val call =  RetrofitClient.instance.getdepartment(userid)
        call.enqueue(object : Callback<GetDepartment> {
            override fun onResponse(call: Call<GetDepartment>, response: Response<GetDepartment>) {
                if (response.isSuccessful) {
                    //  val myDataList = response.body()?.data
                    var listMaterials: GetDepartment? = response.body()
                    listdata2.clear()
                    listDepartmetData.clear()
                    listDepartmetData = listMaterials?.data as ArrayList<GetDepartment.Data>
                    for (i in 0 until listDepartmetData?.size!!) {
                        val dataString: GetDepartment.Data = listDepartmetData.get(i)

                        Log.v("log", i.toString())
                        listdata2.add(dataString.category)

                    }
                    val arrayAdapter =
                        ArrayAdapter(this@UpdateTicketActivity, R.layout.dropdwon_item, listdata2)
                    binding.etTickettitle.setAdapter(arrayAdapter)
                    binding.etTickettitle.threshold = 1

                } else {
                }
            }

            override fun onFailure(call: Call<GetDepartment>, t: Throwable) {
                // Handle network error
            }
        })
    }

    private fun dropdwonfromServer() {

        RetrofitClient.instance.getAllPcns(userid)
            .enqueue(object: retrofit2.Callback<PCN> {
                override fun onFailure(call: Call<PCN>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<PCN>, response: Response<PCN>) {
                    Log.v("Sucess", response.body().toString())
                    var listMaterials: PCN? = response.body()
                    listdata.clear()

                    var arrayList_details: List<PCN.Data>? = listMaterials?.data

                    for (i in 0 until arrayList_details?.size!!) {
                        val dataString: PCN.Data = arrayList_details.get(i)

                        Log.v("log", i.toString())
                        listdata.add(dataString.pcn)

                    }
                    val arrayAdapter =
                        ArrayAdapter(this@UpdateTicketActivity, R.layout.dropdwon_item, listdata)
                    binding.etSelctpcn.setAdapter(arrayAdapter)
                     binding.etSelctpcn.threshold = 1

                }

            })


    }

}