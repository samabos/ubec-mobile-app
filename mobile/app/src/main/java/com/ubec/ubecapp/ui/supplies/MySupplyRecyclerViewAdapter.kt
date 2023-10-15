package com.ubec.ubecapp.ui.supplies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.ubec.ubecapp.R
import com.ubec.ubecapp.data.model.SupplyItemModel
import com.ubec.ubecapp.data.model.SupplyModel
import com.ubec.ubecapp.databinding.FragmentSuppliesBinding

import com.ubec.ubecapp.ui.supplies.placeholder.PlaceholderContent.PlaceholderItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MySupplyRecyclerViewAdapter(
    private val values: List<SupplyModel>,
    private val navController: NavController,
    private val listener: (SupplyModel) -> Unit
) : RecyclerView.Adapter<MySupplyRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(FragmentSuppliesBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.contractor
        holder.itemView.setOnClickListener { listener(item) }

        holder.button.setOnClickListener{
            val bundle = bundleOf("id" to item.id )
            navController.navigate(R.id.action_nav_report_supplies_to_nav_new_supply_items ,bundle,null)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentSuppliesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        val button : Button = binding.btnAddItem

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}