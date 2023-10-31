package com.android.hre

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.api.Api
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivitySplashScreenAtivityBinding
import com.android.hre.response.Indent
import com.android.hre.response.attendncelist.AttendanceListData
import com.android.hre.response.getappdata.AppDetails
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashScreenAtivity :AppCompatActivity(){
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    private val RECORD_REQUEST_CODE = 101
    var userid :String = ""




    private lateinit var binding: ActivitySplashScreenAtivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash_screen_ativity)

        setupPermissions()


        binding = ActivitySplashScreenAtivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences=getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()
        userid = sharedPreferences?.getString("user_id", "")!!


        Log.v("Sharedpref", sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false).toString())

       /* if(!sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
            val Intent = Intent(this@SplashScreenAtivity,LoginActivity::class.java)
            startActivity(Intent)
            finish()
        }else{
            fetchtheappData()
        }*/

        binding.btngetstarted.setOnClickListener {
            proceedToNextPage()
        }

    }

    fun proceedToNextPage(){
       /* if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
            var intent =  Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        } else{
            val Intent = Intent(this@SplashScreenAtivity,LoginActivity::class.java)
            startActivity(Intent)
            finish()

        }*/
        if(!sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
            val Intent = Intent(this@SplashScreenAtivity,LoginActivity::class.java)
            startActivity(Intent)
            finish()
        }else{
            fetchtheappData()
        }


    }
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("TAG", "Permission to storage is  denied")
            makeRequest()
        }
    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            RECORD_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("TAG", "Permission has been denied by user")
                } else {
                    Log.i("TAG", "Permission has been granted by user")
                }
            }
        }
    }

    private fun logout(){
        finish()
    }

    private fun fetchtheappData() {
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

                        if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
                            val Intent = Intent(this@SplashScreenAtivity,MainActivity::class.java)
                            startActivity(Intent)
                            finish()
                        } else{
                            val Intent = Intent(this@SplashScreenAtivity,LoginActivity::class.java)
                            startActivity(Intent)
                            finish()

                        }

                       // openDashboard()
                       /* editor.putBoolean(Constants.isEmployeeLoggedIn,true)
                        editor.apply()
                        editor.commit()*/
                    } else {

                        val Intent = Intent(this@SplashScreenAtivity,LoginActivity::class.java)
                        startActivity(Intent)
                        finish()

                        // openDataLogin()
                        /*showAlertDialogOkAndCloseAfter("Please contact your Super Admin for more information")
                        return*/
                    }

                   // proceedToNextPage()

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

    fun openDashboard(){
        var intent =  Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun openDataLogin(){
        var intent =  Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showAlertDialogOkAndCloseAfter(alertMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i -> finish() }  // LoginActivity::class.java
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
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



}