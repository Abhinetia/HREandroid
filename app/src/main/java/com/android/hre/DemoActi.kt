package com.android.hre

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout


class DemoActi :AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.demo)

//        drawerLayout = findViewById(R.id.my_drawer_layout)
//        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
//
//
//        drawerLayout.addDrawerListener(actionBarDrawerToggle)
//        actionBarDrawerToggle.syncState()
//
//        val Imageview = findViewById<ImageView>(R.id.idsidenavigation)
//
//        Imageview.setOnClickListener {
//            drawerLayout.openDrawer(GravityCompat.START);
//
//        }


    }

}