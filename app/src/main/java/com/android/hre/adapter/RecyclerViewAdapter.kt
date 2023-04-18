package com.android.hre.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.R
import com.android.hre.db.UserEntity

class RecyclerViewAdapter (): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    var items  = ArrayList<UserEntity>()

    fun setListData(data: ArrayList<UserEntity>) {
        this.items = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_row, parent, false)
        return MyViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.MyViewHolder, position: Int) {

        holder.bind(items[position])
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val tvmaterialcategory = view.findViewById<TextView>(R.id.tvcategoryMaterial)
        val tvmaterialName = view.findViewById<TextView>(R.id.tvmaterialnameselection)
        val tvbrand  = view.findViewById<TextView>(R.id.tvbrnacdnamescateg)
        val tvqty = view. findViewById<TextView>(R.id.tv_qtysize)
        val tvdescription = view.findViewById<TextView>(R.id.tvdescrtipondisp)

        fun bind(data: UserEntity) {
            tvmaterialName.text = data.materialname
            tvbrand.text = data.brand
            tvqty.text = data.quantity
            tvdescription.text = data.description
            tvmaterialcategory.text = data.category
        }
    }
}
