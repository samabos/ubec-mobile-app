package com.ubec.ubecapp.ui.reports

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ubec.ubecapp.R

class MyListAdapter(private val context: Activity, private val id: Array<String>, private val project: Array<String?>,
                    private val contractor: Array<String?>, private val status:Array<String?>)
    : ArrayAdapter<String>(context, R.layout.custom_list, id) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list, null, true)

        val idText = rowView.findViewById(R.id.mtrl_list_item_id_text) as TextView
        val projectText = rowView.findViewById(R.id.mtrl_list_item_text) as TextView
        val contractorText = rowView.findViewById(R.id.mtrl_list_item_secondary_text) as TextView
        val statusImg = rowView.findViewById(R.id.imageView1) as ImageView

        if(status[position] == "Submitted"){
            statusImg.setImageResource(R.drawable.ic_baseline_cloud_upload)
        }else{
            statusImg.setImageResource(R.drawable.ic_baseline_cloud_upload_red)
        }
        idText.text = "Id: ${id[position]}"
        projectText.text = "Project: ${project[position]}"
        contractorText.text = "Contractor: ${contractor[position]}"

        return rowView
    }

    fun getId(position:Int):String{
        return id[position]
    }
}