package com.android.hre.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.Constants
import com.android.hre.databinding.AttendancelistBinding
import com.android.hre.response.attendncelist.AttendanceListData
import java.text.SimpleDateFormat
import java.util.Locale


class AttendanceAdapter : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {
    private lateinit var binding: AttendancelistBinding
    private lateinit var context: Context
    var userid : String = ""

    inner class ViewHolder :RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n", "SuspiciousIndentation")
        fun bind(dataX: AttendanceListData.Data?) {
            binding.apply {
                if (dataX != null){
                    Log.v("TAG","${dataX.date}")
                    tvTmee.text = dataX.login
                    tvTimee.text = dataX.logout

                        // Date caluclation made proper
                        val myDividend = dataX.working_minutes.toInt()
                        val myDivisor = 60

                        val resultQuotient = myDividend / myDivisor
                        val resultRemainder = myDividend % myDivisor

                        tvLotimee.text = "$resultQuotient"+"hr" +  ":" + "$resultRemainder"+"mm"

                      //  tvLotimee.text = dataX.working_minutes


//                        val timeSec: String = dataX.working_minutes
//
//                        val hours = timeSec.toInt() / 3600
//                        var temp = timeSec.toInt() - hours * 3600
//                        val mins = temp / 60
//                        temp = temp - mins * 60
//                        val secs = temp
//
//                        val requiredFormat = "$hours Hr: $temp Min"

                       // tvLotimee.text = requiredFormat.toString()


//                    val seconds: String = dataX.working_minutes.toInt().toString()
//                    val S = seconds.toInt() % 60
//                    var H = seconds.toInt() / 60
//                    val M = H % 60
//                    H = H / 60
//                    print("$H:$M:$S")
//
//                    val requiredFormat = "$H Hr: $M"
//                    tvLotimee.text = requiredFormat.toString()

                    val inputDateString = dataX.date
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


                    val date = inputFormat.parse(inputDateString)
                    val outputDateString = outputFormat.format(date)

                    tvDisplaydaee.text  = outputDateString

//                    "date": "2023-05-09",
//                    "login": "---",
//                    "login_location": "",
//                    "logout": "---",
//                    "logout_location": "",
//                    "working_minutes": "---"


                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = AttendancelistBinding.inflate(inflater, parent, false)
        context = parent.context


        val sharedPreferences = context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        return ViewHolder()
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }
    private val differCallback = object : DiffUtil.ItemCallback<AttendanceListData.Data>(){
        override fun areItemsTheSame(oldItem: AttendanceListData.Data, newItem: AttendanceListData.Data): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: AttendanceListData.Data, newItem: AttendanceListData.Data): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
    override fun getItemViewType(position: Int): Int = position  // Shuffling need to be added

}