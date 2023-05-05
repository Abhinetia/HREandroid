package com.android.hre

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import com.android.hre.databinding.FragmentHomeBinding
import com.android.hre.databinding.FragmentTicketBinding


class TicketFragment : Fragment() {

    lateinit var cretaeTicket: TextView
    lateinit var createviewReplies :TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ticket, container, false)



    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity?.let{
            cretaeTicket = it.findViewById(R.id.btn_cretae_ticket)
            createviewReplies = it.findViewById(R.id.tv_viewmore)


            cretaeTicket.setOnClickListener {
               val Intent = Intent(view.context,CreateTicketActivity::class.java)
                startActivity(Intent)
            }

            createviewReplies.setOnClickListener {
                val Intent = Intent(view.context,ViewTicketActivity::class.java)
                startActivity(Intent)
            }


        }

    }

}
