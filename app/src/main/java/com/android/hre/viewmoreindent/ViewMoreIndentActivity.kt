package com.android.hre.viewmoreindent

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.Constants
import com.android.hre.R
import com.android.hre.adapter.ViewIndnetAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityViewMoreIndentBinding
import com.android.hre.databinding.FragmentViewIndentBinding
import com.android.hre.response.viewmoreindent.ViewMoreIndent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewMoreIndentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewMoreIndentBinding
    var userid :String = ""
    private lateinit var viewindentadapter : ViewIndnetAdapter
    var id :String = ""
    var pcn :String = ""
    var pcndetails :String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityViewMoreIndentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!


        viewindentadapter = ViewIndnetAdapter()

         id = intent.getStringExtra("indentid")!!
        pcn = intent.getStringExtra("pcn")!!
        pcndetails = intent.getStringExtra("pcndetails")!!

        Log.v("TAG","id : $id")


        fetchTheListOfIndents()
    }

    private fun fetchTheListOfIndents() {

        val call = RetrofitClient.instance.getViewMoreIndent(userid,id)
        call.enqueue(object : Callback<ViewMoreIndent> {
            override fun onResponse(call: Call<ViewMoreIndent>, response: Response<ViewMoreIndent>) {
                if (response.isSuccessful) {
                    val myDataList = response.body()?.data

                    Log.v("reponse",myDataList.toString())

                    viewindentadapter.differ.submitList(myDataList)

                    binding.rvRecylergrndata.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = viewindentadapter
                    }

                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<ViewMoreIndent>, t: Throwable) {
                // Handle network error
            }
        })
    }

}