package com.android.hre

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.ActivityMainBinding
import com.android.hre.response.getappdata.AppDetails
import com.android.hre.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

   // private lateinit var toggle: ActionBarDrawerToggle
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var name :String = ""
    var role :String = ""
    var empId :String = ""
    var version :String = ""
    var userid : String = ""



    @SuppressLint("StringFormatInvalid")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.background = null

        sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)!!

        editor = sharedPreferences.edit()

        Log.v("Main", sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false).toString())

        userid = sharedPreferences?.getString("user_id", "")!!

        name = sharedPreferences?.getString("username", "")!!
        role = sharedPreferences?.getString("role_name","")!!
        empId = sharedPreferences?.getString("employee_id","")!!

       /* context.getSystemService(INPUT_METHOD_SERVICE)
            .hideSoftInputFromWindow(
                this.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )*/


        //   navController = findNavController(R.id.nav_host_fragment_activity_main)
      //  val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
       // val navVieww = findViewById<NavigationView>(R.id.nav_vview)
       // val navItem = navVieww.menu.findItem(R.id.grntv)


        //replaceFragment(HomeFragment())

        if (savedInstanceState == null) {
            val initialFragment = HomeFragment() // Replace with your initial fragment
            replaceFragment(initialFragment)

            // Set the first icon as selected
            binding.navView.selectedItemId = R.id.navigation_home
        }



        //  val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        version = getAppVersion(this)
        println("App version: $version")




//        toggle = ActionBarDrawerToggle(
//            this, // Host activity
//           // drawerLayout, // DrawerLayout object
//            R.string.nav_drawer_open, // "open drawer" description for accessibility
//            R.string.nav_drawer_close // "close drawer" description for accessibility
//        )
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            ),  // binding.drawerLayout
        )

//        findViewById<ImageView>(R.id.idsidenavigation).setOnClickListener {
//            drawerLayout.openDrawer(GravityCompat.START)
//        }

//        binding.navVview.setupWithNavController(navController)
//
//        binding.hello.text = "Hello"
//        binding.name.text = name
//        binding.Role.text = role
//        binding.empid.text = empId
//        binding.tvVersion.text = "Version : " +  version
//
//        binding.logout.setOnClickListener {
//            //Toast.makeText(this, "Logout",Toast.LENGTH_SHORT).show()
//            logout()
//        }

     //   setupActionBarWithNavController(navController, appBarConfiguration)



//        navItem?.setOnMenuItemClickListener {
//            startActivity(Intent(this, DisplayGrnActivity::class.java))
//            true
//        }
//
//
//        drawerLayout.addDrawerListener(toggle)
       // toggle.syncState()


      /*  binding.fab.setOnClickListener {

//            val fullScreenBottomSheetDialogFragment = FullScreenBottomSheetDialogMenu(this)
//            fullScreenBottomSheetDialogFragment.show(supportFragmentManager, FullScreenBottomSheetDialogMenu::class.simpleName)
//
//            binding.fab.visibility = View.INVISIBLE
            if(sharedPreferences.getBoolean(Constants.ISLOGGEDIN,false)){
                fetchtheappData()
            }

            val Intent = Intent(this@MainActivity,CaretingIndeNewActivity::class.java)
            startActivity(Intent)
        }*/

        binding.idsidenavigation.setOnClickListener {
//            if( binding.drawerLayout.isOpen()){
//                binding.drawerLayout.close()
//            }
//            else{
//                binding.drawerLayout.open()
//            }
        }

//        navView.background = null
        navView.menu.getItem(1).isEnabled = false    //
        navView.menu.getItem(2).isEnabled = false    //        android:icon="@drawable/ic_home_black_24dp"
//      android:icon="@drawable/ic_home_black_24dp"
//       // setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)

        val todaydate = LocalDate.now()
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = sdf.format(Date())
        println("Months first date in yyyy-mm-dd: " + todaydate.withDayOfMonth(1) + "  " + currentDate)

        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> replaceFragment(HomeFragment())
               // R.id.navigation_notifications -> replaceFragment(AttendanceFragment()).
               // navView.getCheckedItem().setTitle(".")
               // R.id.navigation_notifications -> setTitle(empId)

            }
            true
        }

        val item : MenuItem = binding.navView.menu.findItem(R.id.navigation_notifications)
        item.title = "$empId"





    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (toggle.onOptionsItemSelected(item)) {
//            return true
//        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
      //  val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(binding.framelayout.id)
        if (currentFragment is HomeFragment) {
            finish() // Close the activity if FragmentA is displayed
        } else {
            when (currentFragment) {
                is AttendanceFragment -> binding.navView.selectedItemId = R.id.navigation_notifications
                // Handle other fragments
            }
            super.onBackPressed()
        }
    }


    fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = this@MainActivity.supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Replace the current fragment with the new fragment
        transaction.replace(binding.framelayout.id, fragment)

        // Add the transaction to the back stack
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }


    private fun  logout() {
        // Update the user authentication state to indicate logout
        // For example, you might have a boolean variable isLoggedIn that you set to false.
      //  isLoggedIn = false

        // Clear cache from SharedPreferences
        val sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // Navigate to the login screen or any other desired screen
        val intent = Intent(this, SplashScreenAtivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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



    fun openDashboard(){
        var intent =  Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun openDataLogin(){
        var intent =  Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showAlertDialogOkAndCloseAfter(alertMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(alertMessage)
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i ->  }  // LoginActivity::class.java
        val alertDialog: Dialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }


}