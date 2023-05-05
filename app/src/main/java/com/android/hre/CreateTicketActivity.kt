package com.android.hre

import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityCreateTicketBinding
import com.android.hre.response.pcns.PCN
import retrofit2.Call
import retrofit2.Response


class CreateTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTicketBinding
    var userid: String = ""
    private val pickImage = 100
    private var imageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1000
    val listdata: ArrayList<String> = arrayListOf()
    val REQUEST_CODE = 100



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        //  setContentView(R.layout.activity_create_ticket)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            // If the app doesn't have permission, ask the user to grant it
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        } else {
            // Permission is granted or device is not running Android 11 or higher
            // Do your work here, such as capturing an image from the camera or selecting an image from the gallery
        }

        binding = ActivityCreateTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!

        dropdwonfromServer()

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivCamera.setOnClickListener {
//
//            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)


//
        }
        binding.etSelctpcn.setOnClickListener {
            dropdwonfromServer()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
          //  binding.ivCamera.setImageURI(data?.data)
        // handle chosen image
         val selectedImage = data?.data

        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
//
//            val selectedImage = data?.data
//            val inputStream = contentResolver.openInputStream(selectedImage!!)
//            val file = File(cacheDir, "image.jpg")
//            val outputStream = FileOutputStream(file)
//            inputStream?.copyTo(outputStream)
//
//            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
//            val image = MultipartBody.Part.createFormData("image", file.name, requestFile)
//
//            val userId = RequestBody.create(MediaType.parse("text/plain"), userid)
//            val pcn = RequestBody.create(MediaType.parse("text/plain"), binding.etSelctpcn.text.toString())
//            val indentNo = RequestBody.create(MediaType.parse("text/plain"), "your-indent-no")
//            val subject = RequestBody.create(MediaType.parse("text/plain"), binding.etTickettitle.text.toString())
//            val issue = RequestBody.create(MediaType.parse("text/plain"), binding.etDescrtiption.text.toString())
//            val recipient = RequestBody.create(MediaType.parse("text/plain"), "4")
//
//
//            val call = RetrofitClient.instance.uploadData(userId, pcn, indentNo, subject, issue, image, recipient)
//            call.enqueue(object : Callback<TicketCreated> {
//                override fun onResponse(call: Call<TicketCreated>, response: Response<TicketCreated>) {
//                    if (response.isSuccessful && response.body() != null && response.body()?.status == 1) {
//
//                        // Show a success message to the user
//                        Log.v("Data",call.toString())
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


}
