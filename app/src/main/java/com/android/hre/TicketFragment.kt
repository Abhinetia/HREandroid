package com.android.hre

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.TicketAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentTicketBinding
import com.android.hre.response.newticketReponse.TikcetlistNew
import com.android.hre.response.tickets.TicketList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TicketFragment : Fragment() {
    private lateinit var binding: FragmentTicketBinding

    lateinit var cretaeTicket: TextView
    lateinit var createviewReplies :TextView
    var userid :String = ""
    private lateinit var ticketAdapter: TicketAdapter


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  return inflater.inflate(R.layout.fragment_ticket, container, false)


        binding = FragmentTicketBinding.inflate(layoutInflater)
        val root: View = binding.root

        val sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        fetchtheTicketList()
     ticketAdapter = TicketAdapter()
        fetchtheTicketList()
            return root
    }

    private fun fetchtheTicketList() {
        val call = RetrofitClient.instance.getTickets(userid)
        call.enqueue(object : Callback<TikcetlistNew> {
            override fun onResponse(call: Call<TikcetlistNew>, response: Response<TikcetlistNew>) {


                if (response.isSuccessful) {
                    val indentResponse = response.body()

                    if (indentResponse != null && indentResponse.status == 1) {
                        val myIndents = indentResponse.data.tickets

                        ticketAdapter.differ.submitList(myIndents)   //now added reverse function in android @5.53 pm need to check while debugging

                        binding.tvCount.text = myIndents?.size.toString()
                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = ticketAdapter
                        }

                    }
                    else {
                        binding.tvShowPening.visibility = View.VISIBLE
                        binding.tvShowPening.text = "No Pending Indents"
                        // Handle error or unexpected response
                    }
                } else {
                    // Handle API call failure
                }

/*
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    Log.v("dat", dataList.toString())

                    ticketAdapter.differ.submitList(dataList)   //now added reverse function in android @5.53 pm need to check while debugging

                    binding.tvCount.text = dataList?.size.toString()
                    binding.rvRecylergrndata.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = ticketAdapter
                    }

                } else  {

                    // Handle error response
                }
*/
            }

            override fun onFailure(call: Call<TikcetlistNew>, t: Throwable) {
                // Handle network error
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity?.let{
            cretaeTicket = it.findViewById(R.id.btn_cretae_ticket)
          //  createviewReplies = it.findViewById(R.id.tv_viewmore)


            cretaeTicket.setOnClickListener {
                val intent = Intent(
                    context,
                    CreateTicketActivity::class.java
                )
                mStartForResult.launch(intent)

            }

//            createviewReplies.setOnClickListener {
//                val Intent = Intent(view.context,ViewTicketActivity::class.java)
//                startActivity(Intent)
//            }

            binding.ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }


        }

    }

    var mStartForResult = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            fetchtheTicketList()
        }
    }


}
