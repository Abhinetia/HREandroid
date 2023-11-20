package com.android.hre.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.Constants
import com.android.hre.R
import com.android.hre.ViewTicketActivity
import com.android.hre.databinding.RecvemailBinding
import com.android.hre.databinding.TicketDetailsBinding
import com.android.hre.databinding.ViewmailBinding
import com.android.hre.response.getconve.Conversation
import com.android.hre.response.tickets.TicketList
import com.android.hre.storage.SharedPrefManager
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class ViewTcketAdapter :  RecyclerView.Adapter<ViewTcketAdapter.ViewHolder>()
 {
//RecyclerView.Adapter<RecyclerView.ViewHolder>()
    private lateinit var binding : ViewmailBinding
    private lateinit var binding1: RecvemailBinding
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences

    var userid : String = ""
    private val VIEW_TYPE_1 = 1 //sender
    private val VIEW_TYPE_2 = 2 // receiver
    //private lateinit var username: String


    inner class ViewHolder :RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(dataX: Conversation.Data?) {
            binding.apply {
                if (dataX != null){
                    Log.v("TAG","data is $dataX")
                    tvmesage.text = dataX.message
                    tvreceptient.text = dataX.sender +  " -> "  + dataX.recipient

                    if (dataX.filename.size != 0){
                        if(dataX.filename.size  == 1){
                            if (dataX.filename.get(0).isEmpty()){
                                icinfo.visibility = View.GONE
                            }else{
                                icinfo.visibility = View.VISIBLE
                            }
                        }else{
                            icinfo.visibility = View.VISIBLE
                        }
                       // icinfo.setImageIcon(R.drawable.ic_eye)


//                        for (i in 0 until dataX.filename.size ){
//                            val imageUrl = dataX.filepath + dataX.filename.get(i)
//                            Glide.with(context)
//                                .load(imageUrl)
//                                .into(icinfo)
//
//
//                           // displayImages(dataX.filepath , dataX.filename)
//                        }
                    }else{
                        icinfo.visibility = View.GONE
                    }

                    icinfo.setOnClickListener {
                        displayImages(dataX.filepath , dataX.filename)
                    }

                    val inputDateString = dataX.date
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


                    val date = inputFormat.parse(inputDateString)
                    val outputDateString = outputFormat.format(date)

                    tvdate.text  = outputDateString

                }

            }
        }

        private fun displayImages(filepath: String, filename: List<String>) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_image_preview)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val previewImageView = dialog.findViewById<ImageView>(R.id.previewImageView)
            val ivnotification = dialog.findViewById<ImageView>(R.id.iv_cancel)
            val llImages = dialog.findViewById<LinearLayout>(R.id.ll_images)

            for (i in 0 until filename.size){
                setImages(filepath + filename.get(i),llImages)
            }

            ivnotification.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

        }

    }
     private fun setImages(filepath:String, llImages : LinearLayout){
         val infalot = LayoutInflater.from(context)
         val custrom = infalot.inflate(R.layout.item_single_image_view,null)

         val ivAttachment = custrom.findViewById<ImageView>(R.id.iv_attachment)

         Glide.with(context)
             .load(filepath )
             .into(ivAttachment)

         llImages.addView(ivAttachment)

     }

/*
     private fun displayImages(filepath: String, filename: List<String>) {
         val dialog = Dialog(context)
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
         dialog.setContentView(R.layout.dialog_image_preview)
         dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
         val previewImageView = dialog.findViewById<ImageView>(R.id.previewImageView)
         val ivnotification = dialog.findViewById<ImageView>(R.id.iv_cancel)
         Glide.with(context)
             .load(icinfo)
             .into(previewImageView)

         ivnotification.setOnClickListener {
             dialog.dismiss()
         }
         dialog.show()
     }
*/

//    inner class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        @SuppressLint("SetTextI18n")
//        fun bind(dataX: Conversation.Data?) {
//            binding.apply {
//                if (dataX != null){
//                    tvmesage.text = dataX.message
//                    tvreceptient.text = dataX.recipient
//
//                    val inputDateString = dataX.date
//                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
//
//
//                    val date = inputFormat.parse(inputDateString)
//                    val outputDateString = outputFormat.format(date)
//
//                    tvdate.text  = outputDateString
//
//                }
//
//            }
//        }
//    }
//
//    inner class ViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        @SuppressLint("SetTextI18n")
//        fun bind1(dataX: Conversation.Data?) {
//            binding1.apply {
//                if (dataX != null){
//                    tvmesage.text = dataX.message
//                    tvreceptient.text = dataX.recipient
//
//                    val inputDateString = dataX.date
//                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
//
//
//                    val date = inputFormat.parse(inputDateString)
//                    val outputDateString = outputFormat.format(date)
//
//                    tvdate.text  = outputDateString
//
//                }
//
//            }
//        }
//    }

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//
//        val inflater = LayoutInflater.from(parent.context)
//        context = parent.context
//               val sharedPreferences = context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
//        var userdata = sharedPreferences?.getString("username","")!!
//
//        return when (viewType) {
//            VIEW_TYPE_1 -> {
////                val inflater = LayoutInflater.from(parent.context)
////        binding = ViewmailBinding.inflate(inflater, parent, false)
////        context = parent.context
//                val view = LayoutInflater.from(parent.context).inflate(R.layout.viewmail, parent, false)
//                ViewHolder1(view)
//            }
//            VIEW_TYPE_2 -> {
////                val inflater = LayoutInflater.from(parent.context)
////                binding1 = RecvemailBinding.inflate(inflater, parent, false)
////                context = parent.context
//                val view = LayoutInflater.from(parent.context).inflate(R.layout.recvemail, parent, false)
//                ViewHolder2(view)
//            }
//            else -> throw IllegalArgumentException("Invalid view type")
//        }
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val inflater = LayoutInflater.from(parent.context)
        binding = ViewmailBinding.inflate(inflater, parent, false)
        context = parent.context


        val sharedPreferences = context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

//    override fun getItemCount(): Int {
//        return differ.currentList.size
//    }
//
//    override fun getItemViewType(position: Int): Int {
//
//        val item = differ.currentList[position]
//
//       // return if (item.sender.equals(username)) VIEW_TYPE_1 else VIEW_TYPE_2
//
//        return 0
//    }

    private val differCallback = object : DiffUtil.ItemCallback<Conversation.Data>(){
        override fun areItemsTheSame(oldItem: Conversation.Data, newItem: Conversation.Data): Boolean {
            return oldItem.sender == newItem.sender
        }

        override fun areContentsTheSame(oldItem: Conversation.Data, newItem: Conversation.Data): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

     override fun getItemViewType(position: Int): Int = position  // Shuffling need to be added

//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val item = differ.currentList[position]
//        when (holder.itemViewType) {
//            VIEW_TYPE_1 -> {
//                val viewHolder1 = holder as ViewHolder1
//                viewHolder1.bind(differ.currentList[position])
//            }
//            VIEW_TYPE_2 -> {
//                val viewHolder2 = holder as ViewHolder2
//                viewHolder2.bind1(differ.currentList[position])
//            }
//        }
//    }
}