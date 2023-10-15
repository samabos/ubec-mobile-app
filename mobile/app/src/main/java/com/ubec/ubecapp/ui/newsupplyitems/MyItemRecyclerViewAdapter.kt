package com.ubec.ubecapp.ui.newsupplyitems

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.ubec.ubecapp.R
import com.ubec.ubecapp.data.DBHelper
import com.ubec.ubecapp.data.model.SupplyItemModel
import com.ubec.ubecapp.databinding.FragmentItemwithdeleteBinding

import com.ubec.ubecapp.ui.newsupplyitems.placeholder.PlaceholderContent.PlaceholderItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(
    private var values: List<SupplyItemModel>,
    private val activity : Activity,
    private val db : DBHelper,
    private val listener: (SupplyItemModel) -> Unit
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(FragmentItemwithdeleteBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.serialNo
        holder.contentView.text = item.description
        holder.itemView.setOnClickListener { listener(item) }
        holder.button.setOnClickListener{
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("Are you sure you want to Delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    // Delete selected note from database
                    if(db.deleteSupplyItem(item.id.toString())){
                        values = db.getSupplyItems(item.supplyId!!)
                        notifyDataSetChanged();
                    }
                }
                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemwithdeleteBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        val button : Button = binding.btnDeleteItem

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}