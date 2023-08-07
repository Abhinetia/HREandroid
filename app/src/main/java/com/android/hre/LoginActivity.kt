package com.android.hre

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.Manifest
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityLoginBinding
import com.android.hre.response.getappdata.AppDetails
import com.android.hre.storage.SharedPrefManager
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity :AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor : SharedPreferences.Editor


    private lateinit var binding: ActivityLoginBinding
    private val REQUEST_READ_PHONE_STATE = 123
    var deviceId :String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        else{
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences=getSharedPreferences(Constants.PREFS_KEY, MODE_PRIVATE)
        editor=sharedPreferences.edit()



        binding.btnlogin.setOnClickListener {
            loginUpUser()
        }

        checkPermissionsAndRetrieveDeviceId()


    }

    private fun checkPermissionsAndRetrieveDeviceId() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_READ_PHONE_STATE)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getDeviceId()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getDeviceId()
                }
            } else {
                // Handle permission denied case
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDeviceId() {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            // Use deviceId (Note: ANDROID_ID is not guaranteed to be unique across all devices)
            Log.v("IMEI","$deviceId")
        } else {
            deviceId = telephonyManager.imei
            Log.v("IMEI","$deviceId")
            // Use deviceId
        }
    }


    private fun loginUpUser() {
        val email =  binding.etEmployeeId.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val deviceid = deviceId.toString()

        if(email.isEmpty()){
            binding.etEmployeeId.error = "Email required"
            binding.etEmployeeId.requestFocus()
            return
        }


        if(password.isEmpty()){
            binding.etPassword.error = "Password required"
            binding.etPassword.requestFocus()
            return
        }
        RetrofitClient.instance.userLogin(email, password,deviceid)
            .enqueue(object: retrofit2.Callback<Any> {
                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    Log.v("Sucess",response.body().toString())
                    var JsonObject1 = JSONObject()
                    val result = response.body()
                    val gson = Gson()
                    val JsonObject3 = gson.toJsonTree(result).asJsonObject
                  //  var JsonObject = Convertion().convertStringToJSON(response.toString())
                    var JsonObject = JSONObject(JsonObject3.toString())

//                    val stringStringMap: Type = object : TypeToken<Map<String?, String?>?>() {}.type
//                    val map: Map<String, String> = gson.fromJson(json, stringStringMap)
                    Log.v("TAG","Status is $JsonObject")



                    var status =JsonObject.getString("status")


                    Log.v("TAG","Status is $status")


                    if (JsonObject.getString("status")  == "TRUE" ){
                        var JsonArray = JsonObject.getJSONArray("data")
//                        for (i in 0  until JsonArray.length()){
//                        }
                        JsonObject1 = JsonArray.getJSONObject(0)

                        fetchtheappData(JsonObject1.getString("user_id"),JsonObject1)

//                        Log.v("TAG","$JsonObject1")
//                        editor.putBoolean(Constants.ISLOGGEDIN,true)
//                        editor.putString("user_id", JsonObject1.getString("user_id"))
//                        editor.putString("employee_id", JsonObject1.getString("employee_id"))
//                        editor.putString("username", JsonObject1.getString("username"))
//                        editor.putString("role", JsonObject1.getString("role"))
//                        editor.putString("role_name",JsonObject1.getString("role_name"))

//                        editor.apply()
//                        editor.commit()
//                        val intent = Intent(applicationContext, MainActivity::class.java)
//                        startActivity(intent)
//                        finish()
                    } else {
                        showAlertDialogOkAndCloseAfter(JsonObject.getString("message"))
                    }




/*
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse != null) {
                            if (userResponse.status == 1 ) {
                                val userData = userResponse.data.toString()


                                Log.v("TAG","$userData")


                                //  SharedPrefManager.getInstance(applicationContext).saveUser(response.body()?.data!!)  // response.body()?.data!!
//                                Log.v("Shared", response.body()?.data!!.toString())
//                                val intent = Intent(applicationContext, MainActivity::class.java)
//                                startActivity(intent)
//                                finish()
                            } else {
                                // Handle invalid credentials or empty data
                                Log.v("TAG","$userResponse")

                                Toast.makeText(applicationContext, userResponse.message, Toast.LENGTH_LONG).show()
                                showAlertDialogOkAndCloseAfter(response.body()?.message.toString())

                            }
                        } else {
                            showAlertDialogOkAndCloseAfter(response.body()?.message.toString())

                            // Handle null response body
                            Toast.makeText(applicationContext, "Error: Invalid Response", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Handle HTTP error
                        Toast.makeText(applicationContext, "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
*/



                }
            })


    }


    private fun fetchtheappData(userid:String,jsonObject: JSONObject) {
        val call = RetrofitClient.instance.getappData(userid)
        call.enqueue(object : Callback<AppDetails> {
            override fun onResponse(call: Call<AppDetails>, response: Response<AppDetails>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    Log.v("dat", dataList.toString())

                    val version = getAppVersion(applicationContext)
                    println("App version: $version")

                    if (!dataList!!.need_update.equals("No")){
                        showAlertDialogOkAndCloseAfter("Please Use the latest Application of ARCHIVE")
                        return
                    }
                    if(!dataList!!.app_version.equals(version)){
                        showAlertDialogOkAndCloseAfter("Please Use the latest Application of ARCHIVE")
                        return
                    }

                    if (dataList!!.isloggedin.equals("true")){
                        openDashboard(jsonObject)
                    } else {
                        showAlertDialogOkAndCloseAfter("Please contact your Super Admin for more information")
                        return
                    }

                    val isLoggedIn = dataList!!.isloggedin
                    val appVersion = dataList!!.app_version
                    val needUpdate = dataList!!.need_update

                } else  {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<AppDetails>, t: Throwable) {
                // Handle network error
            }
        })
    }

    fun openDashboard(JsonObject1: JSONObject){

        Log.v("TAG","$JsonObject1")
        editor.putBoolean(Constants.ISLOGGEDIN,true)
        editor.putString("user_id", JsonObject1.getString("user_id"))
        editor.putString("employee_id", JsonObject1.getString("employee_id"))
        editor.putString("username", JsonObject1.getString("username"))
        editor.putString("role", JsonObject1.getString("role"))
        editor.putString("role_name",JsonObject1.getString("role_name"))
        editor.apply()
        editor.commit()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()

//        var intent =  Intent(this,MainActivity::class.java)
//        startActivity(intent)
//        finish()
    }

    fun getAppVersion(context: Context): String {
        try {
            val packageName = context.packageName
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "Unknown"
    }



    override fun onStart() {
        super.onStart()

        if (SharedPrefManager.getInstance(this).isLoggedIn) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }


    private fun showAlertDialogOkAndCloseAfter(alertMessage: String) {
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



}