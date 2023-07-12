package com.android.hre

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.AutoCompleteAdapter
import com.android.hre.adapter.SearchMaterialIndentAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivitySearchMaterialIndentBinding
import com.android.hre.databinding.BottomSheetDialogBinding
import com.android.hre.response.Getmaterials
import com.android.hre.response.Indent
import com.android.hre.response.listmaterial.ListMaterials
import com.android.hre.response.pcns.PCN
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Response

class FullScreenBottomSheetDialog constructor(private val bottomSheetItemClickListener: BottomSheetItemClickListener) : BottomSheetDialogFragment(),SearchMaterialIndentAdapter.ItemClickListener {

    val listdata: ArrayList<String> = arrayListOf()
    var userid : String = ""

    val listMaterialdata: ArrayList<String> = arrayListOf()


    private lateinit var binding: BottomSheetDialogBinding
    lateinit var searchMaterialIndentAdapter: SearchMaterialIndentAdapter




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false)

        binding = BottomSheetDialogBinding.inflate(inflater, container, false)


        val sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!

        searchMaterialIndentAdapter = SearchMaterialIndentAdapter(this)
        binding.searchList.adapter = searchMaterialIndentAdapter

        fetchthelistMaterial()
        Log.v("Sucede",listdata.toString())

        binding.etMaterial.setOnClickListener {
            fetchthelistMaterial()
        }
        binding.btnMaterials.setOnClickListener {
            fetchTheMaterials()
        }
        binding.ivnotification.setOnClickListener {
            dialog?.dismiss()
        }




        return binding.root

    }

    private fun fetchthelistMaterial() {

        RetrofitClient.instance.getListMaterial(userid)
            .enqueue(object: retrofit2.Callback<ListMaterials> {
                override fun onFailure(call: Call<ListMaterials>, t: Throwable) {
                    Toast.makeText(context, "Reqeusted List not Found", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<ListMaterials>, response: Response<ListMaterials>) {
                    // val jsonresponse = response.body()

                    var listMaterials: ListMaterials? = response.body()
                    listdata.clear()

                    var arrayList_details: List<ListMaterials.Data>? = listMaterials?.data

                    for (i in 0 until arrayList_details?.size!!){
                        val dataString : ListMaterials.Data  = arrayList_details.get(i)

                        Log.v("log",i.toString())
                        listdata.add(dataString.name)
                    }


                  //  val arrayAdapter = ArrayAdapter(context!!,R.layout.dropdwon_item,listdata)
                    val arrayAdapter =
                        AutoCompleteAdapter(context, R.layout.dropdwon_item, listdata)

                    binding.etMaterial.setAdapter(arrayAdapter)
                    binding.etMaterial.setThreshold(1)


                    binding.etMaterial.setOnItemClickListener { adapterView, view, i, l ->

                        binding.etMaterial.isEnabled = false


                    }
                }
            })

    }
    private fun fetchTheMaterials() {

        val itemCode =  binding.etItemcode.text.toString().trim()
        val materialName = binding.etMaterial.text.toString().trim()
        val materialBrnad = binding.etBrnad.text.toString().trim()


        RetrofitClient.instance.getUserMaterial(itemCode, materialName,materialBrnad)
            .enqueue(object: retrofit2.Callback<Getmaterials> {
                override fun onFailure(call: Call<Getmaterials>, t: Throwable) {
                    Toast.makeText(context, "Reqeusted Id not Found", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Getmaterials>, response: Response<Getmaterials>) {
                    Log.v("Sucess",response.body().toString())
//
                    response.body()?.let { itBody ->
                        itBody.data.let { itData ->
                            if (itData.isNotEmpty()) {
                                searchMaterialIndentAdapter.differ.submitList(itData)
                                //Recycler
                                binding.searchList.apply {
                                    layoutManager = LinearLayoutManager(context)
                                    adapter = searchMaterialIndentAdapter
                                }
                            }
                        }
                    }

                }
            })

    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
//            val bottomSheetBehavior: BottomSheetBehavior<*> =
//                BottomSheetBehavior.from<View>(bottomSheet)
//            bottomSheetBehavior.setPeekHeight(200)
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun onItemClick(datax: Getmaterials.DataX, size: String, desc: String) {

//        val data = Intent()
//        data.putExtra("MaterialID",  datax.material_id)
//        data.putExtra("MaterialBrand", datax.brand)
//        data.putExtra("MaterialName", datax.name)
//        data.putExtra("MaterialUOM", datax.uom)
//        data.putExtra("MaterialSize",size)
//        data.putExtra("MaterialDescrption",desc)

//        Indent(materialId = datax.material_id, description = desc, quantity = size)


        bottomSheetItemClickListener.onClick(datax,size,desc)
        dialog?.dismiss()

    }

    interface  BottomSheetItemClickListener{
        fun onClick(datax : Getmaterials.DataX,size : String, desc :String)
    }



}