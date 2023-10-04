package com.android.hre

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

        binding.icinfo.setOnClickListener {
            onBackPressed()
        }


    }
}