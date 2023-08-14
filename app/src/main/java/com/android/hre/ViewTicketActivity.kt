package com.android.hre

import android.Manifest
import android.annotation.SuppressLint
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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.ViewTcketAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityViewTicketBinding
import com.android.hre.response.Completelist
import com.android.hre.response.createtccikets.TicketCreated
import com.android.hre.response.employee.EmployeeList
import com.android.hre.response.getconve.Conversation
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
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

class ViewTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewTicketBinding
    var ticketno :String = ""
    var status :String = ""
    var userid :String = ""
    var ticketid :String = ""

    var subject :String = ""
    private lateinit var viewTicketAdapter: ViewTcketAdapter
    val listdata1: ArrayList<String> = arrayListOf()
    var maillistdata: ArrayList<Conversation.Data> = arrayListOf()
    var listEmployeeData: ArrayList<EmployeeList.Data> = arrayListOf()
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var file: File? = null
    private var Imaagefile: File? = null
    var receiptEmployee: Int? = null
    var receiptId : Int ? = null
    var data : String ? = null
    var username :String = ""
    val action: String = "Completed"
    private val imageUriList = ArrayList<Uri>()
    var position : Int ? = null



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
       // setContentView(R.layout.activity_view_ticket)


        binding = ActivityViewTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences =getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        username = sharedPreferences?.getString("username","")!!

        viewTicketAdapter = ViewTcketAdapter()

        val intentUser = intent
        ticketno = intentUser!!.getStringExtra("TicketNo").toString()
        status = intentUser.getStringExtra("Stauts").toString()
        subject = intentUser.getStringExtra("Subject").toString()
        //ticketid = intentUser.getStringExtra("ticketid")
        ticketid = intent.extras!!.getInt("TicketId").toString()
        position = intent.extras!!.getInt("pos")


        binding.tvticketno.text = ticketno
        binding.tvstatsus.text = status
        binding.tvsubject.text = "Subject : "  + subject




        fetchthemailList()
        dropdownEmployeeDetails()




        binding.ivBack.setOnClickListener {
            finish()
        }



        binding.ivimageupload.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }
        binding.etpcnId.setOnItemClickListener { adapterView, view, i, l ->

            var index = listdata1.indexOf(adapterView.getItemAtPosition(i))

            Log.v("TAG","i is: "+listEmployeeData.get(index).recipient)
            receiptEmployee  = listEmployeeData.get(index).recipient
            data = listdata1.get(index)
          //  binding.etpcnId.setText(data)
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
        binding.ivsend.setOnClickListener {

            Log.v("Data","$receiptEmployee")
            Log.v("Data","abcd $binding.etpcnId.text.toString()")
            var msg = binding.etpcnId.text.toString().replace(data!!,"")


            val ticketid = RequestBody.create(MediaType.parse("text/plain"), ticketid)
            val ticketno = RequestBody.create(MediaType.parse("text/plain"), ticketno)
            val message = RequestBody.create(MediaType.parse("text/plain"), msg)
            val userId = RequestBody.create(MediaType.parse("text/plain"), userid)
            val recipientid = RequestBody.create(MediaType.parse("text/plain"),"$receiptEmployee")

            Log.v("Data","$receiptEmployee")
            Log.v("Data","abcd $binding.etpcnId.text.toString()")


            // Image From Gallery File path
            val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpg"), Imaagefile)
            val image = MultipartBody.Part.createFormData("image", Imaagefile?.name, requestFile)
            val call = RetrofitClient.instance.addConversationForTicket(ticketid,ticketno, message, userId, recipientid,image)

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


        binding.tvcompleted.setOnClickListener {

            val Intent = Intent(this@ViewTicketActivity,CompletedActivity::class.java)
            Intent.putExtra("TicketNo",ticketno)
            Intent.putExtra("Subject",subject)
            Intent.putExtra("Stauts",status)
            Intent.putExtra("TicketId",ticketid)
            Intent.putExtra("Action",action)
            startForResult.launch(Intent)

        }

    }


    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val intent2=  Intent()
            intent2.putExtra("pos",position)
            setResult(Activity.RESULT_OK,intent2)
            finish()

            // Handle the Intent
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


    private fun fetchthemailList() {

        val call = RetrofitClient.instance.getConverstaion(userid,ticketid,ticketno)
        call.enqueue(object : Callback<Conversation> {
            override fun onResponse(call: Call<Conversation>, response: Response<Conversation>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    maillistdata.clear()
                     maillistdata = responseBody?.data as ArrayList<Conversation.Data>
                    Log.v("dat", maillistdata.toString())

                    if (!maillistdata.isEmpty()) {
                        val replyData : Conversation.Data = maillistdata.last()
                        if(replyData.recipient_id.equals(userid)){
                            binding.tvcompleted.visibility=View.VISIBLE
                            binding.linearlayoutreply.visibility = View.VISIBLE
                            if (status.contains("Resolved")){
                                binding.tvcompleted.visibility=View.GONE
                                binding.linearlayoutreply.visibility = View.GONE
                            }
                        }else{
                            binding.tvcompleted.visibility=View.GONE
                            binding.linearlayoutreply.visibility = View.GONE
                        }
//                       if(maillistdata.toString().contains(userid) && (maillistdata.toString().equals(receiptId))){   //Validating the user id and recipt id for the visible and
//                           binding.tvcompleted.visibility=View.VISIBLE
//                           binding.linearlayoutreply.visibility = View.VISIBLE
//                       } else{
//                           binding.tvcompleted.visibility=View.GONE
//                           binding.linearlayoutreply.visibility = View.GONE
//                       }
                        viewTicketAdapter.differ.submitList(maillistdata)
                    }

                    binding.rvRecylergrndata.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = viewTicketAdapter
                    }
                } else {
                    // Handle error response
                    val errorMessage = response.message()
                    // Display or handle the error message appropriately
                }
            }

            override fun onFailure(call: Call<Conversation>, t: Throwable) {
                // Handle network error
            }
        })
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
                        listdata1.add("@" + dataString.name  + " : " +   dataString.role + " ,  " )

                        //  listPCNdata.add(PCN.Data)

                    }
                    Log.v("TAG","Empl lis : $listdata1")



                    val arrayAdapter =
                        ArrayAdapter(this@ViewTicketActivity, R.layout.dropdwon_item, listdata1)
                    binding.etpcnId.setAdapter(arrayAdapter)
                     binding.etpcnId.setThreshold(1)
                    // binding.etpcnId.threshold = 2

                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<EmployeeList>, t: Throwable) {
                // Handle network error
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
//            binding.image.setImageURI(imageUri)
            file = imageUri?.let { getRealPathFromURI(it)?.let { File(it) } };
            compressAndSaveImage(file.toString(),50)
            binding.ivimageuploadq.isVisible = true
            binding.ivimageuploadq.setImageURI(imageUri)
            binding.ivimageupload.isVisible = false


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





}