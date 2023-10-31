package com.android.hre

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.TicketAdapter
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentTicketBinding
import com.android.hre.response.getappdata.AppDetails
import com.android.hre.response.newticketReponse.TikcetlistNew
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TicketFragment : Fragment(),TicketAdapter.ViewMoreClickListener {
    private lateinit var binding: FragmentTicketBinding

    lateinit var cretaeTicket: TextView
    lateinit var createviewReplies :TextView
    var userid :String = ""
    private lateinit var ticketAdapter: TicketAdapter
    lateinit var globalList :List<TikcetlistNew.Ticket>
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    private lateinit var handler: Handler
    private var timerRunnable : Runnable? = null
    private val delayMillis = 1500L



    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  return inflater.inflate(R.layout.fragment_ticket, container, false)


        binding = FragmentTicketBinding.inflate(layoutInflater)
        val root: View = binding.root

        sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
        handler = Handler(Looper.getMainLooper())
        userid = sharedPreferences?.getString("user_id", "")!!
        fetchtheTicketList()
        ticketAdapter = TicketAdapter(this)

        if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
            fetchtheappData()
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

                    fetchtheSearchTicketList(s.toString())
//                    }else if(s.toString().length == 0){
//                        fetchTheIndentList()
//                    }
                }

                handler.postDelayed(timerRunnable!!, delayMillis)


//                if(s.toString().length == 2){
//                    fetchtheSearchTicketList(s.toString())
//                }else if(s.toString().length == 0){
//                    fetchtheTicketList()
//                }

                //  fetchTheIndentList()
            }

            override fun afterTextChanged(s: Editable?) {
                // Do Nothing
            }
        })


        fetchtheTicketList()
            return root
    }

    private fun fetchtheSearchTicketList(search:String) {
        val call = RetrofitClient.instance.getticketsearch(userid,search,sharedPreferences?.getString("role", "")!!)
        call.enqueue(object : Callback<TikcetlistNew> {
            override fun onResponse(call: Call<TikcetlistNew>, response: Response<TikcetlistNew>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()

                    if (indentResponse != null && indentResponse.status == 1) {
                        binding.ivProgressBar.visibility = View.GONE
                        val myIndents = indentResponse.data


                        globalList = indentResponse.data.tickets
                        ticketAdapter.differ.submitList(globalList)
                        ticketAdapter.notifyDataSetChanged()


                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = ticketAdapter
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

            override fun onFailure(call: Call<TikcetlistNew>, t: Throwable) {
                // Handle network error
            }
        })
    }

    private fun fetchtheTicketList() {
        val call = RetrofitClient.instance.getTickets(userid)
        call.enqueue(object : Callback<TikcetlistNew> {
            override fun onResponse(call: Call<TikcetlistNew>, response: Response<TikcetlistNew>) {


                if (response.isSuccessful) {
                    val indentResponse = response.body()

                    if (indentResponse != null && indentResponse.status == 1) {
                        binding.ivProgressBar.visibility = View.GONE

                        globalList = indentResponse.data.tickets
                        ticketAdapter.differ.submitList(globalList)   //now added reverse function in android @5.53 pm need to check while debugging

                        binding.tvCount.text = globalList?.size.toString()
                        binding.rvRecylergrndata.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = ticketAdapter
                        }
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


    private fun fetchtheappData() {
        val call = RetrofitClient.instance.getappData(userid)
        call.enqueue(object : Callback<AppDetails> {
            override fun onResponse(call: Call<AppDetails>, response: Response<AppDetails>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    Log.v("dat", dataList.toString())

                    val version = getAppVersion(context!!)
                    println("App version: $version")

                    if (!dataList!!.need_update.equals("No")){
                        showAlertDialogOkAndCloseAfter("Please Use the latest Application of ARCHIVE")
                        return
                    }
                    if(!dataList!!.app_version.equals(version)){
                        showAlertDialogOkAndCloseAfter("Please Use the latest Application of ARCHIVE")
                        return
                    }

                    if (dataList!!.isloggedin.equals("true")){
                        // openDashboard()
                    } else {
                        editor.putBoolean(Constants.isEmployeeLoggedIn,false)
                        editor.apply()
                        editor.commit()

                        var intent =  Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                       // openDataLogin()
                        /*showAlertDialogOkAndCloseAfter("Please contact your Super Admin for more information")
                        return*/
                    }

                    val isLoggedIn = dataList!!.isloggedin
                    val appVersion = dataList!!.app_version
                    val needUpdate = dataList!!.need_update

                } else  {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<AppDetails>, t: Throwable) {
                // Handle network error
            }
        })
    }
    fun openDashboard(){
        var intent =  Intent(context, MainActivity::class.java)
        startActivity(intent)
    }

    fun openDataLogin(){
        var intent =  Intent(context, LoginActivity::class.java)
        startActivity(intent)
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
    fun getAppVersion(context: Context): String {
        try {
            val packageName = context.packageName
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "Unknown"
    }

    override fun onBtnClick(position: Int, dataX: TikcetlistNew.Ticket?) {
        val Intent = Intent(context, ViewTicketActivity::class.java)
        if (dataX != null) {
            Intent.putExtra("TicketNo",dataX.ticket_no)
            Intent.putExtra("Subject",dataX.category)
            Intent.putExtra("Stauts",dataX.status)
            Intent.putExtra("TicketId",dataX.ticket_id)
            Intent.putExtra("pos",position)
            startForResult.launch(Intent)
        }

    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if(intent !=null){
                val position  = intent.getIntExtra("pos",0);
                val dataX = position.let { globalList.get(it) }
                //globalList.find { it.ticket_id == dataX.ticket_id }?.status == "Completed"
                globalList.filter { it.ticket_id == dataX.ticket_id }.forEach { it.status = "Completed" }
                ticketAdapter.notifyItemChanged(position)
                // position.let { ticketAdapter.notifyItemChanged(it) }
            }
            // Handle the Intent
        }
    }


}
