package com.android.hre.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.Constants
import com.android.hre.ViewIndentFragment
import com.android.hre.databinding.HomeindentlistBinding
import com.android.hre.databinding.ViewmoreextramaterialBinding
import com.android.hre.databinding.ViewmoreindentBinding
import com.android.hre.response.homeindents.GetIndentsHome
import com.android.hre.response.viewmoreindent.ViewMoreIndent
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ViewIndnetAdapter  : RecyclerView.Adapter<ViewIndnetAdapter.ViewHolder>() {

    private lateinit var binding : ViewmoreindentBinding
    private lateinit var context: Context
    var userid : String = ""



    inner class ViewHolder :RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(dataX: ViewMoreIndent.Data?) {
            binding.apply {
                if (dataX != null){
                    tvDispalymaterilaId.text = dataX.material_id
                    tvDisplaymaterialname.text = dataX.material_name
                    tvDisplaybrand.text = dataX.material_brand
                    tvDisplaystatus.text = dataX.status
                   // tvDisplaycreateon.text = dataX.created_on
                    tvDispalydesc.text = dataX.decription
                    tvQtytotal.text = dataX.quantity
                    tvQtyrecved.text = dataX.recieved
                    tvPening.text = dataX.pending

                    val inputDateString = dataX.created_on
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())


                    val date = inputFormat.parse(inputDateString)
                    val outputDateString = outputFormat.format(date)
                    val outputTimeString = outputTimeFormat.format(date)

                    tvDisplaycreateon.text  = outputDateString +" "+ outputTimeString


                    val info = dataX.material_info
                    Log.v("info", info.toString())
                    val jsonObject = JSONObject(info)
                    var stringd: String = ""
                    val iter: Iterator<String> = jsonObject.keys()
                    while (iter.hasNext()) {
                        val key = iter.next()
                        try {
                            val value: Any = jsonObject.get(key)
                            if (stringd == "") {
                                stringd = key + "  :  " + value

                            } else {
                                stringd = stringd + "\n " + key + " : " + value

                            }

                        } catch (e: JSONException) {
                            // Something went wrong!
                        }

                        fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")

                        tvDisplayinfo.text = stringd.capitalizeWords()

                    }

                }


            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ViewmoreindentBinding.inflate(inflater, parent, false)
        context = parent.context


        val sharedPreferences = context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<ViewMoreIndent.Data>(){
        override fun areItemsTheSame(oldItem: ViewMoreIndent.Data, newItem: ViewMoreIndent.Data): Boolean {
            return oldItem.indent_id == newItem.indent_id
        }

        override fun areContentsTheSame(oldItem: ViewMoreIndent.Data, newItem: ViewMoreIndent.Data): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun getItemViewType(position: Int): Int = position  // Shuffling need to be added

}