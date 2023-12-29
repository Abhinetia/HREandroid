package com.android.hre.adapter


    import android.content.Context
    import android.content.Intent
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.TextView
    import androidx.cardview.widget.CardView
    import androidx.recyclerview.widget.RecyclerView
    import com.android.hre.R
    import com.android.hre.response.newindentrepo.NewIndents
    import com.android.hre.viewmoreindent.ViewMoreIndentActivity
    import java.text.SimpleDateFormat
    import java.util.Locale

class HomeAdapter2(private var items: List<NewIndents.Myindent>,private val context :Context) : RecyclerView.Adapter<HomeAdapter2.ViewHolder>() {

         inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
              val tvpcn: TextView = itemView.findViewById(R.id.tvdisplaypcn)
             val tvstatus: TextView = itemView.findViewById(R.id.tv_indentstatus)
             val tvdisplayindentno: TextView = itemView.findViewById(R.id.tv_displayindent)
             val tvpcndetails: TextView = itemView.findViewById(R.id.tvdpcndatapcn)
             val tvdate: TextView = itemView.findViewById(R.id.tv_displaydate)
             val viewmore :  TextView = itemView.findViewById(R.id.tv_viewindents)
             val cardView : CardView = itemView.findViewById(R.id.carviewdata)
             val creatorname :TextView = itemView.findViewById(R.id.tvcretaornme)


         }

         override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
              val itemView = LayoutInflater.from(parent.context).inflate(R.layout.homeindentlist, parent, false)
              return ViewHolder(itemView)
             }

         override fun onBindViewHolder(holder: ViewHolder, position: Int) {
              val dataX = items[position]

             holder.tvpcn.text = dataX.pcn
             holder.tvstatus.text = dataX.status
             holder.tvdisplayindentno.text = dataX.indent_no
             holder.tvpcndetails.text = dataX.pcn_detail
             //tvDisplaydate.text = dataX.created_on
             holder.tvpcndetails.setSelected(true)
             holder.creatorname.text = dataX.creator_name

             if (dataX.status.equals("Completed")){
                 holder.tvstatus.setBackgroundResource(R.drawable.ic_greenbaby)
             } else{
                 holder.tvstatus.setBackgroundResource(R.drawable.cardgreen)
             }


             val inputDateString = dataX.created_on
             val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
             val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
             val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())


             val date = inputFormat.parse(inputDateString)
             val outputDateString = outputFormat.format(date)
             val outputTimeString = outputTimeFormat.format(date)

             holder.tvdate.text  = outputDateString +" "+ outputTimeString


             holder.viewmore.setOnClickListener {
                 Log.v("TAG",dataX.indent_id.toString())
                 val intent = Intent(context, ViewMoreIndentActivity::class.java)
                 intent.putExtra("indentid",dataX.indent_id.toString())
                 intent.putExtra("pcn",dataX.pcn)
                 intent.putExtra("pcndetails",dataX.pcn_detail)
                 context.startActivity(intent)
             }

             holder.cardView.setOnClickListener {
                 Log.v("TAG",dataX.indent_id.toString())
                 val intent = Intent(context, ViewMoreIndentActivity::class.java)
                 intent.putExtra("indentid",dataX.indent_id.toString())
                 intent.putExtra("pcn",dataX.pcn)
                 intent.putExtra("pcndetails",dataX.pcn_detail)
                 context.startActivity(intent)
             }

         }

         override fun getItemCount(): Int {
              return items.size
             }

         fun updateItems(newItems: List<NewIndents.Myindent>) {
              items = newItems
              notifyDataSetChanged()
             }
    }
