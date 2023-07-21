package com.android.hre

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.hre.databinding.ActivityTranscationInfoBinding

class TranscationInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTranscationInfoBinding
    var userid :String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = ActivityTranscationInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
    }
}