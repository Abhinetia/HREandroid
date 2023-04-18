package com.android.hre

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.hre.adapter.RecyclerViewAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityCreateIntendBinding
import com.android.hre.models.Intends
import com.android.hre.response.creatindent.SaveIndentResponse
import com.android.hre.response.pcns.PCN
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CreateIntendActivity : AppCompatActivity() {

    lateinit var btnshowbootomsheet :Button
    private lateinit var binding: ActivityCreateIntendBinding
    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var viewModel: MainActivityViewModel
    var materialCategory :String = ""
    var materialName : String = ""
    var materialBraand:String = ""
    var materialSize :String = ""
    var materialdescription :String = ""
    var userid : String = ""
    val listdata: ArrayList<String> = arrayListOf()
    var  pcnnumber :String =""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_create_intend)

        binding = ActivityCreateIntendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!

        dropdwonfromServer()

        binding.btnMaterials.setOnClickListener {

//            if(pcnnumber.isEmpty()){
//                binding.etpcnId.error = "Please Select PCN From the Drop Down"
//                binding.etpcnId.requestFocus()
//                return@setOnClickListener
//            }
             val Intent = Intent(this@CreateIntendActivity,SearchMaterialIndentActivity::class.java)
             startForResult.launch(Intent)

        }
        binding.ivCancel.setOnClickListener {
            binding.rvCosepcn.visibility = INVISIBLE

        }



        binding.btncreateintend.setOnClickListener {
            sendDataToServer()
        }

        binding.etpcnId.setOnClickListener {
            dropdwonfromServer()
        }



        binding.tvcategoryMaterial.text = materialCategory
        binding.tvmaterialnameselection.text = materialName
        binding.tvbrnacdnamescateg.text = materialBraand
        binding.tvQtysize.text = materialSize
        binding.tvdescrtipondisp.text = materialdescription

    }

    private fun dropdwonfromServer() {

        RetrofitClient.instance.getAllPcns(userid)
            .enqueue(object: retrofit2.Callback<PCN> {
                override fun onFailure(call: Call<PCN>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<PCN>, response: Response<PCN>) {
                    Log.v("Sucess",response.body().toString())
                    var listMaterials: PCN? = response.body()

                    var arrayList_details: List<PCN.Data>? = listMaterials?.data

                    //  listdata.add("ewfwef")
                    for (i in 0 until arrayList_details?.size!!){
                        val dataString : PCN.Data  = arrayList_details.get(i)

                        Log.v("log",i.toString())
                        listdata.add(dataString.pcn)
                    }
                    val arrayAdapter = ArrayAdapter(this@CreateIntendActivity,R.layout.dropdwon_item,listdata)
                    binding.etpcnId.setAdapter(arrayAdapter)
                    binding.etpcnId.setThreshold(1)

                }
            })


    }

    private fun sendDataToServer() {
//        val array = JSONArray()
//        val objp = JSONObject()
//        objp.put("material_id",materialCategory)
//        objp.put("description",materialdescription)
//        objp.put("quantity",materialSize)
//        val jsonArray = JSONArray()
//        jsonArray.put(objp)
//////        obj.put("user_id", userid)
//////        obj.put("pcn", "PCN_001")
////        for (i in 0 until jsonArray.length()){
////            obj.put("indents",jsonArray)
////        }
//
//        jsonArray.put(objp)
//
////        Log.v("TAG",obj.toString());
//        Log.v("such",jsonArray.toString())


//        val hashMap: HashMap<String, Any> = HashMap()
//        hashMap.put("user_id", userid)
//        hashMap.put("pcn", "PCN_001")
//        hashMap.put("indents", jsonArray)
//
//     Log.v("print",hashMap.toString())

//        val intend = Intends("CT00001","ABCD","20")
//
//        val intends: List<Intends>


        RetrofitClient.instance.sendReq("1","PCN",getMaterials())
            .enqueue(object: Callback<SaveIndentResponse> {
                override fun onFailure(call: Call<SaveIndentResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<SaveIndentResponse>, response: Response<SaveIndentResponse>) {
                    Log.v("Sucess",call.request().toString())

                    Log.v("Sucess",response.body().toString())
                   val status = response.body()?.status
//                    if (status?.equals(1)!!){
//                        Log.v("respo",status.toString())
//
//                    }else{
//                        Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()
//
//                    }

                }
            })


    }

    fun getMaterials() = listOf( Intends( "CT00001","ABCD","20" ),Intends( "CT00002","ABCD","23" ))


    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            // Handle the Intent

//            val name: String = data?.getStringExtra("customerName").toString() /* storing the values from arguments returned. */
//            val number: String = data?.getStringExtra("customerNumber").toString()
//            val postalCode: String = data?.getStringExtra("postalCode").toString()
//            val address: String = data?.getStringExtra("address").toString()
//
//
//            data.putExtra("MaterialID",  datax.material_id)
//            data.putExtra("MaterialBrand", datax.brand)
//            data.putExtra("MaterialName", datax.name)
//            data.putExtra("MaterialUOM", datax.uom)
//            data.putExtra("MaterialSize",size)
//            data.putExtra("MaterialDescrption",desc)
//

            //val intent = intent
            materialCategory = data?.getStringExtra("MaterialID").toString()
            materialName = data?.getStringExtra("MaterialName").toString()
            materialBraand = data?.getStringExtra("MaterialBrand").toString()
            materialSize = data?.getStringExtra("MaterialSize").toString()

            materialdescription = data?.getStringExtra("MaterialDescrption").toString()
            binding.rvCosepcn.visibility = VISIBLE

            binding.tvcategoryMaterial.text = materialCategory
            binding.tvmaterialnameselection.text = materialName
            binding.tvbrnacdnamescateg.text = materialBraand
            binding.tvQtysize.text = materialSize
            binding.tvdescrtipondisp.text = materialdescription

        }
    }
}