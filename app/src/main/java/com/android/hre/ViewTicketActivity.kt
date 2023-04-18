package com.android.hre

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.hre.databinding.ActivityViewTicketBinding

class ViewTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewTicketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_view_ticket)


        binding = ActivityViewTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}