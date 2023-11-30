package com.android.hre.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.android.hre.R
import com.android.hre.interfac.CustomAdapterListInterface
import com.android.hre.response.searchmaterialIndents.searchMaterial
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject

class CustomAdapterList(private val dataSet: ArrayList<searchMaterial.Data>, private val mContext: Context, private val intemClickListener: CustomAdapterListInterface) :
        ArrayAdapter<searchMaterial.Data>(mContext, R.layout.itemlistlayout, dataSet){

        // View lookup cache
        private class ViewHolder {
            var itemMaterialNameTextView: TextView? = null


        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val dataModel = getItem(position)
            val viewHolder: ViewHolder // view lookup cache stored in tag
            val result: View

            if (convertView == null) {
                viewHolder = ViewHolder()
                val inflater = LayoutInflater.from(context)
                convertView = inflater.inflate(R.layout.itemlistlayout, parent, false)

                viewHolder.itemMaterialNameTextView = convertView.findViewById(R.id.tv_textData)


                result = convertView
                convertView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ViewHolder
                result = convertView
            }

            viewHolder.itemMaterialNameTextView?.setOnClickListener {
                intemClickListener.onItemClick(dataModel!!)

            }
            viewHolder.itemMaterialNameTextView?.setText(dataModel!!.value)




            return result
        }



}