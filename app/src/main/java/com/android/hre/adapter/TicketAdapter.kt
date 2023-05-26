package com.android.hre.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.Constants
import com.android.hre.R
import com.android.hre.UpdateTicketActivity
import com.android.hre.ViewTicketActivity
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentTicketBinding
import com.android.hre.databinding.HomeindentlistBinding
import com.android.hre.databinding.TicketDetailsBinding
import com.android.hre.response.departement.GetDepartment
import com.android.hre.response.homeindents.GetIndentsHome
import com.android.hre.response.tickets.TicketList
import com.android.hre.viewmoreindent.ViewMoreIndentActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TicketAdapter : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {

    private lateinit var binding: TicketDetailsBinding
    private lateinit var context: Context
    var userid : String = ""

    inner class ViewHolder :RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n", "SuspiciousIndentation")
        fun bind(dataX: TicketList.Data?) {
            binding.apply {
                if (dataX != null){
                    tvticketno.text = dataX.ticket_no
                    tvTicketstatus.text = dataX.status
                    val open = dataX.status
//                    tvTicketstatus.text = "$open"
//                    tvTicketstatus.setText("Open")

//
                    if (dataX.status.contains("Pending")){
                        tvTicketstatus.setBackgroundResource(R.drawable.ic_babypinkboreder)
                        tvViewmore.visibility = View.VISIBLE
                        tvMailcount.visibility = View.VISIBLE
                        tvAssigned.visibility = View.INVISIBLE
                    }
                     else if (dataX.status.contains("Completed")){
                        tvTicketstatus.setBackgroundResource(R.drawable.ic_greenbaby)
                        tvViewmore.visibility = View.VISIBLE
                        tvMailcount.visibility = View.VISIBLE
                        tvAssigned.visibility = View.INVISIBLE
                    } else if (dataX.status.contains("Rejected")){
                        tvTicketstatus.setBackgroundResource(R.drawable.round_corner)
                        tvViewmore.visibility = View.GONE
                        tvMailcount.visibility = View.GONE
                        tvAssigned.visibility = View.VISIBLE
                    } else if (dataX.status.contains("Created")){
                        tvTicketstatus.setBackgroundResource(R.drawable.ic_rectangle)
                        tvViewmore.visibility = View.GONE
                        tvMailcount.visibility = View.GONE
                        tvAssigned.visibility = View.VISIBLE
                    }

                    tvTicketsubject.text = dataX.category
                    tvBody.text = dataX.message
                    //tvDisplaydate.text = dataX.created_on


                    val inputDateString = dataX.created_on
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


                    val date = inputFormat.parse(inputDateString)
                    val outputDateString = outputFormat.format(date)

                    tvDate.text  = outputDateString

                    binding.tvViewmore.setOnClickListener {
                        val Intent = Intent(context, ViewTicketActivity::class.java)
                        Intent.putExtra("TicketId",dataX.ticket_id)
                        Intent.putExtra("TicketNo",dataX.ticket_no)
                        Intent.putExtra("Subject",dataX.category)
                        Intent.putExtra("Stauts",dataX.status)
                        context.startActivity(Intent)

                    }

                    binding.tvAssigned.setOnClickListener {
                        val Intent = Intent(context,UpdateTicketActivity::class.java)
                        Intent.putExtra("TicketId",dataX.ticket_id)
                        Intent.putExtra("TicketNo",dataX.ticket_no)
                        Intent.putExtra("Subject",dataX.category)
                        Intent.putExtra("Body",dataX.message)
                        Intent.putExtra("PCN",dataX.pcn)
                        Intent.putExtra("Priority",dataX.priority)

                        context.startActivity(Intent)
                    }
                }

            }
        }



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = TicketDetailsBinding.inflate(inflater, parent, false)
        context = parent.context


        val sharedPreferences = context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<TicketList.Data>(){
        override fun areItemsTheSame(oldItem: TicketList.Data, newItem: TicketList.Data): Boolean {
            return oldItem.ticket_no == newItem.ticket_no
        }

        override fun areContentsTheSame(oldItem: TicketList.Data, newItem: TicketList.Data): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

}