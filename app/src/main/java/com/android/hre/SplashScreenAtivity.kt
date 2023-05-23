package com.android.hre

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.hre.databinding.ActivitySplashScreenAtivityBinding
import com.android.hre.storage.SharedPrefManager

class SplashScreenAtivity :AppCompatActivity(){
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    private val RECORD_REQUEST_CODE = 101



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

        Log.v("Sharedpref", sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false).toString())

        binding.btngetstarted.setOnClickListener {

            if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
                var intent =  Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            } else{
                val Intent = Intent(this@SplashScreenAtivity,LoginActivity::class.java)
                startActivity(Intent)

            }


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

}