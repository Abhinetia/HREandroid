package com.android.hre

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.hre.databinding.ActivityMainBinding
import com.android.hre.grn.DisplayGrnActivity
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var name :String = ""
    var role :String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.background = null

        sharedPreferences = getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)!!
        editor = sharedPreferences.edit()
        name = sharedPreferences?.getString("username", "")!!
        role = sharedPreferences?.getString("role_name","")!!

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navVieww = findViewById<NavigationView>(R.id.nav_vview)
        val navItem = navVieww.menu.findItem(R.id.grntv)




        //  val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)



        toggle = ActionBarDrawerToggle(
            this, // Host activity
            drawerLayout, // DrawerLayout object
            R.string.nav_drawer_open, // "open drawer" description for accessibility
            R.string.nav_drawer_close // "close drawer" description for accessibility
        )
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            ),   binding.drawerLayout
        )

//        findViewById<ImageView>(R.id.idsidenavigation).setOnClickListener {
//            drawerLayout.openDrawer(GravityCompat.START)
//        }

        binding.navVview.setupWithNavController(navController)

        binding.hello.text = "Hello"
        binding.name.text = name
        binding.Role.text = role
        binding.logout.setOnClickListener {
            //Toast.makeText(this, "Logout",Toast.LENGTH_SHORT).show()
            logout()
        }

        setupActionBarWithNavController(navController, appBarConfiguration)



        navItem?.setOnMenuItemClickListener {
            startActivity(Intent(this, DisplayGrnActivity::class.java))
            true
        }


        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        binding.fab.setOnClickListener {
            val Intent = Intent(this@MainActivity,CaretingIndeNewActivity::class.java)
            startActivity(Intent)
        }

        binding.idsidenavigation.setOnClickListener {
            if( binding.drawerLayout.isOpen()){
                binding.drawerLayout.close()
            }
            else{
                binding.drawerLayout.open()
            }
        }

        navView.background = null
        navView.menu.getItem(1).isEnabled = false
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
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
}