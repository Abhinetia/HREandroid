package com.android.hre

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityGrncommpleteLkistBinding
import com.android.hre.response.countupdate.CountList
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Response

class GRNCommpleteLkistActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGrncommpleteLkistBinding
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var name :String = ""
    var role :String = ""
    var empId :String = ""
    var version :String = ""
    var pcn :String = ""
    var grn :String = ""
    var pcndetails : String = ""
    var indentno:String = ""
    var materialname :String = ""
    var materialCategory :String = ""
    var brand :String = ""
    var info :String = ""
    var recvied :String = ""
    var dispatched :String = ""
    var stauts :String = ""
    var userid : String = ""
    var dipactcomment :String = ""
    var acceptcomnet :String = ""
    var qtyPening:String = ""
    var qtyraised :String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_grncommplete_lkist)

        supportActionBar?.hide()

        binding = ActivityGrncommpleteLkistBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
        userid = sharedPreferences?.getString("user_id", "")!!

        name = sharedPreferences?.getString("username", "")!!
        role = sharedPreferences?.getString("role_name","")!!
        empId = sharedPreferences?.getString("employee_id","")!!


        pcn = intent.getStringExtra("PCN")!!
        grn = intent.getStringExtra("GRN")!!
        pcndetails = intent.getStringExtra("PCNDetails")!!
        indentno = intent.getStringExtra("IndentNo")!!
        materialname = intent.getStringExtra("MaterialName")!!
        materialCategory = intent.getStringExtra("MaterialCategory")!!
        brand = intent.getStringExtra("Brand")!!
        info = intent.getStringExtra("Info")!!
        recvied = intent.getStringExtra("Recvied")!!
        dispatched = intent.getStringExtra("Dispatched")!!
        stauts = intent.getStringExtra("Status")!!
        dipactcomment = intent.getStringExtra("dispatchcomment")!!
        qtyPening = intent.getStringExtra("qtypening")!!
        acceptcomnet =  intent.getStringExtra("recviercomment")!!
        qtyraised =   intent.getStringExtra("qtyraised")!!


        binding.tvRecvievercomnet.text = acceptcomnet

        binding.tvgrnnumber.text = grn
        binding.tvPcnno.text = pcn
        binding.tvPcnnodet.text = pcndetails
        binding.tvIndentnogen.text = indentno
        binding.tvMatrildispaly.text = materialname
        binding.tvMatrilcategrory.text = materialCategory
        binding.tvBrnddispaly.text = brand
        binding.tvDispatched.text = qtyraised
        binding.tvInfo.text = info
        binding.tvQtyrecved.text = recvied
        binding.tvStstu.text = stauts
        binding.tvDispatchcomnet.text = dipactcomment
        binding.tvQtypening.text = qtyPening
        binding.tvQtydispatched.text = dispatched

         if (stauts.equals("Received")){
             binding.tvViewdteuils.visibility = View.INVISIBLE
             binding.tvpending.visibility = View.INVISIBLE
         }

        binding.tvpending.setOnClickListener {
            onBackPressed()
        }


        binding.tvViewdteuils.setOnClickListener {
            val inflater = LayoutInflater.from(this)
            val popupView = inflater.inflate(R.layout.popupwindow, null)

            val textViewtotalqusntityy = popupView.findViewById<TextInputEditText>(R.id.ti_qtyoperpr)
            val desc = popupView.findViewById<TextInputEditText>(R.id.description)
            val tvupdate = popupView.findViewById<TextView>(R.id.tv_update)
            val tvcancel = popupView.findViewById<TextView>(R.id.tv_cancel)



            tvupdate.setOnClickListener {

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Receiveing Data")
                progressDialog.setMessage("Please wait...")
                progressDialog.setCancelable(false)
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val count = dispatched
                val enteredValue = textViewtotalqusntityy.text.toString().toIntOrNull()
                val grncommnet = desc.text.toString()

                if (grncommnet.isEmpty()){
                    desc.error = "Please enter the comments"
                    desc.requestFocus()
                    return@setOnClickListener
                }
                if (enteredValue == null){
                    Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()

                }

              /*  if (enteredValue == null) {
                } else if ("$enteredValue" > count) {

                  // Toast.makeText(this, "Count is greater than entered value", Toast.LENGTH_SHORT).show()
                } else {*/

//                    val num1: Int = count.toInt()
//                    val num2: Int = textViewtotalqusntityy.text.toString().toInt()

                    val number: Int = textViewtotalqusntityy.getText().toString().toInt()
                    if (number > count.toInt()) {
                        progressDialog.dismiss()
                        showAlertDialogOkAndCloseAfter("Entered Number is Greater than Dispatched Value")

                    } else{
                        val rejected =  count.toInt() - number

                        RetrofitClient.instance.updateGrn(userid,grn,textViewtotalqusntityy.getText().toString(),rejected.toString(),grncommnet)
                            .enqueue(object : retrofit2.Callback<CountList> {
                                override fun onResponse(call: Call<CountList>, response: Response<CountList>) {
                                    progressDialog.dismiss()
                                    val updateResponse = response.body()
                                    if (updateResponse != null && updateResponse.status == 1) {
                                        showAlertDialogOkAndClose("GRN Updated successfully")
                                        // Update was successful, show a success message
                                        //  Toast.makeText(context, updateResponse.message, Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this@GRNCommpleteLkistActivity, "Update failed", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<CountList>, t: Throwable) {
                                    progressDialog.dismiss()
                                    Toast.makeText(this@GRNCommpleteLkistActivity, "Update failed: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }


            }



            val popupDialog = AlertDialog.Builder(this)
                .setView(popupView)
                .create()

            tvcancel.setOnClickListener {
                popupDialog.dismiss()
            }
            popupDialog.show()
        }

    }

    private fun showAlertDialogOkAndClose(alertMessage: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->
            (this as Activity).setResult(Activity.RESULT_OK)
            (this as Activity)?.onBackPressed() }   // (context as Activity).finish()
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
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

}