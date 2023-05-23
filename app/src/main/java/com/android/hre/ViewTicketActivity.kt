package com.android.hre

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
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.TicketAdapter
import com.android.hre.adapter.ViewTcketAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityViewTicketBinding
import com.android.hre.response.employee.EmployeeList
import com.android.hre.response.getconve.Conversation
import com.android.hre.response.tickets.TicketList
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ViewTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewTicketBinding
    var ticketno :String = ""
    var status :String = ""
    var userid :String = ""
    var ticketid :String = ""

    var subject :String = ""
    private lateinit var viewTicketAdapter: ViewTcketAdapter
    val listdata1: ArrayList<String> = arrayListOf()
    var listEmployeeData: ArrayList<EmployeeList.Data> = arrayListOf()
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var file: File? = null
    private var Imaagefile: File? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
       // setContentView(R.layout.activity_view_ticket)


        binding = ActivityViewTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences =getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!

        viewTicketAdapter = ViewTcketAdapter()

        val intentUser = intent
        ticketno = intentUser!!.getStringExtra("TicketNo").toString()
        status = intentUser.getStringExtra("Stauts").toString()
        subject = intentUser.getStringExtra("Subject").toString()
        ticketid = intentUser.getStringExtra("TicketId").toString()


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
    }

    private fun fetchthemailList() {

        val call = RetrofitClient.instance.getConverstaion(userid,ticketid,ticketno)
        call.enqueue(object : Callback<Conversation> {
            override fun onResponse(call: Call<Conversation>, response: Response<Conversation>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val dataList = responseBody?.data
                    Log.v("dat", dataList.toString())

                    if (dataList != null) {
                        viewTicketAdapter.differ.submitList(dataList)
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
                        listdata1.add("@" + dataString.name  + " : " +   dataString.role + " ,  ")

                        //  listPCNdata.add(PCN.Data)

                    }



                    val arrayAdapter =
                        ArrayAdapter(this@ViewTicketActivity, R.layout.dropdwon_item, listdata1)
                    binding.etpcnId.setAdapter(arrayAdapter)
                    // binding.etpcnId.setThreshold(2)
                    //  binding.etpcnId.threshold = 2

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