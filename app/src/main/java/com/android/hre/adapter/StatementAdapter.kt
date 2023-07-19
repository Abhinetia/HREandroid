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
import com.android.hre.databinding.StatmentlistBinding
import com.android.hre.response.statment.StatementListData
import java.text.SimpleDateFormat
import java.util.Locale

class StatementAdapter : RecyclerView.Adapter<StatementAdapter.ViewHolder>() {

    private lateinit var binding: StatmentlistBinding
    private lateinit var context: Context
    var userid : String = ""


    inner class ViewHolder :RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n", "SuspiciousIndentation")
        fun bind(dataX: StatementListData.Data?) {
            binding.apply {
                if (dataX != null){
                   // tvTransdate.text = dataX.transaction_date
                    tvModecashoronline.text = dataX.mode
                    tvTypecreditordebit.text = dataX.type
                    tvAmount.text = dataX.amount
                   // tvEntrydate.text = dataX.bill_submission_date
                    tvRefernceno.text = dataX.ref
                    tvCommnets.text = dataX.comment


                    val inputDateString = dataX.transaction_date
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


                    val date = inputFormat.parse(inputDateString)
                    val outputDateString = outputFormat.format(date)

                    tvTransdate.text  = outputDateString


                    val inputDateStringg = dataX.bill_submission_date
                    val inputFormatt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormatt = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


                    val datee = inputFormatt.parse(inputDateStringg)
                    val outputDateStrineg = outputFormatt.format(datee)

                    tvEntrydate.text  = outputDateStrineg



                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatementAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = StatmentlistBinding.inflate(inflater, parent, false)
        context = parent.context


        val sharedPreferences = context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: StatementAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size
    private val differCallback = object : DiffUtil.ItemCallback<StatementListData.Data>(){
        override fun areItemsTheSame(oldItem: StatementListData.Data, newItem: StatementListData.Data): Boolean {
            return oldItem.transaction_date == newItem.transaction_date
        }

        override fun areContentsTheSame(oldItem: StatementListData.Data, newItem: StatementListData.Data): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun getItemViewType(position: Int): Int = position  // Shuffling need to be added

}