package com.android.hre.adapter

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.hre.R
import com.android.hre.VaultMainActivity
import com.android.hre.response.attendncelist.AttendanceListData
import com.android.hre.response.newvault.NewVaultDetiailsMainFolder
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.HashMap

class VaultMainFolderAdapter (private val mList: List<String>,val context:Context,val fileNames:String) : RecyclerView.Adapter<VaultMainFolderAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vaultfolder, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
         holder.textviewFolderName.text = ItemsViewModel


        holder.linearFolder.setOnClickListener {
            val allFolderNames:String
            if(TextUtils.isEmpty(fileNames)){
                allFolderNames = ItemsViewModel
            }else{
                allFolderNames = fileNames + "," + ItemsViewModel
            }
            context.startActivity(VaultMainActivity().newInstance(context,allFolderNames))
        }

    }

    fun hashMapData(folderName : String,pos:String): HashMap<String, String> {
        val mHashMap = HashMap<String,String>()
        mHashMap["f"+pos] = folderName

        return mHashMap

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textviewFolderName: TextView = itemView.findViewById(R.id.filename)
        val imgFolder: ImageView = itemView.findViewById(R.id.iv_folder)
        val linearFolder: LinearLayout = itemView.findViewById(R.id.linearfolder)


    }
}