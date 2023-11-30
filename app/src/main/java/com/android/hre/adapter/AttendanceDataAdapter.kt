package com.android.hre.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.R
import com.android.hre.response.attendncelist.AttendanceListData
import java.text.SimpleDateFormat
import java.util.Locale

class AttendanceDataAdapter (private val mList: List<AttendanceListData.Data>) : RecyclerView.Adapter<AttendanceDataAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attendancelist, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
       // holder.textViewDate.text = ItemsViewModel.date
        holder.textViewLogin.text = ItemsViewModel.login
        holder.textViewLogout.text = ItemsViewModel.logout

        val myDividend = ItemsViewModel.working_minutes.toInt()
        val myDivisor = 60

        val resultQuotient = myDividend / myDivisor
        val resultRemainder = myDividend % myDivisor

        holder.textViewworkingMinutes.text = "$resultQuotient"+"hr" +  ":" + "$resultRemainder"+"mm"

        val inputDateString = ItemsViewModel.date
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


        val date = inputFormat.parse(inputDateString)
        val outputDateString = outputFormat.format(date)


        Log.v("Date","$outputDateString")
        holder.textViewDate.text  = outputDateString

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textViewDate: TextView = itemView.findViewById(R.id.tv_displaydaee)
        val textViewLogin: TextView = itemView.findViewById(R.id.tv_tmee)
        val textViewLogout: TextView = itemView.findViewById(R.id.tv_timee)
        val textViewworkingMinutes: TextView = itemView.findViewById(R.id.tv_lotimee)


    }
}