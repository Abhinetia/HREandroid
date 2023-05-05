package com.android.hre

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.android.hre.databinding.ActivityCreateIndentDeBinding

class CreateIndentDeActivity : AppCompatActivity() {

    private lateinit var binding :ActivityCreateIndentDeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()



        binding = ActivityCreateIndentDeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.btnMaterials.setOnClickListener {
//            val fullScreenBottomSheetDialogFragment = FullScreenBottomSheetDialog()
//            fullScreenBottomSheetDialogFragment.show(supportFragmentManager, FullScreenBottomSheetDialog::class.simpleName)
        }
    }


}