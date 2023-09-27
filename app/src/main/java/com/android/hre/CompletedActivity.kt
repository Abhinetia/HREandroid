package com.android.hre

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityCompletedBinding
import com.android.hre.databinding.ActivityViewTicketBinding
import com.android.hre.response.Completelist
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CompletedActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCompletedBinding
    var ticketno :String = ""
    var status :String = ""
    var userid :String = ""
    var ticketid :String = ""
    var subject :String = ""
    var username :String = ""
    var action :String = ""
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var file: File? = null
    private var Imaagefile: File? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_completed)

        supportActionBar?.hide()

        binding = ActivityCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences =getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        username = sharedPreferences?.getString("username","")!!

        val position  = intent.getIntExtra("pos",0);


        val intentUser = intent
        ticketno = intentUser!!.getStringExtra("TicketNo").toString()
        status = intentUser.getStringExtra("Stauts").toString()
        subject = intentUser.getStringExtra("Subject").toString()
        //ticketid = intentUser.getStringExtra("ticketid")
        ticketid = intentUser.getStringExtra("TicketId").toString()
        action = intentUser.getStringExtra("Action").toString()


        binding.tvSubject.text = subject
        binding.tvTno.text = ticketno
        binding.tvStatus.text = status

        binding.rvimage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        binding.tvCancel.setOnClickListener {
                onBackPressed()
        }

        binding.tvUpdate.setOnClickListener {
            if (binding.description.text.toString().isEmpty()) {
                binding.description.error = "Description required"
                binding.description.requestFocus()
                return@setOnClickListener
            }
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Data is Completing")
            progressDialog.setMessage("Please wait...")
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()


            val ticketid = RequestBody.create(MediaType.parse("text/plain"), ticketid)
            val ticketno = RequestBody.create(MediaType.parse("text/plain"), ticketno)
            val message = RequestBody.create(MediaType.parse("text/plain"), binding.description.text.toString())
            val userId = RequestBody.create(MediaType.parse("text/plain"), userid)
            val actionCompl = RequestBody.create(MediaType.parse("text/plain"), action)


            // Image From Gallery File path
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("image/jpg"), Imaagefile)
            val image =
                MultipartBody.Part.createFormData("image", Imaagefile?.name, requestFile)
            val call = RetrofitClient.instance.CompletTicket(ticketid,ticketno,message,userId,actionCompl,image)

            call.enqueue(object : retrofit2.Callback<Completelist> {
                override fun onResponse(
                    call: Call<Completelist>,
                    response: Response<Completelist>
                ) {
                    progressDialog.dismiss()
                    Log.v("TAG", response.body().toString())
                    Log.v("TAG", "message " + response.body()?.message.toString())
                    showAlertDialogOkAndCloseAfter(response.body()?.message.toString())

                }

                override fun onFailure(call: Call<Completelist>, t: Throwable) {
                    progressDialog.dismiss()
                    Log.v("TAG", t.toString())
                }

            })
    }

    }

    private fun showAlertDialogOkAndCloseAfter(alertMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->
//            setResult(Activity.RESULT_OK)
            val intent2=  Intent()
            setResult(Activity.RESULT_OK,intent2)
            finish()
        }
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
//            binding.image.setImageURI(imageUri)
            file = imageUri?.let { getRealPathFromURI(it)?.let { File(it) } };
            compressAndSaveImage(file.toString(),50)
            binding.imageview.isVisible = true
            binding.imageview.setImageURI(imageUri)
            binding.rvimage.isVisible = false


        }
    }
    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor = getContentResolver()?.query(contentURI, null, null, null, null)!!
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }
    private fun compressAndSaveImage(imgage : String , quality : Int) {

        val compressedImagePath = createImageFile().absolutePath // Generate a new file path for the compressed image

        val quality = quality // Adjust the quality value as needed (0-100)

        try {
            // Create a bitmap from the original image file
            val originalBitmap = BitmapFactory.decodeFile(imgage)

            // Create a new FileOutputStream to write the compressed bitmap to the file
            val fileOutputStream = FileOutputStream(compressedImagePath)

            // Compress the bitmap with the specified quality into the FileOutputStream
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)

            // Close the FileOutputStream
            fileOutputStream.close()

            // Recycle the bitmap to free up memory
            originalBitmap.recycle()

            // Image compression and saving completed successfully
            // You can now use the compressed image file as needed
        } catch (e: IOException) {
            e.printStackTrace()
            // Error occurred while compressing or saving the image
            // Handle the error accordingly
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Get the timestamp to use as part of the image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        // Create a directory for storing the compressed images (if it doesn't exist)
        val storageDir = File(Environment.getExternalStorageDirectory(), "Documents")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        // Create a new file in the directory with a unique name
        val imageFileName = "IMG_$timeStamp.jpg"
        Imaagefile=File(storageDir, imageFileName)

        return File(storageDir, imageFileName)
    }

}