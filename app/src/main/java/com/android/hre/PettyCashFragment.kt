package com.android.hre

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.hre.databinding.FragmentAttendanceBinding
import com.android.hre.databinding.FragmentPettyCashBinding
import com.google.android.material.textfield.TextInputEditText


class PettyCashFragment : Fragment() {

    private lateinit var binding :FragmentPettyCashBinding
    var userid :String = ""
    var name :String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_petty_cash, container, false)

        binding = FragmentPettyCashBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!
        name = sharedPreferences?.getString("username", "")!!


        binding.btnMaterials.setOnClickListener {
            val inflater = LayoutInflater.from(context)
            val popupView = inflater.inflate(R.layout.popupcashrequest, null)

            val textViewtname = popupView.findViewById<TextInputEditText>(R.id.ti_qtyoperpr)

            textViewtname.setText(name.capitalize())


            val popupDialog = AlertDialog.Builder(context)
                .setView(popupView)
                .create()

            popupDialog.show()
        }


        return root
    }

}