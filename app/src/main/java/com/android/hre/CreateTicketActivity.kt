package com.android.hre

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityCreateTicketBinding
import com.android.hre.response.createtccikets.TicketCreated
import com.android.hre.response.departement.GetDepartment
import com.android.hre.response.employee.EmployeeList
import com.android.hre.response.pcns.PCN
import com.bumptech.glide.Glide
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CreateTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTicketBinding
    var userid: String = ""
    private val pickImage = 100
    private var imageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1000
    val listdata: ArrayList<String> = arrayListOf()
    val listdata1: ArrayList<String> = arrayListOf()
    val listdata2 :ArrayList<String> = arrayListOf()
    var listEmployeeData: ArrayList<EmployeeList.Data> = arrayListOf()
    var listDepartmetData :ArrayList<GetDepartment.Data> = arrayListOf()

    var receiptEmployee: Int? = null
    val REQUEST_CODE = 100
    private var file: File? = null
    private var Imaagefile: File? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        //  setContentView(R.layout.activity_create_ticket)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
//            // If the app doesn't have permission, ask the user to grant it
//            //val intent = Intent(Settings.ACTION_PERMISSION.READ_EXTERNAL_STORAGE)
//            val uri = Uri.fromParts("package", packageName, null)
//            intent.data = uri
//            startActivity(intent)
//        } else {
//            // Permission is granted or device is not running Android 11 or higher
//            // Do your work here, such as capturing an image from the camera or selecting an image from the gallery
//        }

        binding = ActivityCreateTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!

        dropdwonfromServer()

        dropdownEmployeeDetails()

        dropdownDepartmentDetails()


        val month = resources.getStringArray(R.array.priority)
        val arrayAdapter = ArrayAdapter(this,R.layout.dropdwon_item,month)
        binding.etPriority.setAdapter(arrayAdapter)
        binding.etPriority.setOnClickListener {
            binding.etPriority.showDropDown()
        }


        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivCamera.setOnClickListener {
//
//            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)

//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "image/*"
//            startActivityForResult(intent, REQUEST_CODE)
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)

//
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.etSelctpcn.setOnClickListener {
            dropdwonfromServer()
        }

        binding.ivimageuploadq.setOnClickListener {
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

//        binding.etSelctrecepitent.setOnItemClickListener { adapterView, view, i, l ->
//            receiptEmployee = listEmployeeData.get(i).recipient
//        }

        binding.btnCretaeticket.setOnClickListener {

            val comment =  binding.etDescrtiption.text.toString()
            val pcnData = binding.etSelctpcn.text.toString()
            val prior = binding.etPriority.text.toString()
            val departm = binding.etTickettitle.text.toString()

            if (comment.isEmpty()){
                binding.etDescrtiption.error = "Please Enter The Description"
                binding.etDescrtiption.requestFocus()
                return@setOnClickListener
            }

            if (pcnData.isEmpty()){
                binding.etSelctpcn.error = "Please Select The PCN"
                binding.etSelctpcn.requestFocus()
                return@setOnClickListener
            }
            if (prior.isEmpty()){
                binding.etPriority.error = "Please Enter The Description"
                binding.etPriority.requestFocus()
                return@setOnClickListener
            }

            if (departm.isEmpty()){
                binding.etTickettitle.error = "Please Select The Department"
                binding.etTickettitle.requestFocus()
                return@setOnClickListener
            }


            val userId = RequestBody.create(MediaType.parse("text/plain"), userid)
            val pcn = RequestBody.create(MediaType.parse("text/plain"), binding.etSelctpcn.text.toString())
            val priority = RequestBody.create(MediaType.parse("text/plain"), binding.etPriority.text.toString())  // extra added priority
            val subject = RequestBody.create(MediaType.parse("text/plain"), binding.etTickettitle.text.toString())
            val issue = RequestBody.create(MediaType.parse("text/plain"), binding.etDescrtiption.text.toString())
           // val recipient = RequestBody.create(MediaType.parse("text/plain"), "$receiptEmployee")

            val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpg"), Imaagefile)
            val image = MultipartBody.Part.createFormData("image", Imaagefile?.name, requestFile)
            val call = RetrofitClient.instance.uploadData(userId, pcn, priority, subject, issue,image)
            Log.v("TAG", "$receiptEmployee")

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

    private fun dropdownEmployeeDetails() {
        val call =  RetrofitClient.instance.getEmployee(userid)
        call.enqueue(object : Callback<EmployeeList> {
            override fun onResponse(call: Call<EmployeeList>, response: Response<EmployeeList>) {
                if (response.isSuccessful) {
                  //  val myDataList = response.body()?.data

                    var listMaterials: EmployeeList? = response.body()
                    listdata1.clear()
                    listEmployeeData.clear()


                    listEmployeeData = listMaterials?.data as ArrayList<EmployeeList.Data>

                    //  listdata.add("ewfwef")
                    for (i in 0 until listEmployeeData?.size!!) {
                        val dataString: EmployeeList.Data = listEmployeeData.get(i)

                        Log.v("log", i.toString())
                        listdata1.add(dataString.name  + " : " +   dataString.role)

                        //  listPCNdata.add(PCN.Data)

                    }



                    val arrayAdapter =
                        ArrayAdapter(this@CreateTicketActivity, R.layout.dropdwon_item, listdata1)
                   // binding.etSelctrecepitent.setAdapter(arrayAdapter)

                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<EmployeeList>, t: Throwable) {
                // Handle network error
            }
        })
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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
//
//            val selectedImage = data?.data
//           // val inputStream = contentResolver.openInputStream(selectedImage!!)
//            var uri = selectedImage?.let { getRealPathFromURI(it) };
//            var file = File(uri)
//           /* val outputStream = FileOutputStream(file)
//            inputStream?.copyTo(outputStream)*/
//            Log.v("TAG","uri $file")
//
//            val requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), file)
//           // val image = MultipartBody.Part.createFormData("image", file.name, requestFile)
//            val requestFile2: RequestBody =
//                RequestBody.create(MediaType.parse("multipart/form-data"), file)
//
//           // val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
//
//
////            val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
////            val body : MultipartBody.Part= MultipartBody.Part.createFormData("image", file.name, reqFile)
//
//            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
//            val image = MultipartBody.Part.createFormData("image", file.name, requestFile)
//
//
//
//
//
//            val userId = RequestBody.create(MediaType.parse("text/plain"), userid)
//            val pcn = RequestBody.create(MediaType.parse("text/plain"), binding.etSelctpcn.text.toString())
//            val indentNo = RequestBody.create(MediaType.parse("text/plain"), "your-indent-no")
//            val subject = RequestBody.create(MediaType.parse("text/plain"), binding.etTickettitle.text.toString())
//            val issue = RequestBody.create(MediaType.parse("text/plain"), binding.etDescrtiption.text.toString())
//            val recipient = RequestBody.create(MediaType.parse("text/plain"), receiptEmployee.toString())
//
//
//            val call = RetrofitClient.instance.uploadData(userId, pcn, indentNo, subject, issue, image, recipient)
//            call.enqueue(object : Callback<TicketCreated> {
//                override fun onResponse(call: Call<TicketCreated>, response: Response<TicketCreated>) {
//                    Log.v("Data",response.body().toString())
//
//                    if (response.isSuccessful && response.body() != null && response.body()?.status == 1) {
//
//                        // Show a success message to the user
//                       // Log.v("Data",call.toString())
//                    Toast.makeText(this@CreateTicketActivity, "Ticket created successfully", Toast.LENGTH_SHORT).show()
//                } else {
//                    // Show an error message to the user
//                    Toast.makeText(this@CreateTicketActivity, "Error: " + response.code(), Toast.LENGTH_SHORT).show()
//                }
//                }
//
//                override fun onFailure(call: Call<TicketCreated>, t: Throwable) {
//                    // Handle the error
//                }
//            })
//        }
//
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
//            binding.image.setImageURI(imageUri)
            file = imageUri?.let { getRealPathFromURI(it)?.let { File(it) } };
            compressAndSaveImage(file.toString(),50)

            // Added This Functionality
            binding.ivimageuploadq.isVisible = true
            binding.ivimageuploadq.setImageURI(imageUri)
            binding.ivCamera.isVisible = false

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

    // Function to create a new image file with a unique name in the app's external storage directory
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


    private fun dropdwonfromServer() {

        RetrofitClient.instance.getAllPcns(userid)
            .enqueue(object: retrofit2.Callback<PCN> {
                override fun onFailure(call: Call<PCN>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<PCN>, response: Response<PCN>) {
                    Log.v("Sucess", response.body().toString())
                    var listMaterials: PCN? = response.body()
                    listdata.clear()

                    var arrayList_details: List<PCN.Data>? = listMaterials?.data

                    //  listdata.add("ewfwef")
                    for (i in 0 until arrayList_details?.size!!) {
                        val dataString: PCN.Data = arrayList_details.get(i)

                        Log.v("log", i.toString())
                        listdata.add(dataString.pcn)
                        //  listPCNdata.add(PCN.Data)

                    }


                    val arrayAdapter =
                        ArrayAdapter(this@CreateTicketActivity, R.layout.dropdwon_item, listdata)
                    binding.etSelctpcn.setAdapter(arrayAdapter)
                    // binding.etpcnId.setThreshold(2)
                    //  binding.etpcnId.threshold = 2

                }


            })


    }

    private fun showAlertDialogOkAndCloseAfter(alertMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->
            setResult(Activity.RESULT_OK)
            finish() }
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }


    private fun dropdownDepartmentDetails() {
        val call =  RetrofitClient.instance.getdepartment(userid)
        call.enqueue(object : Callback<GetDepartment> {
            override fun onResponse(call: Call<GetDepartment>, response: Response<GetDepartment>) {
                if (response.isSuccessful) {
                    //  val myDataList = response.body()?.data

                    var listMaterials: GetDepartment? = response.body()
                    listdata2.clear()
                    listDepartmetData.clear()


                    listDepartmetData = listMaterials?.data as ArrayList<GetDepartment.Data>

                    //  listdata.add("ewfwef")
                    for (i in 0 until listDepartmetData?.size!!) {
                        val dataString: GetDepartment.Data = listDepartmetData.get(i)

                        Log.v("log", i.toString())
                        listdata2.add(dataString.category)

                        //  listPCNdata.add(PCN.Data)

                    }



                    val arrayAdapter =
                        ArrayAdapter(this@CreateTicketActivity, R.layout.dropdwon_item, listdata2)
                    binding.etTickettitle.setAdapter(arrayAdapter)


                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<GetDepartment>, t: Throwable) {
                // Handle network error
            }
        })
    }

}
