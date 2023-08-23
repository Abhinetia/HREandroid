package com.android.hre.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.Constants
import com.android.hre.LocationHelper
import com.android.hre.adapter.HomeAdapter
import com.android.hre.adapter.HomeAdapterNew
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentHomeBinding
import com.android.hre.grn.DisplayGrnActivity
import com.android.hre.response.attenda.LoginpageAttendance
import com.android.hre.response.homeindents.GetIndentsHome
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.ncorti.slidetoact.SlideToActView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random
import com.google.android.gms.location.*
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.android.hre.AttendanceActivity
import com.android.hre.AttendanceFragment
import com.android.hre.GRNFragment
import com.android.hre.GRnActivity
import com.android.hre.IndentActivity
import com.android.hre.IndentFragment
import com.android.hre.LoginActivity
import com.android.hre.MainActivity
import com.android.hre.PettyCashActivity
import com.android.hre.PettyCashScreenFragment
import com.android.hre.TicketActivity
import com.android.hre.TicketFragment
import com.android.hre.VaultActivity
import com.android.hre.VaultFragment
import com.android.hre.response.getappdata.AppDetails
import com.android.hre.response.homeresponse.DashbardData
import com.android.hre.response.newindentrepo.NewIndents
import com.android.hre.response.vaults.VaultDetails


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    var userid : String = ""
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var homeAdapterNew: HomeAdapterNew
    var attendanceOn :Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
     var  currentDateAndTime : String = ""
    lateinit var simpleDateFormat : DateFormat
        var latitude : Double = 0.0
    var longitude :Double = 0.0
    var address :String = ""
    lateinit var sharedPreferences :SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    private lateinit var locationHelper: LocationHelper
    var version :String = ""

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, proceed with location retrieval
            getCurrentLocation()
        } else {
            // Permission denied, handle accordingly
        }
    }



    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requestLocationPermissionn()


         sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
        var name = sharedPreferences?.getString("username", "")
        var empId = sharedPreferences?.getString("employee_id","")
        userid = sharedPreferences?.getString("user_id", "")!!
        binding.tvDisplay.text = name
//        binding.tvEmpId.text = empId
//        binding.tvusername.text = name
//        binding.tvVersion.text= version

        val simpleDate = SimpleDateFormat("dd/M/yyyy ")
        val currentDate = simpleDate.format(Date())
        println(" Current Date is: " +currentDate)
      //  binding.tvPending.text = "Date : " + currentDate

//        binding.tvPending.text = "Name" + name
        binding.hellodat.text = "Welcome"
//        binding.nameee.text = name
//        binding.empiddd.text = empId

        Log.v("Sharedpref", sharedPreferences?.getBoolean(Constants.ISLOGGEDIN,false).toString())
        if (sharedPreferences.getBoolean(Constants.isEmployeeLoggedIn,false)){
            binding.slider.isReversed = true
            binding.slider.resetSlider()
            attendanceOn = true
        } else{
            binding.slider.isReversed = false
            binding.slider.resetSlider()
            attendanceOn = false
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        locationHelper = LocationHelper(requireContext())


        homeAdapter = HomeAdapter()
       // homeAdapterNew = HomeAdapterNew()


        /*if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
            fetchtheappData()
        }*/

        fetchTheIndentList()

        fetchDashboardData()



        if (checkLocationPermission()) {
            getLastLocation()
        } else {
            requestLocationPermission()
        }

        binding.indentdata.setOnClickListener {
//            val intent = Intent(context,IndentActivity::class.java)
//            startActivity(intent)

//            val ft: FragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction()
//            ft.replace(com.android.hre.R.id.nav_host_fragment_activity_main, IndentFragment())
//            ft.addToBackStack(null)
//            ft.commit()
           // replaceFragment(IndentFragment())
        }
        binding.ticketdata.setOnClickListener {
            val intent = Intent(context,TicketActivity::class.java)
            startActivity(intent)
        }
        binding.grndata.setOnClickListener {
            val intent = Intent(context,GRnActivity::class.java)
            startActivity(intent)
        }
        binding.pettydata.setOnClickListener {
            val intent = Intent(context,PettyCashActivity::class.java)
            startActivity(intent)
        }
        binding.vaultdata.setOnClickListener {
            val intent = Intent(context,VaultActivity::class.java)
            startActivity(intent)
        }
       binding.linearattendance.setOnClickListener {
           val intent = Intent(context,AttendanceActivity::class.java)
           startActivity(intent)
       }

        return root
    }

    fun replaceFragmentIntent() {
        if (activity is MainActivity) {
            val indentFragment = IndentFragment()
            (activity as MainActivity).replaceFragment(indentFragment)
        }
    }

    private fun fetchTheIndentList() {
        val call = RetrofitClient.instance.getIndents(userid)
        call.enqueue(object : Callback<NewIndents> {
            override fun onResponse(call: Call<NewIndents>, response: Response<NewIndents>) {
//
                    if (response.isSuccessful) {
                        val indentResponse = response.body()

                        if (indentResponse != null && indentResponse.status == 1) {
                            val myIndents = indentResponse.data.myindents
                            homeAdapter.differ.submitList(myIndents.reversed())

                            binding.rvRecylergrndata.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = homeAdapter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivnotificatn.setOnClickListener {
            findNavController().navigate(com.android.hre.R.id.action_navigation_home_to_ticketFragment)
        }

        binding.logo.setOnClickListener {
            findNavController().navigate(com.android.hre.R.id.action_navigation_home_to_indentFragment2)
        }

        binding.tvGrn.setOnClickListener {
            val Intent = Intent(context, DisplayGrnActivity::class.java)
            startActivity(Intent)
        }
        binding.indentdata.setOnClickListener {
            val fragmentB = IndentFragment()
            (activity as MainActivity).replaceFragment(fragmentB)
        }

        binding.ticketdata.setOnClickListener {
            val fragmentC = TicketFragment()
            (activity as MainActivity).replaceFragment(fragmentC)
        }

        binding.grndata.setOnClickListener {
            val fragmentD = GRNFragment()
            (activity as MainActivity).replaceFragment(fragmentD)
        }

        binding.pettydata.setOnClickListener {
            val fragmentE = PettyCashScreenFragment()
            (activity as MainActivity).replaceFragment(fragmentE)
        }
        binding.linearattendance.setOnClickListener {
            val fragmentF = AttendanceFragment()
            (activity as MainActivity).replaceFragment(fragmentF)
        }

        binding.vaultdata.setOnClickListener {
            val fragmentG = VaultFragment()
            (activity as MainActivity).replaceFragment(fragmentG)
        }



        binding.slider.onSlideCompleteListener =  object : SlideToActView.OnSlideCompleteListener {
           override fun onSlideComplete(view: SlideToActView) {
               if (!attendanceOn){
                   attendanceOn = true
                   binding.slider.isReversed = true
                   binding.slider.resetSlider()
                    simpleDateFormat = SimpleDateFormat("HH:mm")
                    currentDateAndTime = simpleDateFormat.format(Date())
                   Log.v("location","$currentDateAndTime")
                   binding.slider.text = "Attendance Logout"
                   getLastLocation()
                    var action = "login"
//                   editor.putBoolean("isLoggedIn",true)
//                   editor.apply()
//                   editor.commit()
                   loginattendance(action)
                   //fetchtheappData()
               } else{
                   attendanceOn = false
                   binding.slider.isReversed = false
                   binding.slider.resetSlider()
                   simpleDateFormat = SimpleDateFormat("HH:mm")
                   currentDateAndTime = simpleDateFormat.format(Date())
                   binding.slider.text = "Attendance Login"
                   getLastLocation()
                   var action = "logout"
//                   editor.putBoolean("isLoggedIn",false)
//                   editor.apply()
//                   editor.commit()
                   loginattendance(action)
                   Log.v("location","$currentDateAndTime")
                   //fetchtheappData()
               }

           }

    }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun checkLocationPermission(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun getLastLocation() {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    address = locationHelper.getAddressFromLocation(latitude, longitude)
                        .toString()

                   // address = getAddressFromLocation(requireContext(), latitude, longitude)!!
                    Log.v("location","$latitude+ $longitude + $address")

                }
            }
            .addOnFailureListener { exception: Exception ->
            }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }

    private fun loginattendance( action:String) {

        RetrofitClient.instance.getattendance(userid, action,currentDateAndTime,latitude,longitude,address)
            .enqueue(object: retrofit2.Callback<LoginpageAttendance> {
                override fun onFailure(call: Call<LoginpageAttendance>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<LoginpageAttendance>, response: Response<LoginpageAttendance>) {
                    Log.v("Sucess",response.body().toString())
                    val status = response.body()?.status
                    if (status?.equals(1)!!){
                       // Toast.makeText(context, "Login Successfull", Toast.LENGTH_LONG).show()
                        if (response.body()!!.message.contains("Login")){
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                            editor.putBoolean(Constants.isEmployeeLoggedIn,true)
                            editor.apply()
                            editor.commit()
                        } else if(response.body()!!.message.contains("Logout")){
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                            editor.putBoolean(Constants.isEmployeeLoggedIn,false)
                            editor.apply()
                            editor.commit()
                        } else if (response.body()!!.message.contains("Already loggedIn")){
                            binding.slider.isReversed = true
                            binding.slider.resetSlider()
                        }

                        Log.v("Sucess",status.toString())

                    }else{
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()

                    }

                }
            })


    }

   /* fun main() {
        // Get the current time
        val currentTime = Calendar.getInstance()

        val randomStartTime = Calendar.getInstance().apply {
            val randomHour = Random.nextInt(0, 24) // Generate a random hour (0-23)
            set(Calendar.HOUR_OF_DAY, randomHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val defaultEndTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val elapsedTime = currentTime.timeInMillis - randomStartTime.timeInMillis

        val elapsedHours = elapsedTime / (60 * 60 * 1000)

        val hasWorked8Hours = elapsedHours >= 8
        if (hasWorked8Hours) {
            Toast.makeText(context, "You have worked 8 hours or more.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "You have not worked 8 hours yet.", Toast.LENGTH_LONG).show()

        }
    }
*/
    private fun requestLocationPermissionn() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted, proceed with location retrieval
                getCurrentLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Explain the need for the permission if necessary
                // For example, show a dialog explaining why location access is required
            }
            else -> {
                // Request the permission from the user
                locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation = locationResult.lastLocation
                val latitude = lastLocation.latitude
                val longitude = lastLocation.longitude

                // Use the latitude and longitude values as needed
                Log.d("TAG", "Latitude: $latitude, Longitude: $longitude")
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    private fun fetchtheappData() {
        val call = RetrofitClient.instance.getappData(userid)
        call.enqueue(object : Callback<AppDetails> {
            override fun onResponse(call: Call<AppDetails>, response: Response<AppDetails>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    Log.v("dat", dataList.toString())

                     version = getAppVersion(context!!)
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
                        openDataLogin()
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

    private fun fetchDashboardData() {
        val call = RetrofitClient.instance.getDashbaordDetais(userid)
        call.enqueue(object : Callback<DashbardData> {
            override fun onResponse(call: Call<DashbardData>, response: Response<DashbardData>) {
                if (response.isSuccessful) {
                    val indentResponse = response.body()
                    val dataList = indentResponse?.data
                    Log.v("dat", dataList.toString())
                    binding.tvAattendd.text = dataList!!.attendance.toString()
                    if (dataList!!.attendance.equals("P")){
                        binding.tvAattendd.text = "Present"
                        binding.tvAattendd.setTextColor(Color.parseColor("#FF03DAC5"))
                    } else {
                        binding.tvAattendd.text = "Absent"
                        binding.tvAattendd.setTextColor(Color.parseColor("#F10909"))

                    }

                    binding.indentcount.text = dataList!!.indents_count.toString()
                    binding.tvgrncount.text =  dataList!!.grn_count
                    binding.tvticketcount.text =  dataList!!.tickets_count.toString()
                    binding.pettycashcount.text = "₹" + dataList!!.pettycash.toString()




                    val isLoggedIn = dataList!!.attendance
                    val appVersion = dataList!!.grn_count
                    val needUpdate = dataList!!.tickets_count
                    dataList.indents_count

                } else  {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<DashbardData>, t: Throwable) {
                // Handle network error
            }
        })
    }





}