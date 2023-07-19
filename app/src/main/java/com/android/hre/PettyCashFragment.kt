package com.android.hre

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.FragmentPettyCashBinding
import com.android.hre.response.pettycashDetails.PettyCashDetails
import com.android.hre.response.pettycashfirstscreen.PettyCashFirstScreen
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale


class PettyCashFragment : Fragment() {

    private lateinit var binding :FragmentPettyCashBinding
    var userid :String = ""
    var name :String = ""
    var totalBalance : Int = 0
    var remaingCash :Int = 0
    var spendCash :Int = 0
     var PettyId : String = ""
    lateinit var sharedPreferences :SharedPreferences
    lateinit var editor : SharedPreferences.Editor


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_petty_cash, container, false)

        binding = FragmentPettyCashBinding.inflate(inflater, container, false)
        val root: View = binding.root

         sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)!!
        editor= sharedPreferences.edit()
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
        binding.linerUploadbill.setOnClickListener {
            val intent = Intent(context,UploadExpensesActivity::class.java)
            // intent.putExtra("PettyCashId",datax.pettycash_id)
            startActivity(intent)
        }

        pettycashFromServer()


        return root
    }


    private fun pettycashFromServer() {

        RetrofitClient.instance.getpettycashDetails(userid)
            .enqueue(object: retrofit2.Callback<PettyCashFirstScreen> {
                override fun onFailure(call: Call<PettyCashFirstScreen>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<PettyCashFirstScreen>, response: Response<PettyCashFirstScreen>) {
                    Log.v("Sucess", response.body().toString())
                    var listMaterials: PettyCashFirstScreen? = response.body()


                    var arrayList_details: List<PettyCashFirstScreen.Data>? = listMaterials?.data

                    for (i in 0 until arrayList_details?.size!!) {
                        val dataString: PettyCashFirstScreen.Data = arrayList_details.get(i)
                        binding.pcnClinet.text = dataString.issued_amount.toString()
                        binding.tvexpense.text = dataString.balance_amount.toString()
                        //cashLogic(dataString)

                    }

                }

            })

    }

    @SuppressLint("MissingInflatedId")
    private fun cashLogic(datax :PettyCashFirstScreen.Data) {

        val infalot = LayoutInflater.from(context)
        val custrom = infalot.inflate(R.layout.pettycashlist,null)

        val tvDate = custrom.findViewById<TextView>(R.id.tv_displaydaee) // date
        val  tvamount = custrom.findViewById<TextView>(R.id.tv_tmee) // amount
        val  tvreason = custrom.findViewById<TextView>(R.id.tv_timee) // reason
        val tvdetails = custrom.findViewById<TextView>(R.id.tv_tvdeatils)
        val tvuoplodaExpense = custrom.findViewById<TextView>(R.id.tv_uploadexpense)
        val tvcashpettyId = custrom.findViewById<TextView>(R.id.tvpid)


      //  val  tvremaningCahs = custrom.findViewById<TextView>(R.id.tv_lotimee)  // remaing cash
       // val tvspendcash = custrom.findViewById<TextView>(R.id.tv_spendcash) // spendcash

      /*  totalBalance = totalBalance + datax.total_amount.toInt()
        remaingCash =remaingCash + datax.remaining_cash.toInt()
        spendCash = spendCash + datax.spended_cash.toInt()

        tvamount.text = datax.total_amount
        tvreason.text = datax.purpose
        tvcashpettyId.text = datax.pettycash_id
*/
        //tvremaningCahs.text = datax.remaining_cash
        //tvspendcash.text = datax.spended_cash


        tvdetails.setOnClickListener {
            PettyId = tvcashpettyId.text.toString()
            editor.putString("PID",PettyId)
            editor.commit()
            val fullScreenBottomSheetDialogFragment = FullScreenBottomSheetDialogPettyCash(context)
            fullScreenBottomSheetDialogFragment.show(parentFragmentManager, FullScreenBottomSheetDialogPettyCash::class.simpleName)

        }
        tvuoplodaExpense.setOnClickListener {
            val intent = Intent(context,UploadExpensesActivity::class.java)
          //  intent.putExtra("PettyCashId",datax.pettycash_id)
            startActivity(intent)

        }



      //  val inputDateString = datax.date
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
     //   val date = inputFormat.parse(inputDateString)
//        val outputDateString = outputFormat.format(date)
//        tvDate.text  = outputDateString



        binding.linearLayoutGridLevelSinglePiece.addView(custrom)
    }



}