package com.android.hre.adapter

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.Constants
import com.android.hre.ViewIndentFragment
import com.android.hre.databinding.HomeindentlistBinding
import com.android.hre.response.homeindents.GetIndentsHome
import com.android.hre.viewmoreindent.ViewMoreIndentActivity
import java.text.SimpleDateFormat
import java.util.*


class HomeAdapter  : RecyclerView.Adapter<HomeAdapter.ViewHolder>(){

    private lateinit var binding : HomeindentlistBinding
    private lateinit var context: Context
    var userid : String = ""



    inner class ViewHolder :RecyclerView.ViewHolder(binding.root){
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
                        layoutPosition
                       val intent = Intent(context,ViewMoreIndentActivity::class.java)
                           intent.putExtra("indentid",dataX.indent_id)
                           context.startActivity(intent)
                       }
                }

            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = HomeindentlistBinding.inflate(inflater, parent, false)
        context = parent.context


        val sharedPreferences = context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size


    private val differCallback = object : DiffUtil.ItemCallback<GetIndentsHome.Data>(){
        override fun areItemsTheSame(oldItem: GetIndentsHome.Data, newItem: GetIndentsHome.Data): Boolean {
            return oldItem.indent_id == newItem.indent_id
        }

        override fun areContentsTheSame(oldItem: GetIndentsHome.Data, newItem: GetIndentsHome.Data): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

}