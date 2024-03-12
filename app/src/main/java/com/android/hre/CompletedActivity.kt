package com.android.hre

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.documentfile.provider.DocumentFile
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityCompletedBinding
import com.android.hre.databinding.ActivityViewTicketBinding
import com.android.hre.response.Completelist
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
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
    private val pickDocument = 200

    private var imageUri: Uri? = null
    private var file: File? = null
    private var Imaagefile: File? = null
    //private val imageUriList = java.util.ArrayList<Uri>()
    private val imageUriList = ArrayList<Uri>()
    private val imgList = ArrayList<File>()
    private val listOfImages = ArrayList<MultipartBody.Part>()



    companion object {
        const val CAMERA_PERMISSION_REQUEST = 101
        const val GALLERY_PERMISSION_REQUEST = 102

    }

    private val cameraPermission = Manifest.permission.CAMERA
    private val galleryPermission = Manifest.permission.READ_EXTERNAL_STORAGE
    private val galleryPermission1 = Manifest.permission.READ_MEDIA_IMAGES



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

            if(imgList.size == 4){  //imgList  imageUriList
                Toast.makeText(applicationContext, "Only you select 4 images.", Toast.LENGTH_LONG).show()
                return@setOnClickListener

            }
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            val currentVersion = Build.VERSION.SDK_INT

            // Now you can use the version to conditionally run different methods
            if (currentVersion >= Build.VERSION_CODES.TIRAMISU) {
                checkIfPermissionGrantedAndroid13()
            } else if (currentVersion >= Build.VERSION_CODES.O) {
                checkIfPermissionGranted()

            }

//            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//            startActivityForResult(gallery, pickImage)
//
            binding.linearsubmit.visibility = View.VISIBLE
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

//            if (listOfImages == null){
//                showAlertDialogOkAndCloseAfternotUplodad("Image required")
//                binding.rvimage.requestFocus()
//                return@setOnClickListener
//            }

            if (imgList.size == 0){
                showAlertDialogOkAndCloseAr("Image required","")
                binding.imageview.requestFocus()
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


            // Single Image From Gallery File path
//            val requestFile: RequestBody =
//                RequestBody.create(MediaType.parse("image/jpg"), Imaagefile)
//            val image =
//                MultipartBody.Part.createFormData("image", Imaagefile?.name, requestFile)

            // Multiple Image From Gallery File path
            for (i in imgList){
                Log.v("TAG","imglist is $i")
              //  val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpg"), i)

                val requestFile: RequestBody = createRequestBody(i);
                val image =   MultipartBody.Part.createFormData("image[$i]", getFileName(i), requestFile)
                listOfImages.add(image)
            }
            val call = RetrofitClient.instance.CompletTicket2(ticketid,ticketno,message,userId,actionCompl,listOfImages)

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

    fun getFileName(file: File): String {
        return file.name
    }

    fun  checkIfPermissionGrantedAndroid13(){
        if (ContextCompat.checkSelfPermission(this, cameraPermission) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, galleryPermission1) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(cameraPermission, galleryPermission1),
                UploadExpensesActivity.CAMERA_PERMISSION_REQUEST
            )
        } else {
            selectImage()
            // Camera and gallery permissions already granted
        }
    }
    fun  checkIfPermissionGranted(){
        if (ContextCompat.checkSelfPermission(this, cameraPermission) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, galleryPermission) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(cameraPermission, galleryPermission),
                UploadExpensesActivity.CAMERA_PERMISSION_REQUEST
            )
        } else {
            selectImage()
            // Camera and gallery permissions already granted
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == UploadExpensesActivity.CAMERA_PERMISSION_REQUEST || requestCode == UploadExpensesActivity.GALLERY_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, you can proceed with the actions
                selectImage()
            } else {
                // Permissions denied, handle accordingly (e.g., show a message)
                Toast.makeText(this,"Please allow all the permissions",Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun selectImage() {
        val items = arrayOf<CharSequence>(
            "Choose from Library",
            "Choose Document",
            "Cancel"
        ) //"Take Photo",
        val builder = AlertDialog.Builder(this@CompletedActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
//            if (items[item] == "Take Photo") {
//                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                startActivityForResult(cameraIntent, 200)
//            } else
            if (items[item] == "Choose from Library") {
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, pickImage)
            } else if (items[item] == "Choose Document"){
                val documentIntent = Intent(Intent.ACTION_GET_CONTENT)
                documentIntent.type = "*/*" // All file types
                startActivityForResult(documentIntent, pickDocument)
            }
            else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun showAlertDialogOkAndCloseAr(message:String, alertMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message + "\n"+ alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->
            setResult(Activity.RESULT_OK)
           // binding.tvPcn.requestFocus()
        }
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }



    private fun showAlertDialogOkAndCloseAfter(alertMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->
//            setResult(Activity.RESULT_OK)
            finish()
        }
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    private fun showAlertDialogOkAndCloseAfternotUplodad(alertMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->
//            setResult(Activity.RESULT_OK)

        }
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }



   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
//            binding.image.setImageURI(imageUri)
            file = imageUri?.let { getRealPathFromURI(it)?.let { File(it) } };
            compressAndSaveImage(file.toString(),50)
//            binding.imageview.isVisible = true
//            binding.imageview.setImageURI(imageUri)

            binding.frameimage.isVisible = true
            binding.ivImagecapture.setImageURI(imageUri)
            binding.rvimage.isVisible = false


        }
        binding.imageViewClose.setOnClickListener {
            binding.ivImagecapture.setImageResource(0)
            binding.rvimage.visibility = View.VISIBLE
            binding.frameimage.visibility = View.GONE
            binding.linearsubmit.visibility = View.GONE
            Toast.makeText(this,"Image Removed", Toast.LENGTH_SHORT).show()
        }

    }*/


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                pickImage -> {
                    // Handle image selection
                    imageUri = data?.data
                    file = imageUri?.let { getRealPathFromURI(it)?.let { File(it) } }
                    val fileValue = compressAndSaveImage(file.toString(), 50)
                    addImage(imageUri!!, fileValue)
                }
                pickDocument -> {
                    val selectedFileUri = data?.data
                    selectedFileUri?.let { uri ->
                        val documentFile = DocumentFile.fromSingleUri(this, uri)
                        val selectedFilePath = documentFile?.uri?.toString()
                        selectedFilePath?.let {
                            val selectedFile = getFileFromUri(this,selectedFileUri)
                            Log.v("FileName", "$selectedFile")
                            addImage(selectedFileUri, selectedFile!!)
                        }
                    }
                }
            }
        }
       /* if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            file = imageUri?.let { getRealPathFromURI(it)?.let { File(it) } };
            val fileValue = compressAndSaveImage(file.toString(),50)


            addImage("$imageUri",fileValue)


        }
        if (resultCode == RESULT_OK && requestCode == 200 && data != null){

            val photo = data.extras!!["data"] as Bitmap?
            if (photo!= null){
//

            }
            imageUri = this?.let { getImageUri(it, photo!!) }
            file = imageUri?.let { getRealPathFromURI(it)?.let { File(it) } };
            val fileValue =   compressAndSaveImage(file.toString(),50)

            addImage("$imageUri",fileValue)


            Log.v("TAG","image path : $imageUri and $file")


    }*/
    }
    fun createRequestBody(file: File): RequestBody {
        // Determine the media type based on the file extension or type
        val mediaType = when (file.extension.toLowerCase()) {
            "pdf" -> "application/pdf"
            "doc", "docx" -> "application/msword"
            "xls", "xlsx" -> "application/vnd.ms-excel"
            "ppt", "pptx" -> "application/vnd.ms-powerpoint"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            // Add more cases for other document types as needed
            else -> "application/octet-stream" // Default to binary data
        }
        // Create the RequestBody using the determined media type
        return RequestBody.create(MediaType.parse(mediaType), file)
    }
    fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        var file: File? = null
        inputStream?.use { input ->
            val fileExtension = getFileExtension(contentResolver, uri)
            val fileName = "file_${System.currentTimeMillis()}.$fileExtension"
            val outputFile = File(context.cacheDir, fileName)
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
            file = outputFile
        }
        return file
    }
    fun getFileExtension(contentResolver: ContentResolver, uri: Uri): String {
        val mimeType = contentResolver.getType(uri)
        return if (mimeType != null) {
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: ""
        } else {
            uri.path?.substringAfterLast('.') ?: ""
        }
    }
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(), inImage,
            "Title", null
        )
        return Uri.parse(path)
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

    private fun addImage(uri: Uri,fileValue : File) {

        val infalot = LayoutInflater.from(this)
        val custrom = infalot.inflate(R.layout.addsingleandmultipleimage,null)

        val imageview = custrom.findViewById<ImageView>(R.id.iv_imagecapture)
        val icclose = custrom.findViewById<ImageView>(R.id.imageView_close)
        val linear = custrom.findViewById<LinearLayout>(R.id.linear)

        Log.v("ImahgeBe","$imageUriList")


        imageUriList.add(uri) // adding the image to the list
        imgList.add(fileValue)


        val mimeType = getMimeType(this, uri)
        if (isImageMimeType(mimeType)) {
            imageview.setImageURI(uri) // setting the image view
        } else if (isPdfMimeType(mimeType)) {
            imageview.setImageResource(R.drawable.pdf_ic)
        }
        icclose.setOnClickListener {
            imageview.setImageResource(0)
            icclose.setImageResource(0)
//            imageUriList.removeAt(1)
//            imgList.removeAt(1)

            if(imgList.contains(fileValue)){
                imgList.remove(fileValue)
            }

            binding.linearLayoutGridLevelSinglePiece.removeView(custrom)


            // imageUriList.clear()
            linear.visibility = View.GONE
            icclose.visibility = View.GONE
            Toast.makeText(this, "Image Removed", Toast.LENGTH_SHORT)
                .show()  // 1000042116  1000042116
            Log.v("Imahge", "$imageUriList")
        }

        binding.linearLayoutGridLevelSinglePiece.addView(custrom)


    }
    fun getMimeType(context: Context, uri: Uri): String? {
        val contentResolver: ContentResolver = context.contentResolver
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            contentResolver.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase())
        }
    }

    fun isImageMimeType(mimeType: String?): Boolean {
        return mimeType?.startsWith("image/") == true
    }

    fun isPdfMimeType(mimeType: String?): Boolean {
        return mimeType == "application/pdf"
    }


    private fun compressAndSaveImage(imgage : String , quality : Int) :File {

        val file : File = createImageFile()

        val compressedImagePath = file.absolutePath // Generate a new file path for the compressed image

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
        return file
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
        //imgList.add(Imaagefile!!)
        //return File(storageDir, imageFileName)
        return Imaagefile as File
    }

}