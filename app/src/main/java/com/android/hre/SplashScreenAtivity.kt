package com.android.hre

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.hre.databinding.ActivitySplashScreenAtivityBinding
import com.android.hre.storage.SharedPrefManager

class SplashScreenAtivity :AppCompatActivity(){
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor : SharedPreferences.Editor


    private lateinit var binding: ActivitySplashScreenAtivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash_screen_ativity)

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
}