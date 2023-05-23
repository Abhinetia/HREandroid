package com.android.hre.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.Constants
import com.android.hre.R
import com.android.hre.databinding.HomeindentlistBinding
import com.android.hre.response.homeindents.GetIndentsHome
import com.android.hre.viewmoreindent.ViewMoreIndentActivity
import java.text.SimpleDateFormat
import java.util.*

class HomeAdapterNew(private var context: Context, reversed: List<GetIndentsHome.Data>?) :
    ListAdapter<GetIndentsHome.Data, HomeAdapterNew.ViewHolder>(differCallback) {
    private lateinit var binding : HomeindentlistBinding
    var userid : String = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.homeindentlist, parent, false)
        val inflater = LayoutInflater.from(parent.context)
        binding = HomeindentlistBinding.inflate(inflater, parent, false)
        context = parent.context


        val sharedPreferences = context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = getItem(position)
            holder.bind(data)
        }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
           // private val textView: TextView = itemView.findViewById(R.id.textView)
            @SuppressLint("SetTextI18n", "SuspiciousIndentation")
            fun bind(dataX: GetIndentsHome.Data?) {
                binding.apply {
                    if (dataX != null){
                        tvdisplaypcn.text = dataX.pcn
                        tvIndentstatus.text = dataX.status
                        tvDisplayindent.text = dataX.indent_no
                        //tvDisplaydate.text = dataX.created_on


                        val inputDateString = dataX.created_on
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())


                        val date = inputFormat.parse(inputDateString)
                        val outputDateString = outputFormat.format(date)
                        val outputTimeString = outputTimeFormat.format(date)

                        tvDisplaydate.text  = outputDateString +" "+ outputTimeString


                        binding.tvViewindents.setOnClickListener {
                            Log.v("TAG",dataX.indent_id.toString())
                            val intent = Intent(context, ViewMoreIndentActivity::class.java)
                            intent.putExtra("indentid",dataX.indent_id.toString())
                            context.startActivity(intent)
                        }
                    }

                }
            }

        }

    companion object {
        private val differCallback = object : DiffUtil.ItemCallback<GetIndentsHome.Data>() {
            override fun areItemsTheSame(
                oldItem: GetIndentsHome.Data,
                newItem: GetIndentsHome.Data
            ): Boolean {
                return oldItem.indent_no == newItem.indent_no
            }

            override fun areContentsTheSame(
                oldItem: GetIndentsHome.Data,
                newItem: GetIndentsHome.Data
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}