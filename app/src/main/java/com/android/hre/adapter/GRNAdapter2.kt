package com.android.hre.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.Constants
import com.android.hre.GRNCommpleteLkistActivity
import com.android.hre.R
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.GrnlistBinding
import com.android.hre.response.countupdate.CountList
import com.android.hre.response.grn.GrnList
import com.android.hre.response.newindentrepo.NewIndents
import com.android.hre.viewmoreindent.ViewMoreIndentActivity
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale


class GRNAdapter2(private var items: List<GrnList.Data>,private val context :Context) : RecyclerView.Adapter<GRNAdapter2.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvgrnnumber: TextView = itemView.findViewById(R.id.tvgrnnumber)
        val tvPcnno: TextView = itemView.findViewById(R.id.tv_pcnno)
        val tvMaterialName: TextView = itemView.findViewById(R.id.tv_materialName)
        val tvInedntnumber: TextView = itemView.findViewById(R.id.tv_inedntnumber)
        val tvDispatchenumber: TextView = itemView.findViewById(R.id.tv_dispatchenumber)
        val tvIndentnogen :  TextView = itemView.findViewById(R.id.tv_indentnogen)
        val btnCretaeTicket :  TextView = itemView.findViewById(R.id.btn_cretae_ticket)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.grnlist, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataX = items[position]
        holder.tvgrnnumber.text = dataX.grn
        holder.tvPcnno.text = dataX.pcn
        holder.tvIndentnogen.text = dataX.status
        holder.tvInedntnumber.text = dataX.indent_no
        holder.tvDispatchenumber.text = dataX.dispatched

        // tvDispatched.text = dataX.dispatched
        var intendDetailList = dataX.indent_details
        var intendDetail = intendDetailList.get(0)
        holder.tvMaterialName.text = intendDetail.material_name
//
//
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

            // tvInfo.text = stringd.capitalizeWords()

            Log.v("data", dataX.indent_details.toString())
        }


//        holder.btnCretaeTicket.setOnClickListener {
//            val intent = Intent(context,GRNCommpleteLkistActivity::class.java)
//            intent.putExtra("PCN",dataX.pcn)
//            intent.putExtra("GRN",dataX.grn)
//            intent.putExtra("PCNDetails",dataX.pcn_detail)
//            intent.putExtra("IndentNo",dataX.indent_no)
//            intent.putExtra("Dispatched",dataX.dispatched)
//            intent.putExtra("Status",dataX.status)
//            intent.putExtra("MaterialName",intendDetail.material_name)
//            intent.putExtra("Brand", intendDetail.brand)
//            intent.putExtra("Info",stringd)
//            intent.putExtra("Recvied",intendDetail.quantity_received)
//            context.startActivity(intent)
//        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(newItems: List<GrnList.Data>) {
        items = newItems
        notifyDataSetChanged()
    }

}