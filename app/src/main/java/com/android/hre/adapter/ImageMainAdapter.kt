package com.android.hre.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.R
import com.android.hre.api.RetrofitClient
import com.android.hre.response.newvault.NewVaultDetiailsMainFolder
import com.bumptech.glide.Glide
import java.io.File

class ImageMainAdapter (private val mList: List<NewVaultDetiailsMainFolder.DataX> , val context :Context) : RecyclerView.Adapter<ImageMainAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vaultinfodet, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        holder.textviewImageName.text = ItemsViewModel.name
        holder.textViewImageType.text = ItemsViewModel.type

        //val url = RetrofitClient.BASE_URL.replace("api/","");

       // val url = "https://hre.netiapps.com/"

        val url = "https://admin.hresolutions.in/"

        if (ItemsViewModel.filename.lowercase().contains(".jpg") || ItemsViewModel.filename.lowercase().contains(".jpeg") || ItemsViewModel.filename.lowercase().contains(".png") ||
            ItemsViewModel.filename.lowercase().contains(".gif")){
            Glide.with(context)
                .load(url + ItemsViewModel.folder +"/"+ ItemsViewModel.filename)
                .into(holder.imageView)
        }
        else{
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_download))
        }

        holder.imageView.setOnClickListener {
            if (ItemsViewModel.filename.lowercase().contains(".jpg") || ItemsViewModel.filename.lowercase().contains(".jpeg") || ItemsViewModel.filename.lowercase().contains(".png") ||
                ItemsViewModel.filename.lowercase().contains(".gif")){
                val dialog = Dialog(context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_image_preview)
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val previewImageView = dialog.findViewById<ImageView>(R.id.previewImageView)
                val ivnotification = dialog.findViewById<ImageView>(R.id.iv_cancel)
                previewImageView.visibility = View.VISIBLE
                Glide.with(context)
                    .load(url + ItemsViewModel.folder+ "/" + ItemsViewModel.filename)
                    .into(previewImageView)
                Log.v("Image",url + ItemsViewModel.folder+ "/"+ ItemsViewModel.filename)
                ivnotification.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    downloadFileWithMediaStore(context,url +ItemsViewModel.folder+ "/"+ItemsViewModel.filename,ItemsViewModel.filename)
                    Toast.makeText(context, "Downloading Suceesfuly", Toast.LENGTH_LONG).show()

                }else{
                    downloadFilePreQ(context,url +ItemsViewModel.folder+"/"+ItemsViewModel.filename,ItemsViewModel.filename)
                    Toast.makeText(context, "Downloading Suceesfuly", Toast.LENGTH_LONG).show()

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

        val destination = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        request.setDestinationUri(Uri.fromFile(destination))

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
                val downloadFileUri = Uri.parse(cursor.getString(cursor.getColumnIndex(
                    DownloadManager.COLUMN_LOCAL_URI)))
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

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textviewImageName: TextView = itemView.findViewById(R.id.tvnamee)
        val textViewImageType: TextView = itemView.findViewById(R.id.tv_type)
        val imageView: ImageView = itemView.findViewById(R.id.iv_saved_file)


    }
}