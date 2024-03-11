package com.android.hre

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.hre.adapter.AutoCompleteAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityUpdateTicketBinding
import com.android.hre.response.asignticket.ticketAssign
import com.android.hre.response.createtccikets.TicketCreated
import com.android.hre.response.departement.GetDepartment
import com.android.hre.response.employee.EmployeeList
import com.android.hre.response.pcns.PCN
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
import java.util.Calendar
import java.util.Date
import java.util.Locale


class UpdateTicketActivity : AppCompatActivity() {
    var ticketno :String = ""
    var status :String = ""
    var userid :String = ""
    var ticketid :String = ""
    var body :String = ""
    var subject :String = ""
    var pcn :String = ""
    var pcndetails :String = ""
    var priority :String = ""
    var ticketcreator :String = ""
    var roleId :Int = 0
    val listdata: ArrayList<String> = arrayListOf()
    val listdata2 :ArrayList<String> = arrayListOf()
    var listDepartmetData :ArrayList<GetDepartment.Data> = arrayListOf()

    private lateinit var binding:ActivityUpdateTicketBinding

    private var Imaagefile: File? = null
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var file: File? = null

    val listdata1: ArrayList<String> = arrayListOf()
    var listEmployeeData: ArrayList<EmployeeList.Data> = arrayListOf()

    private val calendar = Calendar.getInstance()
    var receiptEmployee: Int? = null







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityUpdateTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
         roleId = sharedPreferences?.getInt("role_id", 0)!!

       /* val fileUris = intent.getParcelableArrayListExtra<Uri>("fileUris")

        // Now you can use the file URIs as needed in the second activity
        if (fileUris != null) {
            for (uri in fileUris) {
                // Do something with each file URI
            }
        }*/

        val intentUser = intent
        ticketno = intentUser!!.getStringExtra("TicketNo").toString()
        status = intentUser.getStringExtra("Stauts").toString()
        subject = intentUser.getStringExtra("Subject").toString()
        ticketid = intentUser.getStringExtra("TicketId").toString()
        body = intentUser.getStringExtra("Body").toString()
        pcn = intentUser.getStringExtra("PCN").toString()
        priority = intentUser.getStringExtra("Priority").toString()
        pcndetails = intentUser.getStringExtra("PCN_Detilas").toString()
        ticketcreator = intentUser.getStringExtra("ticketcreator").toString()


        if (ticketcreator == userid){
            binding.etDescrtiption.isEnabled = true
            binding.etTickettitle.setInputType(1)
            binding.etTickettitle.setEnabled(true); // Enables the view for user input
            binding.etTickettitle.setFocusableInTouchMode(true);
        }  else{
            binding.etTickettitle.isEnabled = false
            binding.etDescrtiption.isEnabled = false

        }





//        binding.etTickettitle.text = ticketno
//        binding.tvstatsus.text = status
//        binding.tvsubject.text = "Subject : "  + subject
        binding.etTickettitle.setText(subject)
        binding.ticketno.text = "Ticket No:" + ticketno
        binding.etDescrtiption.setText(body)
        binding.etSelctpcn.setText(pcn)
        binding.etPriority.setText(priority)
        binding.tvdpcndatapcn.text = pcndetails


        dropdownDepartmentDetails()
        dropdwonfromServer()


        binding.rvimage.setOnClickListener {

            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        val month = resources.getStringArray(R.array.priority)
        val arrayAdapter = ArrayAdapter(this,R.layout.dropdwon_item,month)
        binding.etPriority.setAdapter(arrayAdapter)
        binding.etPriority.setOnClickListener {
            binding.etPriority.showDropDown()
            binding.etPriority.isEnabled = !(binding.etPriority.isEnabled)
        }

        val ststuscreated = resources.getStringArray(R.array.statuscreate)
        val arrayAdapter1 = ArrayAdapter(this,R.layout.dropdwon_item,ststuscreated)
        binding.etStstusCreated.setAdapter(arrayAdapter1)
        binding.etStstusCreated.setOnClickListener {
            binding.etStstusCreated.showDropDown()
            binding.etStstusCreated.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position) as String
                Log.v("TAG",selectedItem)
                if (selectedItem.equals("Ongoing")){
                    binding.lineaassign.visibility = View.VISIBLE
                    binding.btnAsiigntickte.visibility = View.VISIBLE
                    binding.btnCretaeticket.visibility = View.GONE
                    binding.etCommentsofrejection.visibility = View.GONE
                } else if (selectedItem.equals("Created")|| selectedItem.equals("Reject")) {
                    binding.lineaassign.visibility = View.GONE
                    binding.btnAsiigntickte.visibility = View.GONE
                    binding.btnCretaeticket.visibility = View.VISIBLE
                    binding.etCommentsofrejection.visibility = View.VISIBLE
                    binding.etComments.setText("")
                    binding.etDate.setText("")
                    binding.etAssignto.setText("")
                }

            })

        }


        if (roleId ==1 || roleId == 2 || roleId == 6 ){
            binding.ststusname.visibility = View.VISIBLE
            binding.etStstusCreated.visibility = View.VISIBLE

        } else {
            binding.lineaassign.visibility = View.GONE
            binding.ststusname.visibility = View.GONE
            binding.etStstusCreated.visibility = View.GONE
            binding.btnAsiigntickte.visibility = View.GONE
            binding.btnCretaeticket.visibility = View.VISIBLE

        }



        dropdownEmployeeDetails()


        binding.btnCretaeticket.setOnClickListener {
            validationPart()
        }


        binding.ivback.setOnClickListener {
           onBackPressed()
        }

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.etAssignto.setOnItemClickListener { adapterView, view, i, l ->

            var index = listdata1.indexOf(adapterView.getItemAtPosition(i))

            Log.v("TAG","i is: "+listEmployeeData.get(index).recipient)
            receiptEmployee  = listEmployeeData.get(index).recipient


        }
        binding.btnAsiigntickte.setOnClickListener {
            assignToEmployee()
        }

    }

    private fun showDatePicker() {
        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Create a new Calendar instance to hold the selected date
                val selectedDate = Calendar.getInstance()
                // Set the selected date using the values received from the DatePicker dialog
                selectedDate.set(year, monthOfYear, dayOfMonth)
                // Create a SimpleDateFormat to format the date as "dd/MM/yyyy"
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                // Format the selected date into a string
                val formattedDate = dateFormat.format(selectedDate.time)
                // Update the TextView to display the selected date with the "Selected Date: " prefix
                binding.etDate.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the DatePicker dialog
        datePickerDialog.show()
    }



    private fun validationPart() {
        if (binding.etPriority.text.isEmpty()){
            showAlertDialogOkAndAfter("Please Select Priority")

        }
        else if (binding.etSelctpcn.text.isEmpty()){
            showAlertDialogOkAndAfter("Please Select PCN")

            Toast.makeText(applicationContext, "Please Select Pcn", Toast.LENGTH_LONG).show()

        }
        else if (binding.etDescrtiption.text.isEmpty()){
            showAlertDialogOkAndAfter("Please Enter Description")
        }
        else if (binding.etTickettitle.text.isEmpty()){
            showAlertDialogOkAndAfter("Please Select Department")
        }else{
            updateToServer()
        }

    }

    private fun updateToServer() {

        val comment = binding.etCommentsofrejection.text.toString()
        val userId = RequestBody.create(MediaType.parse("text/plain"), userid)
        val ticketno = RequestBody.create(MediaType.parse("text/plain"), ticketno)
        val priority = RequestBody.create(MediaType.parse("text/plain"), binding.etPriority.text.toString())  // extra added priority
        val subject = RequestBody.create(MediaType.parse("text/plain"), binding.etTickettitle.text.toString())
        val issue = RequestBody.create(MediaType.parse("text/plain"), binding.etDescrtiption.text.toString())
        val ststus =  binding.etStstusCreated.text.toString()

        if (ststus.isEmpty()){
            binding.etStstusCreated.error = "Please Select Status"
            binding.etStstusCreated.requestFocus()
            return
        }

        var ticketststraus = ststus
        if (ststus.contains("Reject")){
            ticketststraus = "Rejected"
        }
        val ststusda=  RequestBody.create(MediaType.parse("text/plain"), ticketststraus)
        val commments = RequestBody.create(MediaType.parse("text/plain"), comment)



        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Saving Data")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        var call: Call<TicketCreated>? = null

        if(Imaagefile != null){
            val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpg"), Imaagefile)
            val image = MultipartBody.Part.createFormData("image", Imaagefile?.name, requestFile)
            call = RetrofitClient.instance.updateticketwithimage(userId,subject,issue,ticketno,priority,image)

        }else{
            call = RetrofitClient.instance.updateticket(userId,subject,issue,ticketno,priority,ststusda,commments)
        }

//            val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpg"), Imaagefile)
//            val image = MultipartBody.Part.createFormData("image", Imaagefile?.name, requestFile)
       // val call = RetrofitClient.instance.updateticket(userId,subject,issue,ticketno,priority)

        call.enqueue(object : retrofit2.Callback<TicketCreated> {
            override fun onResponse(call: Call<TicketCreated>, response: Response<TicketCreated>) {
                progressDialog.dismiss()
                Log.v("TAG", response.body().toString())
                Log.v("TAG","message "+ response.body()?.message.toString())
                if (response.body()!!.message.contains("Ticket updated")){
                    showAlertDialogOkAndCloseAfter(response.body()?.message.toString())

                }
               // showAlertDialogOkAndCloseAfter(response.body()?.message.toString())
               else if (response.body()!!.message.contains("Insufficient Inputs")){
                    showAlertDialogOkAndAfter(response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<TicketCreated>, t: Throwable) {
                progressDialog.dismiss()

                Log.v("TAG", t.toString())
            }

        })

    }


    private fun assignToEmployee() {



        val ststus =  binding.etStstusCreated.text.toString()
        val comment = binding.etComments.text.toString()
        val recipeinet = receiptEmployee.toString()
        val priority = binding.etPriority.text.toString()
        val tat = binding.etDate.text.toString()


         var ticketststraus = ststus
        if (ststus.equals("Ongoing")){
            ticketststraus = "Pending/Ongoing"
        }
        if (tat.isEmpty()){
            binding.etDate.error = "Please Select Date"
            binding.etDate.requestFocus()
            return
        }
        if(comment.isEmpty()){
            binding.etComments.error = "Comments required"
            binding.etComments.requestFocus()
            return
        }



        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Saving Data")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()



        val call = RetrofitClient.instance.assignTicket(userid,ticketid,ticketststraus,comment,recipeinet,priority,tat)

       call.enqueue(object : retrofit2.Callback<ticketAssign> {
            override fun onResponse(call: Call<ticketAssign>, response: Response<ticketAssign>) {
                progressDialog.dismiss()
                Log.v("TAG", response.body().toString())
                Log.v("TAG","message "+ response.body()?.message.toString())
                if (response.body()!!.message.contains("Ticket Assigned Successfully")){
                    showAlertDialogOkAndCloseAfter(response.body()?.message.toString())

                }
                // showAlertDialogOkAndCloseAfter(response.body()?.message.toString())
                else if (response.body()!!.message.contains("Insufficient Inputs")){
                    showAlertDialogOkAndAfter(response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<ticketAssign>, t: Throwable) {
                progressDialog.dismiss()

                Log.v("TAG", t.toString())
            }

        })

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
                    for (i in 0 until listDepartmetData?.size!!) {
                        val dataString: GetDepartment.Data = listDepartmetData.get(i)
                        Log.v("log", i.toString())
                      //  listdata2.add(dataString.category)
                        listdata2.add(dataString.department)

                    }
                    val arrayAdapter =
                        ArrayAdapter(this@UpdateTicketActivity, R.layout.dropdwon_item, listdata2)
                    binding.etTickettitle.setAdapter(arrayAdapter)
                    binding.etTickettitle.threshold = 1

                } else {
                }
            }

            override fun onFailure(call: Call<GetDepartment>, t: Throwable) {
                // Handle network error
            }
        })
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

                    for (i in 0 until arrayList_details?.size!!) {
                        val dataString: PCN.Data = arrayList_details.get(i)

                        Log.v("log", i.toString())
                        listdata.add(dataString.pcn)

                    }
//                    val arrayAdapter =
//                        ArrayAdapter(this@UpdateTicketActivity, R.layout.dropdwon_item, listdata)

                    val arrayAdapter =
                        AutoCompleteAdapter(this@UpdateTicketActivity, R.layout.dropdwon_item, listdata)

                    binding.etSelctpcn.setAdapter(arrayAdapter)
                     binding.etSelctpcn.threshold = 1

                    arrayAdapter.notifyDataSetChanged()


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
    private fun showAlertDialogOkAndAfter(alertMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->
            setResult(Activity.RESULT_OK)
             }
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
            compressAndSaveImage(file.toString(),50)

            // Added This Functionality
            binding.ivImagecapture.isVisible = true
            binding.frameimage.isVisible = true
            binding.ivImagecapture.setImageURI(imageUri)
            binding.rvimage.isVisible = false

            binding.imageViewClose.setOnClickListener {
                binding.ivImagecapture.setImageResource(0)
                binding.rvimage.visibility = View.VISIBLE
                binding.frameimage.visibility = View.GONE
                Toast.makeText(this,"Image Removed", Toast.LENGTH_SHORT).show()
            }

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

    private fun dropdownEmployeeDetails() {
        val call =  RetrofitClient.instance.getEmployee(userid)
        call.enqueue(object : Callback<EmployeeList> {
            override fun onResponse(call: Call<EmployeeList>, response: Response<EmployeeList>) {
                if (response.isSuccessful) {
                    //  val myDataList = response.body()?.data

                    var listMaterials: EmployeeList? = response.body()
                    listdata1.clear()
                    listEmployeeData.clear()


                    listEmployeeData = listMaterials?.data as java.util.ArrayList<EmployeeList.Data>

                    //  listdata.add("ewfwef")
                    for (i in 0 until listEmployeeData?.size!!) {
                        val dataString: EmployeeList.Data = listEmployeeData.get(i)


                        Log.v("log", i.toString())
                        listdata1.add(dataString.name  + " : " + dataString.role)


                    }
                    Log.v("TAG","Empl lis : $listdata1")



                    val arrayAdapter =
                        ArrayAdapter(this@UpdateTicketActivity, R.layout.dropdwon_item, listdata1)
                    binding.etAssignto.setAdapter(arrayAdapter)
                    binding.etAssignto.setThreshold(1)



                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<EmployeeList>, t: Throwable) {
                // Handle network error
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
        }
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }





}