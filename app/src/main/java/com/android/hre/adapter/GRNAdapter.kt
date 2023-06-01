package com.android.hre.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.Constants
import com.android.hre.R
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.GrnlistBinding
import com.android.hre.response.countupdate.CountList
import com.android.hre.response.grn.GrnList
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response


class GRNAdapter : RecyclerView.Adapter<GRNAdapter.ViewHolder>() {


    private lateinit var binding: GrnlistBinding
    private lateinit var context: Context
    var userid : String = ""

    inner class ViewHolder :RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(dataX: GrnList.Data?) {
            binding.apply {

                if (dataX != null) {
                    tvgrnnumber.text = dataX.grn
                    tvPcnno.text = dataX.pcn
                    tvIndentnogen.text = dataX.indent_no
                    tvDispatched.text = dataX.dispatched
                    var intendDetailList = dataX.indent_details
                    var intendDetail = intendDetailList.get(0)
                    tvMatrildispaly.text = intendDetail.material_name
                    tvBrnddispaly.text = intendDetail.brand
                    tvPening.text = intendDetail.quantity_pending
                    tvQtyrecved.text = intendDetail.quantity_received
                    tvQtytotal.text = intendDetail.quantity_raised

                    val info = intendDetail.information

                    Log.v("info", info.toString())
                    val jsonObject = JSONObject(info)
                    var stringd: String = ""
                    val iter: Iterator<String> = jsonObject.keys()
                    while (iter.hasNext()) {
                        val key = iter.next()
                        try {
                            val value: Any = jsonObject.get(key)
                            if (stringd == "") {
                                stringd = key + "  :  " + value

                            } else {
                                stringd = stringd + "\n " + key + " : " + value

                            }

                        } catch (e: JSONException) {
                            // Something went wrong!
                        }

                        fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")

                        tvInfo.text = stringd.capitalizeWords()

                        Log.v("data", dataX.indent_details.toString())
                    }

                    binding.tvViewdteuils.setOnClickListener {
                        val inflater = LayoutInflater.from(context)
                        val popupView = inflater.inflate(R.layout.popupwindow, null)

                        val textViewtotalqusntityy = popupView.findViewById<TextInputEditText>(R.id.ti_qtyoperpr)
                        val tvupdate = popupView.findViewById<TextView>(R.id.tv_update)
                        val tvcancel = popupView.findViewById<TextView>(R.id.tv_cancel)


                        tvupdate.setOnClickListener {

                            val count = dataX.dispatched
                            val enteredValue = textViewtotalqusntityy.text.toString().toIntOrNull()



                            if (enteredValue == null) {
                                Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                            } else if ("$enteredValue" > count) {

                                Toast.makeText(context, "Count is greater than entered value", Toast.LENGTH_SHORT).show()
                            } else {

                                val num1: Int = count.toInt()
                                val num2: Int = textViewtotalqusntityy.text.toString().toInt()

                                val rejected =  num1 - num2



                                RetrofitClient.instance.updateGrn(userid,dataX.grn,enteredValue.toString(),rejected.toString())
                                .enqueue(object : retrofit2.Callback<CountList> {
                                    override fun onResponse(call: Call<CountList>, response: Response<CountList>) {
                                        val updateResponse = response.body()
                                        if (updateResponse != null && updateResponse.status == 1) {
                                            showAlertDialogOkAndClose("GRN Updated successfully")
                                            // Update was successful, show a success message
                                          //  Toast.makeText(context, updateResponse.message, Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<CountList>, t: Throwable) {
                                        Toast.makeText(context, "Update failed: ${t.message}", Toast.LENGTH_SHORT).show()
                                    }
                                })

                                Toast.makeText(context, "Count is not greater than entered value", Toast.LENGTH_SHORT).show()
                            }
                        }



                        val popupDialog = AlertDialog.Builder(context)
                            .setView(popupView)
                            .create()

                        tvcancel.setOnClickListener {
                            popupDialog.dismiss()
                        }
                        popupDialog.show()
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = GrnlistBinding.inflate(inflater, parent, false)
        context = parent.context

        val sharedPreferences = context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])    }

    override fun getItemCount(): Int =differ.currentList.size


    private val differCallback = object : DiffUtil.ItemCallback<GrnList.Data>(){
        override fun areItemsTheSame(oldItem: GrnList.Data, newItem: GrnList.Data): Boolean {
            return oldItem.grn == newItem.grn
        }

        override fun areContentsTheSame(oldItem: GrnList.Data, newItem: GrnList.Data): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    private fun showAlertDialogOkAndClose(alertMessage: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->
             }   // (context as Activity).finish()
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }


}