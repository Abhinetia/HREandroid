package com.android.hre

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.hre.adapter.ViewIndnetAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentHomeBinding
import com.android.hre.databinding.FragmentViewIndentBinding
import com.android.hre.response.viewmoreindent.ViewMoreIndent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ViewIndentFragment : Fragment() {

    private lateinit var binding: FragmentViewIndentBinding
    var userid :String = ""
    private lateinit var viewindentadapter : ViewIndnetAdapter
    var id :String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_view_indent, container, false)

        binding = FragmentViewIndentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        viewindentadapter = ViewIndnetAdapter()



        fetchTheListOfIndents()

        return root
    }

    private fun fetchTheListOfIndents() {

        val call = RetrofitClient.instance.getViewMoreIndent(userid,id)
        call.enqueue(object : Callback<ViewMoreIndent> {
            override fun onResponse(call: Call<ViewMoreIndent>, response: Response<ViewMoreIndent>) {
                if (response.isSuccessful) {
                    val myDataList = response.body()?.data

                    Log.v("reponse",myDataList.toString())

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