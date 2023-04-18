package com.android.hre

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.hre.databinding.ActivityCreateTicketBinding
import com.android.hre.databinding.ActivityMainBinding

class CreateTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTicketBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_create_ticket)

        binding = ActivityCreateTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.ivBack.setOnClickListener {
            finish()
        }


    }
}