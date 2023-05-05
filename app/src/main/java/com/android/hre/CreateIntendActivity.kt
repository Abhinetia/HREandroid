package com.android.hre

import android.R
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.hre.api.RetrofitClient
import com.android.hre.api.RetrofitClient.api
import com.android.hre.databinding.ActivityCreateIntendBinding
import com.android.hre.models.Intends
import com.android.hre.response.CreateIndentRequest
import com.android.hre.response.Getmaterials
import com.android.hre.response.Indent
import com.android.hre.response.IndentResponse
import com.android.hre.response.pcns.PCN
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.AccessController.getContext


class CreateIntendActivity : AppCompatActivity(), FullScreenBottomSheetDialog.BottomSheetItemClickListener {

    lateinit var btnshowbootomsheet :Button
    private lateinit var binding: ActivityCreateIntendBinding
    var materialCategory :String = ""
    var materialName : String = ""
    var materialBraand:String = ""
    var materialSize :String = ""
    var materialdescription :String = ""
    var userid : String = ""
    val listdata: ArrayList<String> = arrayListOf()
    var  pcnnumber :String =""
    val listdindent: ArrayList<String> = arrayListOf()


    var arrayList_details: List<Indent> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        //setContentView(R.layout.activity_create_intend)

        binding = ActivityCreateIntendBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mLayoutInflater = LayoutInflater.from(this@CreateIntendActivity)
       // val customLayout = mLayoutInflater.inflate(R.layout.a, null)

        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!

        dropdwonfromServer()

        binding.btnMaterials.setOnClickListener {


//             val Intent = Intent(this@CreateIntendActivity,SearchMaterialIndentActivity::class.java)
//             startForResult.launch(Intent)

            val fullScreenBottomSheetDialogFragment = FullScreenBottomSheetDialog(this)
            fullScreenBottomSheetDialogFragment.show(supportFragmentManager, FullScreenBottomSheetDialog::class.simpleName)

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
//                    val arrayAdapter = ArrayAdapter(this@CreateIntendActivity,R.layout.dr,listdata)
//                    binding.etpcnId.setAdapter(arrayAdapter)
//                    binding.etpcnId.setThreshold(1)

                }
            })


    }

    private fun sendDataToServer() {

//        val request = CreateIndentRequest(
//            userId = "1",
//            pcn = "PCN_01155",
//            indents = listOf(
//                Indent(materialId = "CT00001", description = "ABCD", quantity = "20"),
//                Indent(materialId = "CT00002", description = "DEF", quantity = "10")
//            )
//        )

        val request = CreateIndentRequest(
            userId = "1",
            pcn = "PCN_055",
            indents = arrayList_details
        )

//        var arrayList_details: List<Indent>? = request.indents
//        for (i in 0 until arrayList_details?.size!!){
//            val dataString : Indent  = arrayList_details.get(i)
//
//            Log.v("log",i.toString())
//            listdata.add(dataString.materialId)
//            listdata.add(dataString.description)
//            listdata.add(dataString.quantity)
//
//        }

        val call = api.createIndent(request)
        call.enqueue(object : Callback<IndentResponse> {
            override fun onResponse(call: Call<IndentResponse>, response: Response<IndentResponse>) {
                if (response.isSuccessful) {
                    Log.v("data",response.body().toString())
                    // Handle successful response
                    val apiResponse = response.body()
                    // ...
                } else {
                    // Handle error response
                    val errorMessage = response.message()
                    // ...
                }
            }

            override fun onFailure(call: Call<IndentResponse>, t: Throwable) {
                // Handle network failure or other errors
                Log.v("data",t.toString())

                // ...
            }
        })


//        Log.v("INPUTS",mainJSONObject.toString())
//
//        RetrofitClient.instance.sendReq(mainJSONObject)
//            .enqueue(object: Callback<SaveResponse> {
//                override fun onFailure(call: Call<SaveResponse>, t: Throwable) {
//                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
//                }
//
//                override fun onResponse(call: Call<SaveResponse>, response: Response<SaveResponse>) {
//                    Log.v("Sucess",call.request().toString())
//
//                    Log.v("Sucess",response.body().toString())
//                   val status = response.body()?.status
////                    if (status?.equals(1)!!){
////                        Log.v("respo",status.toString())
////
////                    }else{
////                        Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()
////
////                    }
//
//                }
//            })


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

    override fun onClick(datax: Getmaterials.DataX, size: String, desc: String) {

        val indents = listOf(
            Indent(materialId = datax.material_id, description = desc, quantity = size)
        )

        materialCategory = datax.material_id
        materialName = datax.name
        materialBraand = datax.brand
        materialSize = size

        materialdescription = desc
        binding.rvCosepcn.visibility = VISIBLE

        binding.tvcategoryMaterial.text = materialCategory
        binding.tvmaterialnameselection.text = materialName
        binding.tvbrnacdnamescateg.text = materialBraand
        binding.tvQtysize.text = materialSize
        binding.tvdescrtipondisp.text = materialdescription


        arrayList_details = indents + arrayList_details

        singleLogic()

    }

    private fun singleLogic() {



//        val mLayoutInflater = LayoutInflater.from(this@CreateIntendActivity)
//        val customLayout = mLayoutInflater.inflate(R.layout., null)

        TODO("Not yet implemented")
    }

}



