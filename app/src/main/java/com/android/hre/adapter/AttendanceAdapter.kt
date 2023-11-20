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

//                        if (dataX.working_minutes != "---"){
                            val myDividend = dataX.working_minutes.toInt()
                            val myDivisor = 60

                            val resultQuotient = myDividend / myDivisor
                            val resultRemainder = myDividend % myDivisor

                            tvLotimee.text = "$resultQuotient"+"hr" +  ":" + "$resultRemainder"+"mm"
//                        } else{
//                            tvLotimee.text = "0"+"hr" +  ":" + "0"+"mm"
//                        }



                    val inputDateString = dataX.date
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


                    val date = inputFormat.parse(inputDateString)
                    val outputDateString = outputFormat.format(date)


                    Log.v("Date","$outputDateString")
                    tvDisplaydaee.text  = outputDateString

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int =differ.currentList.size


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