package com.android.hre.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.databinding.ViewmoreextramaterialBinding
import com.android.hre.response.Getmaterials
import me.tatarka.inject.annotations.Inject
import org.json.JSONException
import org.json.JSONObject


class SearchMaterialIndentAdapter @Inject() constructor(private val intemClickListener: ItemClickListener) : RecyclerView.Adapter<SearchMaterialIndentAdapter.ViewHolder>() {


    private lateinit var binding: ViewmoreextramaterialBinding
    private lateinit var context: Context
    private var lastCheckedRB: RadioButton? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ViewmoreextramaterialBinding.inflate(inflater, parent, false)
        context = parent.context
        return ViewHolder()    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int =differ.currentList.size
    override fun getItemViewType(position: Int): Int = position


    inner class ViewHolder :RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(dataX: Getmaterials.DataX) {
            binding.apply {
                titleTextiew.text = dataX.material_id
                tvmaterialNameindent.text = dataX.name
                tvbrnadindent.text = dataX.brand

//                binding.radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
//                    val checked_rb = group.findViewById<View>(checkedId) as RadioButton
//                    if (lastCheckedRB != null) {
//                        lastCheckedRB?.setChecked(false)
//                    }else{
//                        lastCheckedRB?.setChecked(true)
//                    }
//                    lastCheckedRB = checked_rb
//                })
                var size: String?= ""

                val info = dataX.information

                Log.v("info",info.toString())
                val jsonObject = JSONObject(info)
                var stringd :String = ""
                val iter: Iterator<String> = jsonObject.keys()
                while (iter.hasNext()) {
                    val key = iter.next()
                    try {
                        val value: Any = jsonObject.get(key)
                        if (stringd==""){
                            stringd = key + "  :  " + value

                        }else{
                            stringd = stringd + "\n " + key + " : " + value

                        }

                    } catch (e: JSONException) {
                        // Something went wrong!
                    }
                    //     Log.v("infod",value.toString())
//                        Log.v("infok",key)

                    fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")

                    tvDisplay.setOnClickListener {
                        tvDisplay.text= stringd.capitalizeWords()
                        Log.v("text",stringd.toString())
                        tvqty.visibility = View.VISIBLE
                        description.visibility = View.VISIBLE
                        tvuser.visibility = View.VISIBLE
                        tvusu.visibility = View.VISIBLE


                    }





                    tvViewmore.setOnClickListener {
                        val  qty = tvqty.text.toString()
                        val desc = description.text.toString()
                        if (qty.isEmpty()){
                            binding.tvqty.error = "Quantity required"
                            Toast.makeText(context,"Quantity required",Toast.LENGTH_LONG).show()
                            binding.tvqty.requestFocus()
                            return@setOnClickListener
                        }
                        if (desc.isEmpty()){
                            binding.description.error = "Description required"
                            Toast.makeText(context,"Description required",Toast.LENGTH_LONG).show()
                            binding.description.requestFocus()
                            return@setOnClickListener
                        }
                     //   val intent = Intent(context,CreateIntendActivity::class.java);
//                        val data = Intent()
//                        data.putExtra("MaterialID",  dataX.material_id)
//                        data.putExtra("MaterialBrand", dataX.brand)
//                        data.putExtra("MaterialName", dataX.name)
//                        data.putExtra("MaterialUOM", dataX.uom)
//                        data.putExtra("MaterialSize",tvqty.text.toString())
//                        data.putExtra("MaterialDescrption",description.text.toString())
//                        context.set
//
//
//                        context.startActivity(intent)

                            // onItemClickListener?.invoke(dataX);
                        intemClickListener.onItemClick(dataX,tvqty.text.toString(),description.text.toString())


                    }

                }
            }
        }

    }

    private var onItemClickListener: ((Getmaterials.DataX) -> Unit)? = null

    fun setOnItemClickListener(listener: (Getmaterials.DataX) -> Unit) {
        onItemClickListener = listener
    }

    interface  ItemClickListener{
        fun onItemClick(datax : Getmaterials.DataX,size : String, desc :String)
    }

    private val differCallback = object :DiffUtil.ItemCallback<Getmaterials.DataX>(){
        override fun areItemsTheSame(oldItem: Getmaterials.DataX, newItem: Getmaterials.DataX): Boolean {
            return oldItem.material_id == newItem.material_id
        }

        override fun areContentsTheSame(oldItem: Getmaterials.DataX, newItem: Getmaterials.DataX): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


}