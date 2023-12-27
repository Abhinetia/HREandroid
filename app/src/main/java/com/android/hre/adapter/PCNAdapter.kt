package com.android.hre.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.R
import com.android.hre.response.attendncelist.AttendanceListData
import com.android.hre.response.pcns.PCN
import java.text.SimpleDateFormat
import java.util.Locale

class PCNAdapter  (private val mList: List<PCN.Data>) : RecyclerView.Adapter<PCNAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pcnlistdata, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        holder.textviewPcn.text = ItemsViewModel.pcn
        holder.textViewpcnBrnad.text = ItemsViewModel.brand

        holder.textViewpcnststus.text = ItemsViewModel.status

        holder.textViewpcnbillingnameaddres.text = ItemsViewModel.client_name
        holder.textViewpcnbillingnameaddres.setSelected(true)
        holder.textviewAddress.text = ItemsViewModel.location + ItemsViewModel.area + ItemsViewModel.city + ItemsViewModel.pincode
        holder.textviewAddress.setSelected(true)



    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textviewPcn: TextView = itemView.findViewById(R.id.tv_PcnNOdata)
        val textViewpcnststus: TextView = itemView.findViewById(R.id.tv_pcnstatus)
        val textViewpcnbillingnameaddres: TextView = itemView.findViewById(R.id.tvpcnbillingname)
        val textViewpcnBrnad: TextView = itemView.findViewById(R.id.tvdpcnbrand)
        val textviewAddress :TextView = itemView.findViewById(R.id.tvpcnaddress)


    }
}