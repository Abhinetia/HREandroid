package com.android.hre

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.hre.adapter.AutoCompleteAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityCreateTicketBinding
import com.android.hre.response.createtccikets.TicketCreated
import com.android.hre.response.departement.GetDepartment
import com.android.hre.response.employee.EmployeeList
import com.android.hre.response.pcns.PCN
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


const val PERMISSION_REQUEST_CODE = 1001


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
    private val imageUriList = ArrayList<Uri>()

    private val imgList = ArrayList<File>()
    private val listOfImages = ArrayList<MultipartBody.Part>()
    var isSelectedText :Boolean = true





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        //  setContentView(R.layout.activity_create_ticket)

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

        binding.rvimage.setOnClickListener {

            if(imageUriList.size == 4){
                Toast.makeText(applicationContext, "Only you select 4 images.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            checkTheRunTimePermissions()

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

         // single
//            val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpg"), Imaagefile)
//            val image = MultipartBody.Part.createFormData("image", Imaagefile?.name, requestFile)
           // Log.v("TAG","imglist is $imgList")

            for (i in imgList){
                Log.v("TAG","imglist is $i")
                val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpg"), i)

               // val requestBody = RequestBody.create(MediaType.parse("image/jpg"), i)
                val image =   MultipartBody.Part.createFormData("image[$i]", Imaagefile?.name, requestFile)
                listOfImages.add(image)
            }
            Log.v("TAG","Multipartarray is $listOfImages")

            // adding the image to array list
//            val images: List<MultipartBody.Part> = imageFiles.mapIndexed { index, file ->
//                val requestBody = RequestBody.create(MediaType.parse("image/*"), Imaagefile)
//                MultipartBody.Part.createFormData("image[$index]", Imaagefile?.name, requestBody)
//            }

            val call = RetrofitClient.instance.uploadData(userId, pcn, priority, subject, issue,listOfImages)
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

    private fun selectImage() {
        val items = arrayOf<CharSequence>(
            "Take Photo", "Choose from Library",
            "Cancel"
        )
        val builder = AlertDialog.Builder(this@CreateTicketActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            if (items[item] == "Take Photo") {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 200)
            } else if (items[item] == "Choose from Library") {
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, pickImage)
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
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

                    for (i in 0 until listEmployeeData?.size!!) {
                        val dataString: EmployeeList.Data = listEmployeeData.get(i)

                        Log.v("log", i.toString())
                        listdata1.add(dataString.name  + " : " +   dataString.role)

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




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
//            binding.image.setImageURI(imageUri)
            file = imageUri?.let { getRealPathFromURI(it)?.let { File(it) } };
            compressAndSaveImage(file.toString(),50)

            // Added This Functionality
//            binding.ivImagecapture.isVisible = true
//            binding.ivImagecapture.setImageURI(imageUri)
//            binding.ivCamera.isVisible = false
            addImage("$imageUri")


        }
        if (resultCode == RESULT_OK && requestCode == 200 && data != null){
            //imageView.setImageBitmap(data.extras.get("data") as Bitmap)

            val photo = data.extras!!["data"] as Bitmap?
            if (photo!= null){
//                binding.ivImagecapture.setImageBitmap(photo)
//                binding.ivImagecapture.visibility = View.VISIBLE
               // binding.ivCamera.isVisible = false
              //  addImage("$photo")

            }
            imageUri = this?.let { getImageUri(it, photo!!) }
            file = imageUri?.let { getRealPathFromURI(it)?.let { File(it) } };
            compressAndSaveImage(file.toString(),50)

            addImage("$imageUri")


            Log.v("TAG","image path : $imageUri and $file")

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
    private fun compressAndSaveImage(imgage : String , quality : Int) {

        val compressedImagePath = createImageFile().absolutePath // Generate a new file path for the compressed image

        val quality = quality // Adjust the quality value as needed (0-100)

        try {
            val originalBitmap = BitmapFactory.decodeFile(imgage)
            val fileOutputStream = FileOutputStream(compressedImagePath)
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
            fileOutputStream.close()
            originalBitmap.recycle()
        } catch (e: IOException) {
            e.printStackTrace()
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
        imgList.add(Imaagefile!!)
        Toast.makeText(applicationContext, "Image Upload successful", Toast.LENGTH_LONG).show()

        return File(storageDir, imageFileName)
    }


/*
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


//                    val arrayAdapter =
//                        ArrayAdapter(this@CreateTicketActivity, R.layout.dropdwon_item, listdata)
//                    binding.etSelctpcn.setAdapter(arrayAdapter)

                    val arrayAdapter =
                        AutoCompleteAdapter(this@CreateTicketActivity, R.layout.dropdwon_item, listdata)
                    binding.etSelctpcn.setAdapter(arrayAdapter)

                     binding.etSelctpcn.setThreshold(1)
                    //  binding.etpcnId.threshold = 2
                    binding.etSelctpcn.setOnItemClickListener { adapterView, view, i, l ->
                        var data: PCN.Data = arrayList_details.get(i)
                        binding.carviewpcn.visibility = View.VISIBLE
                        binding.pcnClinet.text = data.brand
                        binding.pcnAddress.text = data.area + " " + data.city + " " + data.state
                       // binding.etSelctpcn.isEnabled = false

                    }
                }


            })


    }
*/

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
                        listdata.add(dataString.pcn)// + "-" + dataString.client_name + "-" + dataString.brand + "-" + dataString.location + "-" + dataString.area

                        //  listPCNdata.add(PCN.Data)   + "-" + dataString.city


                    }


//                    val arrayAdapter =
//                        ArrayAdapter(this@CaretingIndeNewActivity, R.layout.dropdwon_item, listdata)
                    val arrayAdapter =
                        AutoCompleteAdapter(this@CreateTicketActivity, R.layout.dropdwon_item, listdata)


                    binding.etSelctpcn.setAdapter(arrayAdapter)
                    binding.etSelctpcn.setThreshold(1)
                    arrayAdapter.notifyDataSetChanged()


                    //  binding.etpcnId.threshold = 2

//
                    binding.etSelctpcn.setOnItemClickListener { adapterView, view, i, l ->
                        var position : Int = listdata.indexOf(binding.etSelctpcn.text.toString())
                        var data: PCN.Data = arrayList_details.get(position)
                        isSelectedText = true
                        if (data.status.contains("Active")){
                            binding.carviewpcn.visibility = View.VISIBLE
                            binding.pcnClinet.text = data.brand
                            binding.pcnAddress.text =  data.area + " -" + data.city

                        } else if (data.status.contains("Completed")){
                            showAlertDialogOkAndCloseAfter("This PCN is Completed , Please contact your Super Admin for more information")
                        }
                    }
                    // myAutoComplete.addTextChangedListener(new CustomAutoCompleteTextChangedListener(this));
                    binding.etSelctpcn.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            charSequence: CharSequence,
                            i: Int,
                            i1: Int,
                            i2: Int
                        ) {
                        }

                        override fun onTextChanged(
                            charSequence: CharSequence,
                            i: Int,
                            i1: Int,
                            i2: Int
                        ) {
                            isSelectedText = false
                        }

                        override fun afterTextChanged(editable: Editable) {}
                    })


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
                        //  listdata2.add(dataString.category)

                        listdata2.add(dataString.department)
                    }
//                    val arrayAdapter =
//                        ArrayAdapter(this@CreateTicketActivity, R.layout.dropdwon_item, listdata2)

                    val arrayAdapter =
                        AutoCompleteAdapter(this@CreateTicketActivity, R.layout.dropdwon_item, listdata2)
                    binding.etTickettitle.setAdapter(arrayAdapter)
                    binding.etTickettitle.threshold = 1
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<GetDepartment>, t: Throwable) {
                // Handle network error
            }
        })
    }

    private fun addImage(image: String) {

        val infalot = LayoutInflater.from(this)
        val custrom = infalot.inflate(R.layout.addsingleandmultipleimage,null)

        val imageview = custrom.findViewById<ImageView>(R.id.iv_imagecapture)

        imageUriList.add(imageUri!!) // adding the image to the list
        imageview.setImageURI(imageUri) // setting the image view

        binding.btnCretaeticket.visibility= View.VISIBLE

        binding.linearLayoutGridLevelSinglePiece.addView(custrom)
    }

    private fun checkTheRunTimePermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // You have the required permissions, so you can proceed to capture the image.
            selectImage()
        } else {
            // If the user has denied the permissions previously, show an explanation.
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // Show an explanation to the user about why the permissions are needed.
                AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("We need camera and storage permissions to capture and save images.")
                    .setPositiveButton("OK") { _, _ ->
                        // Request the permissions again.
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ),
                            PERMISSION_REQUEST_CODE
                        )
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        // You can handle the cancel action here, e.g., show a message to the user.
                        Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()

                    }
                    .show()
            } else {
                // Request the permissions if it's the first time or "Never ask again" was checked.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size == 2 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                // Both permissions were granted, you can proceed to capture the image.
                selectImage()
            } else {

                checkTheRunTimePermissions()
                // Permission request was denied.
               // Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


}
