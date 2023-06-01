package com.android.hre

import android.app.Activity
import android.app.Dialog
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
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityCaretingIndeNewBinding
import com.android.hre.databinding.ActivityUploadExpensesBinding
import com.android.hre.databinding.FragmentPettyCashBinding
import com.android.hre.response.createtccikets.TicketCreated
import com.bumptech.glide.Glide
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

class UploadExpensesActivity : AppCompatActivity() {

    private lateinit var binding :ActivityUploadExpensesBinding
    var userid :String = ""
    var username :String = ""
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var file: File? = null
    private var Imaagefile: File? = null
    var pettycashid :String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_upload_expenses)

        supportActionBar?.hide()
        binding = ActivityUploadExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        username = sharedPreferences?.getString("username", "")!!

        pettycashid = intent.getStringExtra("PettyCashId")!!

        binding.tiNamedispaly.setText(username.capitalize())

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.imageview1.visibility = View.GONE
        binding.imageview.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        binding.imageview1.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_image_preview)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val previewImageView = dialog.findViewById<ImageView>(R.id.previewImageView)
            val ivnotification = dialog.findViewById<ImageView>(R.id.iv_cancel)
            Glide.with(this)
                .load(imageUri)
                .into(previewImageView)

            ivnotification.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.btnMaterials.setOnClickListener {
            val userId = RequestBody.create(MediaType.parse("text/plain"), userid)
            val pettycashid = RequestBody.create(MediaType.parse("text/plain"),pettycashid )
            val spentamount = RequestBody.create(MediaType.parse("text/plain"), binding.tvamount.text.toString())  // extra added priority
            val comment = RequestBody.create(MediaType.parse("text/plain"), binding.etDescrtiption.text.toString())

            val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpg"), Imaagefile)
            val image = MultipartBody.Part.createFormData("bill", Imaagefile?.name, requestFile)
            val call = RetrofitClient.instance.uploadPettycashBill(userId, pettycashid, spentamount, comment,image)

            call.enqueue(object : retrofit2.Callback<TicketCreated> {
                override fun onResponse(call: Call<TicketCreated>, response: Response<TicketCreated>) {
                    Log.v("TAG", response.body().toString())
                    Log.v("TAG","message "+ response.body()?.message.toString())
                    showAlertDialogOkAndCloseAfter(response.body()?.message.toString())

                }

                override fun onFailure(call: Call<TicketCreated>, t: Throwable) {

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
            finish() }
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
//            binding.image.setImageURI(imageUri)
            file = imageUri?.let { getRealPathFromURI(it)?.let { File(it) } };
            Log.v("TAAAAAAAAAAG","$file")
            compressAndSaveImage(file.toString(),50)
                                                                            
            // Added This Functionality
            binding.imageview1.setImageURI(imageUri)
            binding.imageview1.visibility = View.VISIBLE
            binding.imageview.visibility = View.GONE

        }
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