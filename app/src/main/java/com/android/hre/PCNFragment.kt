package com.android.hre

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.HomeAdapter2
import com.android.hre.adapter.PCNAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentIndentBinding
import com.android.hre.databinding.FragmentPCNBinding
import com.android.hre.response.newindentrepo.NewIndents
import com.android.hre.response.newticketReponse.TikcetlistNew
import com.android.hre.response.pcns.PCN
import com.android.hre.response.searchpcndata.SearchPCNDataN
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PCNFragment : Fragment() {

    private lateinit var binding : FragmentPCNBinding
    private lateinit var pcnAdapter: PCNAdapter
    var userid : String = ""
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    private lateinit var handler: Handler
    private var timerRunnable : Runnable? = null
    private val delayMillis = 1500L
    lateinit var globalList :List<SearchPCNDataN.Data>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  return inflater.inflate(R.layout.fragment_p_c_n, container, false)

        binding = FragmentPCNBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
        userid = sharedPreferences?.getString("user_id", "")!!

        handler = Handler(Looper.getMainLooper())

        fetchThePCNList()

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.ivsearch.setOnClickListener {
            binding.carviewseacrh.visibility = View.VISIBLE
        }
        binding.etsearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                val initialItemList: List<Item> =  // Your initial data here
//                    homeAdapter.submitList(initialItemList)
//                homeAdapter.differ.submitList()

                if(s.toString().length == 0){
                    binding.ivProgressBar.visibility = View.GONE
                }else{
                    binding.ivProgressBar.visibility = View.VISIBLE
                }
                timerRunnable?.let { handler.removeCallbacks(it) }

                // Schedule a new timerRunnable with the specified delay
                timerRunnable = Runnable {

                    fetchtheSearchPCNList(s.toString())
//
                }

                handler.postDelayed(timerRunnable!!, delayMillis)


            }

            override fun afterTextChanged(s: Editable?) {
                // Do Nothing
            }
        })


        return root
    }

    private fun fetchThePCNList() {

        val call = RetrofitClient.instance.getAllPcns(userid)
        call.enqueue(object : Callback<PCN> {
            override fun onResponse(call: Call<PCN>, response: Response<PCN>) {
//

                if (response.isSuccessful) {
                    val indentResponse = response.body()

                    if (indentResponse != null && indentResponse.status == 1) {
                        val myIndents = indentResponse.data

                        for (i in 0 until myIndents.size ){
                            val myindent = myIndents.get(i)

                        }

                        pcnAdapter = PCNAdapter(myIndents)


                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = pcnAdapter
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

            }

            override fun onFailure(call: Call<PCN>, t: Throwable) {
                // Handle network error
            }
        })

    }

    private fun fetchtheSearchPCNList(search:String) {
        val call = RetrofitClient.instance.searchPcn(userid,search)
        call.enqueue(object : Callback<PCN> {
            override fun onResponse(call: Call<PCN>, response: Response<PCN>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()

                    if (indentResponse != null && indentResponse.status == 1) {
                        binding.ivProgressBar.visibility = View.GONE
                        val myIndents = indentResponse.data


                        pcnAdapter = PCNAdapter(myIndents)
                        pcnAdapter.notifyDataSetChanged()


                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = pcnAdapter
                        }

                        val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.windowToken, 0)

                    }
                    else {
                        binding.tvShowPening.visibility = View.VISIBLE
                        binding.tvShowPening.text = "No Pending Tickets"
                        // Handle error or unexpected response
                    }
                } else {
                    // Handle API call failure
                }


            }

            override fun onFailure(call: Call<PCN>, t: Throwable) {
                // Handle network error
            }
        })
    }



}