package com.android.hre

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.android.hre.adapter.CustomAdapterList
import com.android.hre.adapter.SearchMaterialIndentAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.BottomSheetDialogBinding
import com.android.hre.interfac.CustomAdapterListInterface
import com.android.hre.response.Getmaterials
import com.android.hre.response.searchmaterialIndents.searchMaterial
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import kotlinx.serialization.decodeFromString
import org.json.JSONException


class FullScreenBottomSheetDialog constructor(private val bottomSheetItemClickListener: BottomSheetItemClickListener,private val mContext: Context) : BottomSheetDialogFragment(),SearchMaterialIndentAdapter.ItemClickListener,CustomAdapterListInterface {

    val listdata: ArrayList<String> = arrayListOf()
    var userid : String = ""

    val listMaterialdata: ArrayList<String> = arrayListOf()


    private lateinit var binding: BottomSheetDialogBinding
    lateinit var searchMaterialIndentAdapter: SearchMaterialIndentAdapter
    val searchListMultiple: ArrayList<String> = arrayListOf()
    lateinit var customAdapterList: CustomAdapterList





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false)

        binding = BottomSheetDialogBinding.inflate(inflater, container, false)


        val sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!

        searchMaterialIndentAdapter = SearchMaterialIndentAdapter(this)
       // binding.searchList.adapter = searchMaterialIndentAdapter

        Log.v("Sucede",listdata.toString())

//
//        binding.btnMaterials.setOnClickListener {
//            fetchTheMaterials()
//        }
        binding.ivnotification.setOnClickListener {
            dialog?.dismiss()
        }
        binding.etMaterial.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //fetchthelistMaterial(s.toString())

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //handleSearchInput(s.toString())
                fetchthelistMaterial(s.toString())

            }

            override fun afterTextChanged(s: Editable?) {
                // fetchthelistMaterial(s.toString())
            }
        })



        return binding.root

    }
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

/*
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

                       // binding.etMaterial.isEnabled = false


                    }
                }
            })

    }
*/


    private fun fetchthelistMaterial(search: String) {

        searchListMultiple.clear()
        RetrofitClient.instance.searchMaterialData(search)
            .enqueue(object : retrofit2.Callback<searchMaterial> {
                override fun onFailure(call: Call<searchMaterial>, t: Throwable) {
                    // Toast.makeText(context, "Reqeusted List not Found", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<searchMaterial>,
                    response: Response<searchMaterial>
                ) {
                    // val jsonresponse = response.body()

                    var listMaterials: searchMaterial? = response.body()


                    var arrayList_details: ArrayList<searchMaterial.Data>? = listMaterials?.data as ArrayList<searchMaterial.Data>?

                    Log.v("size",  arrayList_details!!.size.toString())



                    for (i in 0 until arrayList_details?.size!!) {
                        val dataString: searchMaterial.Data = arrayList_details.get(i)

                        val jsonString = dataString

                        //  val itemList: List<searchMaterial.Data> = Gson().fromJson(jsonString, itemType)
                        searchListMultiple.add(jsonString.value)
                    }

                    if(searchListMultiple.size > 0){
                        if(!binding.lisview.isVisible){
                            binding.lisview.visibility = View.VISIBLE
                        }


                        // Drop down layout style - simple list view with radio button
                        //  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        //searchList.adapter = adapter

                        customAdapterList = CustomAdapterList(arrayList_details, mContext,this@FullScreenBottomSheetDialog)
                        binding.lisview.adapter= customAdapterList


                    }
                }
            })

    }

/*
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

                                val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(view?.windowToken, 0)

                            }
                        }
                    }

                }
            })

    }
*/



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
    private fun addData(datax: searchMaterial.Data) {

        val infalot = LayoutInflater.from(context)
        val custrom = infalot.inflate(R.layout.item_layout,null)

        val materialName = custrom.findViewById<TextView>(R.id.tvmaterialNameindent)
        var brandNmae = custrom.findViewById<TextView>(R.id.tvbrnadindent)
        val itemcode = custrom.findViewById<TextView>(R.id.titleTextiew)

        val qtyedity = custrom.findViewById<TextInputEditText>(R.id.tvqty)
        val remarks = custrom.findViewById<TextInputEditText>(R.id.description)
        val tvsleect = custrom.findViewById<TextView>(R.id.tv_viewmore)
        val infoDisplay = custrom.findViewById<TextView>(R.id.tv_display)

        materialName.text = datax.name
        brandNmae.text = datax.brand
        itemcode.text = datax.item_code

        var infoDetails = datax.information
        var obj = JSONObject(infoDetails)

        var stringd: String = ""
        val iter: Iterator<String> = obj.keys()
        while (iter.hasNext()) {
            val key = iter.next()
            try {
                val value: Any = obj.get(key)
                if (stringd == "") {
                    stringd = key + "  :  " + value

                } else {
                    stringd = stringd + "\n " + key + " : " + value

                }

            } catch (e: JSONException) {
                // Something went wrong!
            }
        }


        infoDisplay.text = stringd.capitalizeWords()


        tvsleect.setOnClickListener {

            val qtyenteredd = qtyedity.text.toString()
            val descEntered = remarks.text.toString()


            if (qtyenteredd.isEmpty()){
                showAlertDialogOkAndCloseAfter("Quantity required")

                //Toast.makeText(context,"Quantity required",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (descEntered.isEmpty()){
                showAlertDialogOkAndCloseAfter("Description required")

               // Toast.makeText(context,"Description required",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
//            var infoDetails = datax.information
//            var obj = JSONObject(infoDetails)
            val information = jsonObjectToMap(obj)
            var getmaterials = Getmaterials.DataX( brandNmae.text.toString(),datax.item_code,materialName.text.toString(),datax.uom,information )
            Log.v("DATA",getmaterials.toString())

//
//            val qtyenteredd = qtyedity.text.toString()
//            val descEntered = remarks.text.toString()



           // data.information = brandNmae.text.toString()

//            val brand: String,
//            //  val information: Information,
//            val material_id: String,
//            val name: String,
//            val uom: String,
//            val information: Map<String,String>

            bottomSheetItemClickListener.onClick(getmaterials,qtyenteredd,descEntered)
            dialog?.dismiss()
        }



        //qtyedity.text =

        binding.linearLayoutGridLevelSinglePiece.addView(custrom)
    }

    fun String.capitalizeWords(): String =
        split(" ").map { it.capitalize() }.joinToString(" ")


    fun jsonObjectToMap(jsonObject: JSONObject): Map<String, String> {
        val map = mutableMapOf<String, String>()

        // Iterate over keys and values in the JSONObject
        for (key in jsonObject.keys()) {
            // Convert the value to String and add it to the map
            val value = jsonObject.getString(key)
            map[key] = value
        }

        return map
    }

    override fun onItemClick(datax: searchMaterial.Data) {
        binding.lisview.visibility = View.GONE
        addData(datax)
    }
    private fun showAlertDialogOkAndCloseAfter(alertMessage: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->  }  // LoginActivity::class.java
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

}