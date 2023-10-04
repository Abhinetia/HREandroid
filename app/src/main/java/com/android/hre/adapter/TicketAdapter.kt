package com.android.hre.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.Constants
import com.android.hre.R
import com.android.hre.UpdateTicketActivity
import com.android.hre.ViewTicketActivity
import com.android.hre.databinding.TicketDetailsBinding
import com.android.hre.response.newticketReponse.TikcetlistNew
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class TicketAdapter(val btnlistner: ViewMoreClickListener) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {

    private lateinit var binding: TicketDetailsBinding
    private lateinit var context: Context
    var userid : String = ""
    
    companion object{
        var moreClickListener:ViewMoreClickListener? = null
    }

    inner class ViewHolder :RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n", "SuspiciousIndentation")
        fun bind(dataX: TikcetlistNew.Ticket?,position: Int) {
            binding.apply {
                if (dataX != null){
                    tvticketno.text = dataX.ticket_no
                    tvTicketstatus.text = dataX.status
                    tvPcn.text = dataX.pcn
                    tvdpcndatapcn.text = dataX.pcn_detail

                    val open = dataX.status
//                    tvTicketstatus.text = "$open"
//                    tvTicketstatus.setText("Open")

//
                    if (dataX.status.contains("Pending/Ongoing")){
                        tvTicketstatus.setBackgroundResource(R.drawable.ic_babypinkboreder)
                        tvViewmore.visibility = View.VISIBLE
                        //tvMailcount.visibility = View.VISIBLE
                        tvAssigned.visibility = View.INVISIBLE
                    }
                     else if (dataX.status.contains("Completed")){
                        tvTicketstatus.setBackgroundResource(R.drawable.ic_greenbaby)
                        tvViewmore.visibility = View.VISIBLE
                       // tvMailcount.visibility = View.VISIBLE
                        tvAssigned.visibility = View.INVISIBLE
                    } else if (dataX.status.contains("Rejected")){
                        tvTicketstatus.setBackgroundResource(R.drawable.ic_rectangle)
                        tvViewmore.visibility = View.GONE
                       // tvMailcount.visibility = View.GONE
                        tvAssigned.visibility = View.GONE
                    } else if (dataX.status.contains("Created")){
                        tvTicketstatus.setBackgroundResource(R.drawable.ic_rectangle)
                       //tvMailcount.visibility = View.GONE
                        tvAssigned.visibility = View.GONE
                        tvViewmore.text = "Update Ticket"
                    } else if (dataX.status.contains("Resolved")){
                        tvTicketstatus.setBackgroundResource(R.drawable.ic_rectangle)
                        tvAssigned.visibility = View.GONE

                    }

                    tvTicketsubject.text = dataX.category
                    tvBody.text = dataX.message
                    //tvDisplaydate.text = dataX.created_on
                    icinfo.setOnClickListener {
//                        if (dataX.filename.contains(".jpg") || dataX.filename.contains(".jpeg") || dataX.filename.contains(".png")) {
//                        }
                            if(dataX.filename.size == 0){
                                //popup
                                val builder = AlertDialog.Builder(context)
                                builder.setTitle("HRE Alert")
                                builder.setMessage("No Image Has been uploaded")

                                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                                    Toast.makeText(context,
                                        android.R.string.yes, Toast.LENGTH_SHORT).show()
                                }

                                builder.setNegativeButton(android.R.string.no) { dialog, which ->
                                    Toast.makeText(context,
                                        android.R.string.no, Toast.LENGTH_SHORT).show()
                                }

                                builder.show()
                                return@setOnClickListener
                            }

                        displayImages(dataX.filepath , dataX.filename)

                    }


                    val inputDateString = dataX.created_on
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


                    val date = inputFormat.parse(inputDateString)
                    val outputDateString = outputFormat.format(date)

                    tvDate.text  = outputDateString

                    binding.tvViewmore.setOnClickListener {
                        if (dataX.status.contains("Created")){
                              if (tvViewmore.text.contains("Update Ticket")){
                                  val Intent = Intent(context,UpdateTicketActivity::class.java)
                                  Intent.putExtra("TicketId",dataX.ticket_id)
                                  Intent.putExtra("TicketNo",dataX.ticket_no)
                                  Intent.putExtra("Subject",dataX.category)
                                  Intent.putExtra("Body",dataX.message)
                                  Intent.putExtra("PCN",dataX.pcn)
                                  Intent.putExtra("PCN_Detilas",dataX.pcn_detail)
                                  Intent.putExtra("Priority",dataX.priority)
                                  context.startActivity(Intent)
                              }
                        } else if (dataX.status.equals("Pending/Ongoing") || dataX.status.equals("Completed") || dataX.status.equals("Resolved")){
                            if (tvViewmore.text.contains("View Convo")){

                                moreClickListener = btnlistner
                                moreClickListener?.onBtnClick(position,dataX)
////
//                                val Intent = Intent(context, ViewTicketActivity::class.java)
//                                Intent.putExtra("TicketId",dataX.ticket_id)
//                                Intent.putExtra("TicketNo",dataX.ticket_no)
//                                Intent.putExtra("Subject",dataX.category)
//                                Intent.putExtra("Stauts",dataX.status)
//                                Intent.putExtra("TicketId",dataX.ticket_id)
//                                context.startActivity(Intent)



                            }


                        }
//                        val Intent = Intent(context, ViewTicketActivity::class.java)
////                        Intent.putExtra("TicketId",dataX.ticket_id)
//                        Intent.putExtra("TicketNo",dataX.ticket_no)
//                        Intent.putExtra("Subject",dataX.category)
//                        Intent.putExtra("Stauts",dataX.status)
//                        Intent.putExtra("ticketid",dataX.ticket_id)
//                        context.startActivity(Intent)

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

    @SuppressLint("MissingInflatedId")
    private fun setImages(filepath:String, llImages : LinearLayout){
        val infalot = LayoutInflater.from(context)
        val custrom = infalot.inflate(R.layout.item_single_image_view,null)

        val ivAttachment = custrom.findViewById<ImageView>(R.id.iv_attachment)

        Glide.with(context)
            .load(filepath )
            .into(ivAttachment)

        ivAttachment.setOnClickListener {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_image_preview)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val previewImageView = dialog.findViewById<ImageView>(R.id.previewImageView)
            val ivnotification = dialog.findViewById<ImageView>(R.id.iv_cancel)
            val llImages = dialog.findViewById<LinearLayout>(R.id.ll_images)
            previewImageView.visibility = View.VISIBLE
            Glide.with(context)
                .load(filepath)
                .into(previewImageView)

            ivnotification.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
       // preview(ivAttachment)

        llImages.addView(ivAttachment)

    }

    private fun preview(ivAttachment: ImageView?) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_image_preview)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val previewImageView = dialog.findViewById<ImageView>(R.id.previewImageView)
        val ivnotification = dialog.findViewById<ImageView>(R.id.iv_cancel)
        val llImages = dialog.findViewById<LinearLayout>(R.id.ll_images)

        Glide.with(context)
            .load(previewImageView)
            .into(ivAttachment!!)

        ivnotification.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    open interface ViewMoreClickListener{
        fun onBtnClick(position: Int,dataX: TikcetlistNew.Ticket?)
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
        holder.bind(differ.currentList[position],position)
    }

    override fun getItemCount(): Int = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<TikcetlistNew.Ticket>(){
        override fun areItemsTheSame(oldItem: TikcetlistNew.Ticket, newItem: TikcetlistNew.Ticket): Boolean {
            return oldItem.ticket_no == newItem.ticket_no
        }

        override fun areContentsTheSame(oldItem: TikcetlistNew.Ticket, newItem: TikcetlistNew.Ticket): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun getItemViewType(position: Int): Int = position  // Shuffling need to be added
}