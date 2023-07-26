package com.android.hre.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.Notification
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.Constants
import com.android.hre.R
import com.android.hre.databinding.TranscationinfoBinding
import com.android.hre.others.Notifications
import com.android.hre.response.transcationinfo.TranscationInfoDetails
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

// Notification IDs
private const val NOTIFICATION_ID_DOWNLOAD_PROGRESS = 1
private const val NOTIFICATION_ID_DOWNLOAD_COMPLETE = 2
private const val CHANNEL_ID_DOWNLOAD = "download_channel"


class TransInfoAdapter : RecyclerView.Adapter<TransInfoAdapter.ViewHolder>() {
    private lateinit var binding: TranscationinfoBinding
    private lateinit var context: Context
    var userid : String = ""
    val REQUEST_CODE_PERMISSION = 1001
    var manager: DownloadManager? = null


    inner class ViewHolder :RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n", "SuspiciousIndentation")
        fun bind(dataX: TranscationInfoDetails.Data?) {
            binding.apply {
                if (dataX != null){
                    tvAmountutilized.text = dataX.utilised_amount
                    tvIsApproved.text = dataX.isapproved



                    val inputDateString = dataX.bill_date
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


                    val date = inputFormat.parse(inputDateString)
                    val outputDateString = outputFormat.format(date)
                    tvBilldate.text = dataX.bill_date



                    tvviemore.setOnClickListener {
                        val inflater = LayoutInflater.from(context)
                        val popupView = inflater.inflate(R.layout.popupfortransinfomore, null)

                        val billdate = popupView.findViewById<TextView>(R.id.tvbilldate)
                        val amountutilixed = popupView.findViewById<TextView>(R.id.tv_amountutlized)
                        val purpose = popupView.findViewById<TextView>(R.id.tv_purpose)
                        val pcn = popupView.findViewById<TextView>(R.id.tv_pcn)
                        val commnets = popupView.findViewById<TextView>(R.id.tv_comments)
                        val submitDate = popupView.findViewById<TextView>(R.id.tv_billsubmitdate)
                        val remarks = popupView.findViewById<TextView>(R.id.tv_remarks)
                        val isapproved = popupView.findViewById<TextView>(R.id.tv_statsu)
                        val llFiles = popupView.findViewById<LinearLayout>(R.id.ll_files)


                        billdate.text = dataX.bill_date
                        amountutilixed.text= dataX.utilised_amount
                        purpose.text = dataX.purpose
                        commnets.text= dataX.comments
                        remarks.text = dataX.remarks
                        isapproved.text = dataX.isapproved
                        pcn.text= dataX.pcn


                        for(i  in 0 until dataX.filename!!.size){
                            val data  = dataX.filename.get(i)
                            if(data.length > 1){
                                inflatefiles(llFiles,data,dataX.filepath)
                            }
                        }


                        val inputDateStringg = dataX.bill_submission_date
                        val inputFormatt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val outputFormatt = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


                        val datee = inputFormatt.parse(inputDateStringg)
                        val outputDateStrineg = outputFormatt.format(datee)

                        submitDate.text = outputDateStrineg

                        val popupDialog = AlertDialog.Builder(context)
                            .setView(popupView)
                            .create()


                        popupDialog.show()

                    }


                }

            }
        }

    }


    private fun inflatefiles(llFiles : LinearLayout, filename : String, filePath : String) {

        val infalot = LayoutInflater.from(context)
        val custrom = infalot.inflate(R.layout.item_image_view,null)

        val ivFileName = custrom.findViewById<ImageView>(R.id.iv_qtytotal)



        if (filename.lowercase().contains(".jpg") || filename.lowercase().contains(".jpeg") || filename.lowercase().contains(".png") ||
            filename.lowercase().contains(".gif")){
                Glide.with(context)
                    .load(filePath+ filename)
                    .into(ivFileName)
        }
        else{
            ivFileName.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.attach_svgrepo_com))
        }

        ivFileName.setOnClickListener {
            if (filename.lowercase().contains(".jpg") || filename.lowercase().contains(".jpeg") || filename.lowercase().contains(".png") ||
                filename.lowercase().contains(".gif")){
                val dialog = Dialog(context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_image_preview)
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val previewImageView = dialog.findViewById<ImageView>(R.id.previewImageView)
                val ivnotification = dialog.findViewById<ImageView>(R.id.iv_cancel)
                Glide.with(context)
                    .load(filePath+ filename)
                    .into(previewImageView)
                ivnotification.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }else{
              //  ivFileName.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.attach_svgrepo_com))

              //  downloadFile(context,filePath,filename)
//                var manager: DownloadManager? = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
//                val uri =
//                    Uri.parse("https://hre.netiapps.com/pettycashfiles/459705.pdf")
//                val request = DownloadManager.Request(uri)
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
//                val reference: Long = manager!!.enqueue(request)


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    downloadFileWithMediaStore(context,filePath+filename,filename)
                }else{
                    downloadFilePreQ(context,filePath+filename,filename)
                }

            }
        }

        llFiles.addView(custrom)
    }

    fun downloadFilePreQ(context: Context, fileUrl: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(fileUrl))
            .setTitle(fileName)
            .setDescription("Downloading") // You can customize the description
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("Range")
    fun downloadFileWithMediaStore(context: Context, fileUrl: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(fileUrl))
            .setTitle(fileName)
            .setDescription("Downloading") // You can customize the description
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        // Get the download file's URI after download is complete
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                val downloadFileUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))
                saveFileToMediaStore(context, downloadFileUri, fileName)
            }
        }
        cursor.close()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileToMediaStore(context: Context, fileUri: Uri, fileName: String) {
        val contentResolver: ContentResolver = context.contentResolver

        // Create a new ContentValues instance to store the file details
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(fileUri))
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        // Insert the file into MediaStore
        val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        // Get the OutputStream of the MediaStore content to write the downloaded file
        uri?.let { mediaUri ->
            val outputStream = contentResolver.openOutputStream(mediaUri)
            val inputStream = contentResolver.openInputStream(fileUri)

            outputStream?.use { output ->
                inputStream?.use { input ->
                    input.copyTo(output)
                }
            }

            outputStream?.close()
            inputStream?.close()
        }
    }

    private fun getMimeType(uri: Uri): String {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)!!
    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransInfoAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = TranscationinfoBinding.inflate(inflater, parent, false)
        context = parent.context


        val sharedPreferences = context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: TransInfoAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size
    private val differCallback = object : DiffUtil.ItemCallback<TranscationInfoDetails.Data>(){
        override fun areItemsTheSame(oldItem: TranscationInfoDetails.Data, newItem: TranscationInfoDetails.Data): Boolean {
            return oldItem.bill_date == newItem.bill_date
        }

        override fun areContentsTheSame(oldItem: TranscationInfoDetails.Data, newItem: TranscationInfoDetails.Data): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun getItemViewType(position: Int): Int = position  // Shuffling need to be added


}