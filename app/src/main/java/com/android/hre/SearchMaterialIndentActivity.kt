package com.android.hre

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.AutoSuggestAdapter
import com.android.hre.adapter.SearchMaterialIndentAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivitySearchMaterialIndentBinding
import com.android.hre.response.Getmaterials
import com.android.hre.response.listmaterial.Data
import com.android.hre.response.listmaterial.ListMaterials
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class SearchMaterialIndentActivity : AppCompatActivity(),SearchMaterialIndentAdapter.ItemClickListener {


    private lateinit var binding: ActivitySearchMaterialIndentBinding

    lateinit var searchMaterialIndentAdapter: SearchMaterialIndentAdapter
    lateinit var sharedPreferences: SharedPreferences

    var userid : String = ""
    val listdata: ArrayList<String> = arrayListOf()
    var arrayList_details:ArrayList<ListMaterials> = ArrayList();


    private var autoSuggestAdapter: AutoSuggestAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_search_material_indent)

        binding = ActivitySearchMaterialIndentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
         userid = sharedPreferences?.getString("user_id", "")!!


        fetchthelistMaterial()
        Log.v("Sucede",listdata.toString())

        searchMaterialIndentAdapter = SearchMaterialIndentAdapter(this)
        binding.searchList.adapter = searchMaterialIndentAdapter


        binding.btnMaterials.setOnClickListener {
             fetchTheMaterials()
        }
        binding.etMaterial.setOnClickListener {
            fetchthelistMaterial()
        }

        autoSuggestAdapter = AutoSuggestAdapter(
            this@SearchMaterialIndentActivity,
            android.R.layout.simple_dropdown_item_1line
        )
        binding.etMaterial.setThreshold(2)
        binding.etMaterial.setAdapter(autoSuggestAdapter)



    }

    private fun fetchthelistMaterial() {

        RetrofitClient.instance.getListMaterial(userid)
            .enqueue(object: retrofit2.Callback<ListMaterials> {
                override fun onFailure(call: Call<ListMaterials>, t: Throwable) {
                    Toast.makeText(applicationContext, "Reqeusted Id not Found", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<ListMaterials>, response: Response<ListMaterials>) {
                   // val jsonresponse = response.body()

                    var listMaterials: ListMaterials? = response.body()

                    var arrayList_details: List<ListMaterials.Data>? = listMaterials?.data

                  //  listdata.add("ewfwef")
                    for (i in 0 until arrayList_details?.size!!){
                        val dataString : ListMaterials.Data  = arrayList_details.get(i)

                        Log.v("log",i.toString())
                        listdata.add(dataString.name)
                    }


                        val arrayAdapter = ArrayAdapter(this@SearchMaterialIndentActivity,R.layout.dropdwon_item,listdata)
                        binding.etMaterial.setAdapter(arrayAdapter)
                        binding.etMaterial.setThreshold(1)


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
                    Toast.makeText(applicationContext, "Reqeusted Id not Found", Toast.LENGTH_LONG).show()
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
                                    layoutManager = LinearLayoutManager(this@SearchMaterialIndentActivity)
                                    adapter = searchMaterialIndentAdapter
                                }
                            }
                        }
                    }

                }
            })

    }


    override fun onItemClick(datax: Getmaterials.DataX, size: String, desc: String) {
        val data = Intent()
        data.putExtra("MaterialID",  datax.material_id)
        data.putExtra("MaterialBrand", datax.brand)
        data.putExtra("MaterialName", datax.name)
        data.putExtra("MaterialUOM", datax.uom)
        data.putExtra("MaterialSize",size)
        data.putExtra("MaterialDescrption",desc)
        setResult(Activity.RESULT_OK,data)
        finish()
    }
}