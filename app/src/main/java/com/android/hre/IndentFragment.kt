package com.android.hre

import android.app.Dialog
import android.content.ClipData.Item
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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.adapter.HomeAdapter
import com.android.hre.adapter.HomeAdapter2
import com.android.hre.adapter.HomeAdapterNew
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentIndentBinding
import com.android.hre.response.getappdata.AppDetails
import com.android.hre.response.newindentrepo.NewIndents
import com.android.hre.response.searchindent.SearchIndent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class IndentFragment : Fragment() {

    private var _binding: FragmentIndentBinding? = null

    private val binding get() = _binding!!
    var userid : String = ""
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var homeAdapter2: HomeAdapter2
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var activeList :ArrayList<NewIndents.Myindent> = arrayListOf()
    var completedList :ArrayList<NewIndents.Myindent> = arrayListOf()


    var isActive : Boolean = true
    private var repositoryList: List<NewIndents> = emptyList()

    private lateinit var handler: Handler
    private var timerRunnable : Runnable? = null
    private val delayMillis = 1500L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentIndentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        handler = Handler(Looper.getMainLooper())
        sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
        var name = sharedPreferences?.getString("username", "")
        userid = sharedPreferences?.getString("user_id", "")!!
        homeAdapter = HomeAdapter()

        // homeAdapterNew = HomeAdapterNew()

        if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
            fetchtheappData()
        }

        fetchTheIndentList()

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()

        }
        binding.tvActive.setOnClickListener {
            if(!isActive){
                isActive = true
                Log.v("TAGActive",activeList.reversed().toString())
                //homeAdapter.differ.submitList(activeList.reversed())
                //homeAdapter.notifyDataSetChanged()
                homeAdapter2.updateItems(activeList.reversed())
                binding.ivActive.visibility = View.VISIBLE
                binding.ivCompleted.visibility = View.GONE
            }
            if(activeList.size == 0){
                binding.tvShowPening.text = "No Active intend"
                binding.tvShowPening.visibility = View.VISIBLE
            }else{
                binding.tvShowPening.text = ""
                binding.tvShowPening.visibility = View.GONE
            }

        }
        binding.tvComplted.setOnClickListener {
            if(isActive){
                isActive = false
                Log.v("TAGCompleted",completedList.reversed().toString())
                homeAdapter2.updateItems(completedList.reversed())

//                homeAdapter.differ.submitList(completedList.reversed())
//                homeAdapter.notifyDataSetChanged()
                binding.ivCompleted.visibility = View.VISIBLE
                binding.ivActive.visibility = View.GONE
            }
            if(completedList.size == 0){
                binding.tvShowPening.text = "No Completed intend"
                binding.tvShowPening.visibility = View.VISIBLE
            }else{
                binding.tvShowPening.text = ""
                binding.tvShowPening.visibility = View.GONE
            }
        }
   binding.btnCretaeTicket.setOnClickListener {
       val intent = Intent(context,CaretingIndeNewActivity::class.java)
       startActivity(intent)

   }

        binding.ivsearch.setOnClickListener {
            binding.carviewseacrh.visibility = View.VISIBLE
        }

        binding.etsearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().length == 0){
                    binding.ivProgressBar.visibility = View.GONE
                }else{
                    binding.ivProgressBar.visibility = View.VISIBLE
                }
                timerRunnable?.let { handler.removeCallbacks(it) }

                // Schedule a new timerRunnable with the specified delay
                timerRunnable = Runnable {

                    fetchTheSearchIndentList(s.toString())
//                    }else if(s.toString().length == 0){
//                        fetchTheIndentList()
//                    }
                }

                handler.postDelayed(timerRunnable!!, delayMillis)



            }

            override fun afterTextChanged(s: Editable?) {
                // Do Nothing
            }
        })


        return root
    }

    private fun fetchTheSearchIndentList( search:String ) {
        activeList.clear()
        completedList.clear()
        val call = RetrofitClient.instance.getindents(userid,search,sharedPreferences?.getString("role", "")!!)
        call.enqueue(object : Callback<NewIndents> {
            override fun onResponse(call: Call<NewIndents>, response: Response<NewIndents>) {
//
                if (response.isSuccessful) {
                    val indentResponse = response.body()

                    if (indentResponse != null && indentResponse.status == 1) {
                        binding.ivProgressBar.visibility = View.GONE

                        val myIndents = indentResponse.data.myindents

                        for (i in 0 until myIndents.size ){
                            val myindent = myIndents.get(i)
                            if(myindent.status.equals("Active")){
                                activeList.add(myindent)
                            }else{
                                completedList.add(myindent)
                            }
                        }

                        if(isActive){
                           // homeAdapter.differ.submitList(activeList.reversed())
                            homeAdapter2.updateItems(activeList.reversed())
                            if(activeList.size == 0){
                                binding.tvShowPening.text = "No Active intend"
                                binding.tvShowPening.visibility = View.VISIBLE
                            }else{
                                binding.tvShowPening.text = ""
                                binding.tvShowPening.visibility = View.GONE
                            }
                        }else{
                          //  homeAdapter.differ.submitList(completedList.reversed())
                            homeAdapter2.updateItems(completedList.reversed())
                            if(completedList.size == 0){
                                binding.tvShowPening.text = "No Completed intend"
                                binding.tvShowPening.visibility = View.VISIBLE
                            }else{
                                binding.tvShowPening.text = ""
                                binding.tvShowPening.visibility = View.GONE
                            }
                        }
                        //homeAdapter.notifyDataSetChanged()

                        binding.countof.text = activeList.size.toString()
                        binding.countcompl.text = completedList.size.toString()

//                        binding.rvRecylergrndata.apply {
//                            layoutManager = LinearLayoutManager(context)
//                            adapter = ho
//                        }
                        val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.windowToken, 0)



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

            override fun onFailure(call: Call<NewIndents>, t: Throwable) {
                // Handle network error
                Log.v("ERROR",t.toString())
            }
        })

    }




    private fun fetchTheIndentList() {
        activeList.clear()
        completedList.clear()
        val call = RetrofitClient.instance.getIndents(userid)
        call.enqueue(object : Callback<NewIndents> {
            override fun onResponse(call: Call<NewIndents>, response: Response<NewIndents>) {
//

                    if (response.isSuccessful) {
                        val indentResponse = response.body()

                        if (indentResponse != null && indentResponse.status == 1) {
                            val myIndents = indentResponse.data.myindents

                            for (i in 0 until myIndents.size ){
                                val myindent = myIndents.get(i)
                                if(myindent.status.equals("Active")){
                                    activeList.add(myindent)
                                }else{
                                    completedList.add(myindent)
                                }
                            }
                            if(isActive){
                               // homeAdapter.differ.submitList(activeList.reversed())
                                binding.ivCompleted.visibility = View.GONE
                                binding.ivActive.visibility = View.VISIBLE
                            }else{
                                //homeAdapter.differ.submitList(completedList.reversed())

                                binding.ivCompleted.visibility = View.VISIBLE
                                binding.ivActive.visibility = View.GONE
                            }

                            homeAdapter2 = HomeAdapter2(activeList.reversed(),context!!)

                            binding.countof.text = indentResponse.data.counts.Active.toString()
                            binding.countcompl.text = indentResponse.data.counts.Completed.toString()

                            binding.rvRecylergrndata.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = homeAdapter2
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

            override fun onFailure(call: Call<NewIndents>, t: Throwable) {
                // Handle network error
            }
        })

    }


    private fun fetchtheappData() {
        val version = getAppVersion(requireContext())
        val call = RetrofitClient.instance.getappData(userid,version)
        call.enqueue(object : Callback<AppDetails> {
            override fun onResponse(call: Call<AppDetails>, response: Response<AppDetails>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    Log.v("dat", dataList.toString())


                    println("App version: $version")

                  /*  if (!dataList!!.need_update.equals("No")){
                        showAlertDialogOkAndCloseAfter("Please Use the latest Application of ARCHIVE")
                        return
                    }
                    if(dataList!!.app_version > version){
                        showAlertDialogOkAndCloseAfter("Please Use the latest Application of HRETeams")
                        return
                    }*/
                    if (!dataList!!.need_update.equals("No") && dataList.app_version > version){
                        showAlertDialogOkAndCloseAfter("Upadte The HRETeams APK ")
                        return
                    }
                    if((dataList!!.app_version > version) && dataList!!.need_update.equals("No")){
                        showAlertDialogOkAndCloseAfter("NewApk Is Available For Update")
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



}