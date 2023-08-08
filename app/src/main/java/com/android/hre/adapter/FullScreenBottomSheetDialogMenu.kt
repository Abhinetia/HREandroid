package com.android.hre.adapter

import android.R
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.android.hre.AttendanceFragment
import com.android.hre.CaretingIndeNewActivity
import com.android.hre.Constants
import com.android.hre.GRNFragment
import com.android.hre.IndentFragment
import com.android.hre.MainActivity
import com.android.hre.PettyCashScreenFragment
import com.android.hre.TicketFragment
import com.android.hre.databinding.BootommenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FullScreenBottomSheetDialogMenu (context: Context?) : BottomSheetDialogFragment() {

    private lateinit var binding: BootommenuBinding
    var userid : String = ""
    lateinit var sharedPreferences: SharedPreferences
    var pid :String = ""
    var name :String = ""
    var empId :String = ""
    var version :String = ""




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(com.android.hre.R.layout.bootommenu, container, false)

        binding = BootommenuBinding.inflate(inflater, container, false)


        sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)!!
        sharedPreferences?.getString("PettyCashId","defaultName")

        userid = sharedPreferences?.getString("user_id", "")!!
        name = sharedPreferences?.getString("username", "")!!
        empId = sharedPreferences?.getString("employee_id","")!!


        pid = sharedPreferences.getString("PID","0")!!
        Log.v("TAG","PID :"+ sharedPreferences.getString("PID","0"))

        version = getAppVersion(requireContext())
        println("App version: $version")



        binding.imgclose.setOnClickListener {
            val intent = Intent(context,MainActivity::class.java)
            startActivity(intent)
        }

        binding.createindent.setOnClickListener {
            val intent = Intent(context,CaretingIndeNewActivity::class.java)
            startActivity(intent)
        }

       binding.indent.setOnClickListener {
//

           val ft: FragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction()
           ft.replace(com.android.hre.R.id.nav_host_fragment_activity_main, IndentFragment())
           ft.addToBackStack(null)
           ft.commit()

       }

        binding.agttenda.setOnClickListener {
            val ft: FragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction()
            ft.replace(com.android.hre.R.id.nav_host_fragment_activity_main, AttendanceFragment())
            ft.addToBackStack(null)
            ft.commit()
        }

        binding.grnln.setOnClickListener {
            val ft: FragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction()
            ft.replace(com.android.hre.R.id.nav_host_fragment_activity_main, GRNFragment())
            ft.addToBackStack(null)
            ft.commit()
        }
        binding.tikcetda.setOnClickListener {
            val ft: FragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction()
            ft.replace(com.android.hre.R.id.nav_host_fragment_activity_main, TicketFragment())
            ft.addToBackStack(null)
            ft.commit()
        }
        binding.pettycashln.setOnClickListener {
            val ft: FragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction()
            ft.replace(com.android.hre.R.id.nav_host_fragment_activity_main, PettyCashScreenFragment())
            ft.addToBackStack(null)
            ft.commit()
        }

        binding.tvusername.text = name
        binding.tvEmpId.text = empId
        binding.tvVersion.text = version


        return binding.root

    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
//
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
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