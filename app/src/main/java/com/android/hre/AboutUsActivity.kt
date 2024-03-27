package com.android.hre

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.hre.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAboutUsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        //setContentView(R.layout.activity_about_us)

        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val version = getAppVersion(applicationContext)

        binding.tvVersionnumber.text = version

        binding.icinfo.setOnClickListener {
            onBackPressed()
        }

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