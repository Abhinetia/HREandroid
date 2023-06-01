package com.android.hre

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hre.api.RetrofitClient
import com.android.hre.databinding.PettycashaproveBinding
import com.android.hre.response.ApprovalPettyCash.AprrovalPettyCash
import com.android.hre.response.Getmaterials
import com.android.hre.response.pettycashDetails.PettyCashDetails
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class FullScreenBottomSheetDialogPettyCash(context: Context?) : BottomSheetDialogFragment() {

    private lateinit var binding: PettycashaproveBinding
    var userid : String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.pettycashaprove, container, false)

        binding = PettycashaproveBinding.inflate(inflater, container, false)


        val sharedPreferences = context?.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences?.getString("user_id", "")!!



        binding.ivnotification.setOnClickListener {
            dialog?.dismiss()
        }

        fetchThePettyCashStatus()


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

    private fun fetchThePettyCashStatus() {

        RetrofitClient.instance.getPettyCashapproval(userid, "5")
            .enqueue(object: retrofit2.Callback<AprrovalPettyCash> {
                override fun onFailure(call: Call<AprrovalPettyCash>, t: Throwable) {
                    Toast.makeText(context, "Reqeusted Id not Found", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<AprrovalPettyCash>, response: Response<AprrovalPettyCash>) {
                    Log.v("Sucess",response.body().toString())
//
                    var listMaterials: AprrovalPettyCash? = response.body()
                    var arrayList_details: List<AprrovalPettyCash.Data>? = listMaterials?.data

                    for (i in 0 until arrayList_details?.size!!) {
                        val dataString: AprrovalPettyCash.Data = arrayList_details.get(i)
                        cashLogic(dataString)

                    }

                }
            })

    }
    @SuppressLint("MissingInflatedId")
    private fun cashLogic(datax :AprrovalPettyCash.Data) {

        val infalot = LayoutInflater.from(context)
        val custrom = infalot.inflate(R.layout.cashaprove,null)

        val tvDate = custrom.findViewById<TextView>(R.id.tv_displaydaee) // date
        val  tvamount = custrom.findViewById<TextView>(R.id.tv_tmee) // amount
        val  tvreason = custrom.findViewById<TextView>(R.id.tv_timee) // reason
        val tvdetails = custrom.findViewById<TextView>(R.id.tv_tvdeatils)
        val tvstatus = custrom.findViewById<TextView>(R.id.tv_stsua)
        val imageView = custrom.findViewById<ImageView>(R.id.myImageView)
        val attachment = custrom.findViewById<TextView>(R.id.tv_tvdeatils)

        imageView.visibility = View.INVISIBLE

        tvamount.text = datax.spent_amount
        tvreason.text = datax.comments
        tvstatus.text = datax.isapproved


            imageView.visibility = View.VISIBLE
            val imageUrl = datax.filepath + datax.filename    // "https://example.com/image.jpg"

            Glide.with(this)
                .load(imageUrl)
                .into(imageView)


        imageView.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_image_preview)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val previewImageView = dialog.findViewById<ImageView>(R.id.previewImageView)
            val ivnotification = dialog.findViewById<ImageView>(R.id.iv_cancel)
            Glide.with(this)
                .load(imageUrl)
                .into(previewImageView)

            ivnotification.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }


        val inputDateString = datax.date
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = inputFormat.parse(inputDateString)
        val outputDateString = outputFormat.format(date)
        tvDate.text  = outputDateString



        binding.linearLayoutGridLevelSinglePiece.addView(custrom)
    }

}