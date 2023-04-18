package com.android.hre

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.android.hre.databinding.ActivityAddMaterialBinding
import com.android.hre.databinding.ActivityCreateTicketBinding

class AddMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMaterialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_add_material)

        binding = ActivityAddMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnMaterials.setOnClickListener {
            validateAndPassInputs()
        }

            val materialcategory = resources.getStringArray(R.array.materialcategories)
            val arrayAdapter = ArrayAdapter(this@AddMaterialActivity,R.layout.dropdwon_item,materialcategory)
            binding.etChosecategory.setAdapter(arrayAdapter)

            val materialName = resources.getStringArray(R.array.materialnames)
            val arrayAdaptermaterialName = ArrayAdapter(this@AddMaterialActivity,R.layout.dropdwon_item,materialName)
            binding.etMaterialName.setAdapter(arrayAdaptermaterialName)

            val materialbrand = resources.getStringArray(R.array.brands)
            val arrayAdapterBrand = ArrayAdapter(this@AddMaterialActivity,R.layout.dropdwon_item,materialbrand)
            binding.etbrandname.setAdapter(arrayAdapterBrand)

            val materialsizes = resources.getStringArray(R.array.sizes)
            val arrayAdapterSizes = ArrayAdapter(this@AddMaterialActivity,R.layout.dropdwon_item,materialsizes)
             binding.etsize.setAdapter(arrayAdapterSizes)

            val materialThickness = resources.getStringArray(R.array.thickness)
            val arrayAdapterThickness = ArrayAdapter(this@AddMaterialActivity,R.layout.dropdwon_item,materialThickness)
            binding.etThickness.setAdapter(arrayAdapterThickness)

            val materialunits = resources.getStringArray(R.array.units)
            val arrayAdapterunits = ArrayAdapter(this@AddMaterialActivity,R.layout.dropdwon_item,materialunits)
            binding.etUnits.setAdapter(arrayAdapterunits)

            val materialgrades = resources.getStringArray(R.array.grades)
            val arrayAdapterGrades = ArrayAdapter(this@AddMaterialActivity,R.layout.dropdwon_item,materialgrades)
            binding.etGrade.setAdapter(arrayAdapterGrades)


    }

    private fun validateAndPassInputs() {

        val materialCategory = binding.etChosecategory.text.toString()
        val materialName = binding.etMaterialName.text.toString()
        val materialBraand = binding.etbrandname.text.toString()
        val materialSize = binding.etsize.text.toString()
        val materialThickness = binding.etThickness.text.toString()
        val materialGrade = binding.etGrade.text.toString()
        val materialShades = binding.etShadeno.text.toString()
        val materialUnits = binding.etUnits.text.toString()
        val materialdescription = binding.etDescriptionofmaterial.text.toString()


        val intent = Intent(this@AddMaterialActivity,CreateIntendActivity::class.java)
        intent.putExtra("MaterialCategory",materialCategory)
        intent.putExtra("MaterialName",materialName)
        intent.putExtra("MaterialBrand",materialBraand)
        intent.putExtra("MaterialSize",materialSize)
        intent.putExtra("MaterialThickness",materialThickness)
        intent.putExtra("MaterialGrade",materialGrade)
        intent.putExtra("MaterialShades",materialShades)
        intent.putExtra("MaterialUnits",materialUnits)
        intent.putExtra("MaterialDescrption",materialdescription)
        startActivity(intent)
        finish()


    }
}