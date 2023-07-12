package com.android.hre

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.hre.adapter.AutoCompleteAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityCaretingIndeNewBinding
import com.android.hre.models.Intends
import com.android.hre.response.CreateIndentRequest
import com.android.hre.response.Getmaterials
import com.android.hre.response.Indent
import com.android.hre.response.IndentResponse
import com.android.hre.response.pcns.PCN
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CaretingIndeNewActivity : AppCompatActivity() ,FullScreenBottomSheetDialog.BottomSheetItemClickListener  {
    lateinit var btnshowbootomsheet : Button
    var materialCategory :String = ""
    var materialName : String = ""
    var materialBraand:String = ""
    var materialSize :String = ""
    var materialdescription :String = ""
    var userid : String = ""
    val listdata: ArrayList<String> = arrayListOf()
    val listPCNdata: ArrayList<PCN> = arrayListOf()

    var  pcnnumber :String =""
    private var actualitemcodeTextList: List<TextView>? = null
    private var actualmaterialName :List<TextView>? = null
    private var actualmaterialBrand :List<TextView>? = null
    private var actualmaterilaqty : List<TextView>? =  null
    private var actualmaterialDesc : List<TextView>? = null

    var arrayList_details: List<Indent> = arrayListOf()
    val indentHashMap = HashMap<String, Indent>()
    val indentArrayList: ArrayList<Indent> = arrayListOf()



    private lateinit var binding: ActivityCaretingIndeNewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_careting_inde_new)


        supportActionBar?.hide()
        binding = ActivityCaretingIndeNewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!

       // Log.v(usr)

        dropdwonfromServer()

        binding.btnMaterials.setOnClickListener {
            val email =  binding.etpcnId.text.toString().trim()

            if(email.isEmpty()){
                binding.etpcnId.error = "Please Select PCN"
                binding.etpcnId.requestFocus()
                return@setOnClickListener
            }

            val fullScreenBottomSheetDialogFragment = FullScreenBottomSheetDialog(this)
            fullScreenBottomSheetDialogFragment.show(supportFragmentManager, FullScreenBottomSheetDialog::class.simpleName)

        }

        binding.ivCancel.setOnClickListener {
            binding.rvCosepcn.visibility = View.INVISIBLE

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
                    Log.v("Sucess", response.body().toString())
                    var listMaterials: PCN? = response.body()
                    listdata.clear()

                    var arrayList_details: List<PCN.Data>? = listMaterials?.data

                    //  listdata.add("ewfwef")
                    for (i in 0 until arrayList_details?.size!!) {
                        val dataString: PCN.Data = arrayList_details.get(i)

                        Log.v("log", i.toString())
                        listdata.add(dataString.pcn)
                        //  listPCNdata.add(PCN.Data)


                    }


//                    val arrayAdapter =
//                        ArrayAdapter(this@CaretingIndeNewActivity, R.layout.dropdwon_item, listdata)
                    val arrayAdapter =
                        AutoCompleteAdapter(this@CaretingIndeNewActivity, R.layout.dropdwon_item, listdata)


                    binding.etpcnId.setAdapter(arrayAdapter)
                    binding.etpcnId.setThreshold(1)


                  //  binding.etpcnId.threshold = 2

//
                    binding.etpcnId.setOnItemClickListener { adapterView, view, i, l ->
                        var data: PCN.Data = arrayList_details.get(i)
                        binding.carviewpcn.visibility = View.VISIBLE
                        binding.pcnClinet.text = data.client_name
                        //  binding.etpcnId.isEnabled = false
                        binding.pcnAddress.text = data.area + " " + data.city + " " + data.state
                        binding.etpcnId.isEnabled = false


                    }
                   // myAutoComplete.addTextChangedListener(new CustomAutoCompleteTextChangedListener(this));



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

//        for (key in indentHashMap.keys) {
//            arrayList_details = arrayList_details + indentHashMap.getValue(key)
//        }

        for (keys in indentArrayList){
            arrayList_details = arrayList_details + keys
        }

        val request = CreateIndentRequest(
            userId = userid,
            pcn = binding.etpcnId.text.toString(),
            indents = arrayList_details
        )
        Log.v("TAG",request.toString())

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

        val call = RetrofitClient.api.createIndent(request)
        call.enqueue(object : Callback<IndentResponse> {
            override fun onResponse(call: Call<IndentResponse>, response: Response<IndentResponse>) {

                    Log.v("data",response.body().toString())
                    // Handle successful response
                    val apiResponse = response.body()
                    if (apiResponse?.status == 1){
                        showAlertDialogOkAndCloseAfter(apiResponse.message.toString())

                }
            }

            override fun onFailure(call: Call<IndentResponse>, t: Throwable) {
                // Handle network failure or other errors
                Log.v("data",t.toString())

                // ...
            }
        })

    }
    fun getMaterials() = listOf( Intends( "CT00001","ABCD","20" ), Intends( "CT00002","ABCD","23" ))


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
            binding.rvCosepcn.visibility = View.VISIBLE

            binding.tvcategoryMaterial.text = materialCategory
            binding.tvmaterialnameselection.text = materialName
            binding.tvbrnacdnamescateg.text = materialBraand
            binding.tvQtysize.text = materialSize
            binding.tvdescrtipondisp.text = materialdescription

        }
    }

    override fun onClick(datax: Getmaterials.DataX, size: String, desc: String) {

//        if(indentHashMap.containsKey(datax.material_id)){
//            showAlertDialogOkAndClose("Duplicate item codes are not allowed.")// This Functionlaity Temporiatly Told to be On HolD from kamal they can add duplicate items
//            return
//        }

        val indents = listOf(
            Indent(materialId = datax.material_id, description = desc, quantity = size)
        )

        materialCategory = datax.material_id
        materialName = datax.name
        materialBraand = datax.brand
        materialSize = size

        materialdescription = desc
       // binding.rvCosepcn.visibility = View.VISIBLE

        binding.tvcategoryMaterial.text = materialCategory
        binding.tvmaterialnameselection.text = materialName
        binding.tvbrnacdnamescateg.text = materialBraand
        binding.tvQtysize.text = materialSize
        binding.tvdescrtipondisp.text = materialdescription


        //arrayList_details = indents + arrayList_details

//        indentHashMap.put(datax.material_id,Indent(materialId = datax.material_id, description = desc, quantity = size))

        indentArrayList.add(Indent(materialId = datax.material_id, description = desc, quantity = size))

        Log.v("TAG",indentArrayList.toString())
        singleLogic(datax,size,desc,indentArrayList)

    }

    private fun singleLogic(datax: Getmaterials.DataX, size: String, desc: String,indentArrayList : ArrayList<Indent>) {

        val infalot = LayoutInflater.from(this)
        val custrom = infalot.inflate(R.layout.recyclerview_row,null)

        val textViewitemcode = custrom.findViewById<TextView>(R.id.tvcategoryMaterial)
        val  textmaterialnames = custrom.findViewById<TextView>(R.id.tvmaterialnameselection)
        val  textmaterialbrand = custrom.findViewById<TextView>(R.id.tvbrnacdnamescateg)
        val  textqty = custrom.findViewById<TextView>(R.id.tv_qtysize)
        val textdesc = custrom.findViewById<TextView>(R.id.tvdescrtipondisp)
        val iv_cancel = custrom.findViewById<ImageView>(R.id.iv_cancel)
        textViewitemcode.setTag(indentArrayList.size-1)

        textViewitemcode.text = datax.material_id
        textmaterialnames.text = datax.name
        textmaterialbrand.text = datax.brand
        textqty.text = size
        textdesc.text = desc

        iv_cancel.setOnClickListener {
            binding.linearLayoutGridLevelSinglePiece.removeView(custrom)


            indentArrayList.removeAt(textViewitemcode.getTag() as Int)

//            if (indentHashMap.containsKey(datax.material_id)) {
//                indentHashMap.remove(datax.material_id)
//            }
        }

        binding.linearLayoutGridLevelSinglePiece.addView(custrom)
    }



    private fun showAlertDialogOkAndClose(alertMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->  }
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    private fun showAlertDialogOkAndCloseAfter(alertMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i -> finish() }
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }


}