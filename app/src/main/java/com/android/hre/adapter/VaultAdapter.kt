package com.android.hre.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.Constants
import com.android.hre.R
import com.android.hre.databinding.TranscationinfoBinding
import com.android.hre.databinding.VaultinfodetBinding
import com.android.hre.response.transcationinfo.TranscationInfoDetails
import com.android.hre.response.vaults.VaultDetails
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale


private const val NOTIFICATION_ID_DOWNLOAD_PROGRESS = 1
private const val NOTIFICATION_ID_DOWNLOAD_COMPLETE = 2
private const val CHANNEL_ID_DOWNLOAD = "download_channel"

class VaultAdapter : RecyclerView.Adapter<VaultAdapter.ViewHolder>() {
    private lateinit var binding : VaultinfodetBinding
    private lateinit var context: Context
    var userid : String = ""
    val REQUEST_CODE_PERMISSION = 1001
    var manager: DownloadManager? = null


    inner class ViewHolder :RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n", "SuspiciousIndentation")
        fun bind(dataX: VaultDetails.Data?) {
            binding.apply {
                if (dataX != null){
                    tvnamee.text = dataX.name
                    tvType.text = dataX.type

                    if (dataX.filename.lowercase().contains(".jpg") || dataX.filename.lowercase().contains(".jpeg") || dataX.filename.lowercase().contains(".png") ||
                        dataX.filename.lowercase().contains(".gif")){
                        Glide.with(context)
                            .load(dataX.filepath+ dataX.filename)
                            .into(ivSavedFile)
                    }
                    else{
                        ivSavedFile.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.attach_svgrepo_com))
                    }


                    ivSavedFile.setOnClickListener {
                        if (dataX.filename.lowercase().contains(".jpg") || dataX.filename.lowercase().contains(".jpeg") || dataX.filename.lowercase().contains(".png") ||
                            dataX.filename.lowercase().contains(".gif")){
                            val dialog = Dialog(context)
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setContentView(R.layout.dialog_image_preview)
                            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            val previewImageView = dialog.findViewById<ImageView>(R.id.previewImageView)
                            val ivnotification = dialog.findViewById<ImageView>(R.id.iv_cancel)
                            Glide.with(context)
                                .load(dataX.filepath+ dataX.filename)
                                .into(previewImageView)
                            Log.v("Image",dataX.filepath+ dataX.filename)
                            ivnotification.setOnClickListener {
                                dialog.dismiss()
                            }
                            dialog.show()
                        }else{
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                downloadFileWithMediaStore(context,dataX.filepath+dataX.filename,dataX.filename)
                                Toast.makeText(context, "Downloading Suceesfuly", Toast.LENGTH_LONG).show()

                            }else{
                                downloadFilePreQ(context,dataX.filepath+dataX.filename,dataX.filename)
                                Toast.makeText(context, "Downloading Suceesfuly", Toast.LENGTH_LONG).show()

                            }

                        }
                    }

                }

            }
        }

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








    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaultAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = VaultinfodetBinding.inflate(inflater, parent, false)
        context = parent.context


        val sharedPreferences = context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: VaultAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size
    private val differCallback = object : DiffUtil.ItemCallback<VaultDetails.Data>(){
        override fun areItemsTheSame(oldItem: VaultDetails.Data, newItem: VaultDetails.Data): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: VaultDetails.Data, newItem: VaultDetails.Data): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun getItemViewType(position: Int): Int = position  // Shuffling need to be added

}