package com.android.hre

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.hre.adapter.AutoCompleteAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityUploadExpensesBinding
import com.android.hre.response.createtccikets.TicketCreated
import com.android.hre.response.pcns.PCN
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
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
    val listdata: ArrayList<String> = arrayListOf()
    var selectedItem :String = ""
    private val imageUriList = ArrayList<Uri>()
    private val imgList = ArrayList<File>()
    private val listOfImages = ArrayList<MultipartBody.Part>()
    var isSelectedText :Boolean = true

        companion object {
            const val CAMERA_PERMISSION_REQUEST = 101
            const val GALLERY_PERMISSION_REQUEST = 102

        }

    private val cameraPermission = Manifest.permission.CAMERA
    private val galleryPermission = Manifest.permission.READ_EXTERNAL_STORAGE
    private val galleryPermission1 = Manifest.permission.READ_MEDIA_IMAGES


    private lateinit var handler: Handler
    private var timerRunnable : Runnable? = null
    private val delayMillis = 1500L



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_upload_expenses)

        supportActionBar?.hide()
        binding = ActivityUploadExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        username = sharedPreferences?.getString("username", "")!!

     //   pettycashid = intent.getStringExtra("PettyCashId")!!

        binding.tiNamedispaly.setText(username.capitalize())

        binding.ivBack.setOnClickListener {
            finish()
        }

        dropdwonfromServer()



        val month = resources.getStringArray(R.array.purpose)
        val arrayAdapter = ArrayAdapter(this,R.layout.dropdwon_item,month)
        binding.tvPurpose.setAdapter(arrayAdapter)
        binding.tvPurpose.setOnClickListener {
            binding.tvPurpose.showDropDown()
        }

        binding.tvPurpose.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            selectedItem = parent.getItemAtPosition(position).toString()

            if (selectedItem == "Purchase") {
                binding.linearlayoutpcn.visibility = View.VISIBLE
            } else {
                binding.linearlayoutpcn.visibility = View.GONE
                binding.carviewpcn.visibility = View.GONE
                binding.tvPcn.setText("")
                binding.pcnClinet.setText("")
                binding.pcnAddress.setText("")
            }
        }



        binding.tvDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth, selectedDay)

                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    binding.tvDate.setText(formattedDate)
                },
                year,
                month,
                dayOfMonth
            )

            datePickerDialog.show()
            datePickerDialog.getDatePicker().setMaxDate(Date().time)
//            datePickerDialog.datePicker.maxDate(System.currentTimeMillis())

        }

      //  binding.imageview1.visibility = View.GONE
        binding.imageview.setOnClickListener {
            if(imgList.size == 4){  //imgList  imageUriList
                Toast.makeText(applicationContext, "Only you select 4 images.", Toast.LENGTH_LONG).show()
                return@setOnClickListener

            }
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            //selectImage()

            val currentVersion = Build.VERSION.SDK_INT

            // Now you can use the version to conditionally run different methods
            if (currentVersion >= Build.VERSION_CODES.TIRAMISU) {
                checkIfPermissionGrantedAndroid13()
            } else if (currentVersion >= Build.VERSION_CODES.O) {
                checkIfPermissionGranted()
                // Run methods for versions earlier than Android 9.0
                // For example:
                // yourMethodForVersionsBeforePie()
            }


//            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//            startActivityForResult(gallery, pickImage)
        }

      /*  binding.imageview1.setOnClickListener {
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
        }*/

        binding.btnMaterials.setOnClickListener {



           // binding.btnMaterials.visibility = View.GONE

            val  amount = binding.tvamount.text.toString()
            val comm = binding.etDescrtiption.text.toString()
            val purpseFrom = binding.tvPurpose.text.toString()
            val date = binding.tvDate.text.toString()
            val pcndata = binding.tvPcn.text.toString()
            if (amount.isEmpty()){
                binding.tvamount.error = "Amount is required"
                binding.tvamount.requestFocus()
                return@setOnClickListener
            }
            if (comm.isEmpty()){
                binding.etDescrtiption.error = "Comment is required"
                binding.etDescrtiption.requestFocus()
                return@setOnClickListener
            }
            if (purpseFrom.isEmpty()){
                binding.tvPurpose.error = "Please Select Purpose From Drop Down"
                binding.tvPurpose.requestFocus()
                return@setOnClickListener
            }
            if (date.isEmpty()){
                binding.tvDate.error = "Please Select The Date"
                binding.tvDate.requestFocus()
                return@setOnClickListener
            }
            if (selectedItem.equals("Purchase")){
                if (pcndata.isEmpty()){
                    binding.tvPcn.error = "Please Enter The PCN"
                    binding.tvPcn.requestFocus()
                    return@setOnClickListener
                }

            }

            if (imgList.size == 0){
                showAlertDialogOkAndCloseAr("Image required","")
                binding.imageview.requestFocus()
                return@setOnClickListener
            }

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading Data")
            progressDialog.setMessage("Please wait...")
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()



            val initDate = SimpleDateFormat("dd-MM-yyyy").parse(binding.tvDate.text.toString())
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val parsedDate = formatter.format(initDate)
            println(parsedDate)

            val userId = RequestBody.create(MediaType.parse("text/plain"), userid)
            val billnumber = RequestBody.create(MediaType.parse("text/plain"),binding.tvBillnumber.text.toString() )
            val spentamount = RequestBody.create(MediaType.parse("text/plain"), binding.tvamount.text.toString()) // amount
            val comment = RequestBody.create(MediaType.parse("text/plain"), binding.etDescrtiption.text.toString())
            val billDate = RequestBody.create(MediaType.parse("text/plain"),parsedDate)  //ddate
            val purpose = RequestBody.create(MediaType.parse("text/plain"),binding.tvPurpose.text.toString())
            val pcn = RequestBody.create(MediaType.parse("text/plain"),binding.tvPcn.text.toString())

//            val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpg"), Imaagefile)
//            val image = MultipartBody.Part.createFormData("bill", Imaagefile?.name, requestFile)


            for (i in imgList){
                Log.v("TAG","imglist is $i")
                val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpg"), i)
                val image =   MultipartBody.Part.createFormData("image[$i]", Imaagefile?.name, requestFile)
                listOfImages.add(image)
            }
            val call = RetrofitClient.instance.uploadPettycashBill(userId, billnumber, spentamount, comment,billDate,purpose,pcn,listOfImages)

            call.enqueue(object : retrofit2.Callback<TicketCreated> {
                override fun onResponse(call: Call<TicketCreated>, response: Response<TicketCreated>) {
                    progressDialog.dismiss()
                    Log.v("TAG", response.body().toString())
                    Log.v("TAG","message "+ response.body()?.message.toString())
                   // showAlertDialogOkAndCloseAfter(response.body()?.message.toString())
                    if (response.body()?.message.toString().contains("Success")){
                        showAlertDialogOkAndCloseAftersucees("Bill Upload Sucessful")
                    } else {
                        showAlertDialogOkAndCloseAftersucees(response.body()?.message.toString())

                    }

                   // "Bill Upload Sucessful"

                }

                override fun onFailure(call: Call<TicketCreated>, t: Throwable) {
                    progressDialog.dismiss()
                    Log.v("TAG", t.toString())
                }

            })
        }

    }

    fun  checkIfPermissionGrantedAndroid13(){
        if (ContextCompat.checkSelfPermission(this, cameraPermission) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, galleryPermission1) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(cameraPermission, galleryPermission1), CAMERA_PERMISSION_REQUEST)
        } else {
            selectImage()
            // Camera and gallery permissions already granted
        }
    }

    fun  checkIfPermissionGranted(){
        if (ContextCompat.checkSelfPermission(this, cameraPermission) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, galleryPermission) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(cameraPermission, galleryPermission), CAMERA_PERMISSION_REQUEST)
        } else {
            selectImage()
            // Camera and gallery permissions already granted
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST || requestCode == GALLERY_PERMISSION_REQUEST) {
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
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        val items = arrayOf<CharSequence>(
             "Choose from Library",
            "Cancel"
        ) // "Take Photo",
        val builder = AlertDialog.Builder(this@UploadExpensesActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
//            if (items[item] == "Take Photo") {
////                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
////                startActivityForResult(cameraIntent, 200)
//            } else
            if (items[item] == "Choose from Library") {
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, pickImage)
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
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
//                        ArrayAdapter(this@UploadExpensesActivity, R.layout.dropdwon_item, listdata)

                    val arrayAdapter =
                        AutoCompleteAdapter(this@UploadExpensesActivity, R.layout.dropdwon_item, listdata)


                    binding.tvPcn.setAdapter(arrayAdapter)
                    binding.tvPcn.threshold = 1

                    binding.tvPcn.setOnItemClickListener { adapterView, view, i, l ->
                        var data: PCN.Data = arrayList_details.get(i)
                        binding.carviewpcn.visibility = View.VISIBLE
                        binding.pcnClinet.text = data.brand
                        binding.pcnAddress.text = data.area + " " + data.city + " " + data.state

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
                        AutoCompleteAdapter(this@UploadExpensesActivity, R.layout.dropdwon_item, listdata)


                    binding.tvPcn.setAdapter(arrayAdapter)
                    binding.tvPcn.setThreshold(1)

                    arrayAdapter.notifyDataSetChanged()

                    binding.tvPcn.setValidator(CustomValidator(listdata))


                    //  binding.etpcnId.threshold = 2

//
                    binding.tvPcn.setOnItemClickListener { adapterView, view, i, l ->
                        var position : Int = listdata.indexOf(binding.tvPcn.text.toString())
                        var data: PCN.Data = arrayList_details.get(position)
                        isSelectedText = true
                        if (data.status.contains("Active")){
                            binding.carviewpcn.visibility = View.VISIBLE
                            binding.pcnClinet.text = data.brand
                            binding.pcnAddress.text = data.location + data.area + " -" + data.city + data.pincode

                        } else if (data.status.contains("Completed")){
                           // showAlertDialogOkAndCloseAfter("This PCN is Completed , Please contact your Super Admin for more information")
                            showAlertDialogOkAndCloseAfter("Selected Project is Completed, Please contact super Admin for more information")


                        }
                    }

                    binding.tvPcn.setOnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) {
                            val enteredText = binding.tvPcn.text.toString()
                            if (enteredText.isNotEmpty() && !listdata.contains(enteredText)) {
                                // Handle the case when the user didn't select from the dropdown
                                showAlertDialogOkAndCloseAr("Wrong PCN, please select from Drop Down: $enteredText","")
                                // Toast.makeText(this@CreateTicketActivity, "wrong input please correct: $enteredText", Toast.LENGTH_SHORT).show()
                                binding.tvPcn.setText("") // Clear the invalid entry
                            }
                        }
                    }



                    // myAutoComplete.addTextChangedListener(new CustomAutoCompleteTextChangedListener(this));
                    binding.tvPcn.addTextChangedListener(object : TextWatcher {
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
                            if(!listdata.contains(binding.tvPcn.text.toString()) && charSequence.toString().length > 1){
                                binding.carviewpcn.visibility = View.GONE
                                binding.tvPcnDoesNotExit.visibility = View.VISIBLE
                            }else{
                                binding.tvPcnDoesNotExit.visibility = View.GONE
                            }
                        }

                        override fun afterTextChanged(editable: Editable) {}
                    })


                }



            })


    }

    private fun showAlertDialogOkAndCloseAr(message:String, alertMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message + "\n"+ alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->
            setResult(Activity.RESULT_OK)
            binding.tvPcn.requestFocus()
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
            binding.tvPcn.setText("")
             }
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    private fun showAlertDialogOkAndCloseAftersucees(alertMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->
//            setResult(Activity.RESULT_OK)
        finish()}
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
          //compressAndSaveImage(file.toString(),50)

            val fileValue = compressAndSaveImage(file.toString(),50)


            // Added This Functionality
//            binding.imageview1.setImageURI(imageUri)
//            binding.imageview1.visibility = View.VISIBLE
//            binding.imageview.visibility = View.GONE
            addImage("$imageUri",fileValue)

        }
        if (resultCode == RESULT_OK && requestCode == 200 && data != null) {
            //imageView.setImageBitmap(data.extras.get("data") as Bitmap)

            val photo = data.extras!!["data"] as Bitmap?
            if (photo != null) {
//                binding.imageview1.setImageBitmap(photo)
//                binding.imageview1.visibility = View.VISIBLE
//                // binding.ivCamera.isVisible = false
//                binding.imageview.visibility = View.GONE

            }
            imageUri = this?.let { getImageUri(it, photo!!) }
            file = imageUri?.let { getRealPathFromURI(it)?.let { File(it) } };
            val fileValue = compressAndSaveImage(file.toString(), 50)
            addImage("$imageUri",fileValue)

            Log.v("TAG", "image path : $imageUri and $file")
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
       // imgList.add(Imaagefile!!)
        //return File(storageDir, imageFileName)

        return Imaagefile as File
    }

    private fun addImage(image: String,fileValue : File) {

        val infalot = LayoutInflater.from(this)
        val custrom = infalot.inflate(R.layout.addsingleandmultipleimage,null)

        val imageview = custrom.findViewById<ImageView>(R.id.iv_imagecapture)
        val icclose = custrom.findViewById<ImageView>(R.id.imageView_close)
        val linear = custrom.findViewById<LinearLayout>(R.id.linear)


        imageUriList.add(imageUri!!) // adding the image to the list
        imageview.setImageURI(imageUri) // setting the image view
        imgList.add(fileValue)
        icclose.setOnClickListener {
//            imageview.setImageResource(0)
//            icclose.setImageResource(0)
//            imageUriList.clear()
            if(imgList.contains(fileValue)){
                imgList.remove(fileValue)
            }

            binding.linearLayoutGridLevelSinglePiece.removeView(custrom)

            linear.visibility = View.GONE
            icclose.visibility = View.GONE
            Toast.makeText(this,"Image Removed",Toast.LENGTH_SHORT).show()
        }

        binding.btnMaterials.visibility = View.VISIBLE

        binding.linearLayoutGridLevelSinglePiece.addView(custrom)
    }



}