package com.mprusina.evooq.ui.drugdialog.utils

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mprusina.evooq.data.entities.Drug

class DialogDrugListAdapter(private val onMyClickListener: OnMyClickListener) : ListAdapter<Drug, DialogDrugListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogDrugListViewHolder {
        return DialogDrugListViewHolder.create(parent, this.onMyClickListener)
    }

    override fun onBindViewHolder(holder: DialogDrugListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Drug>() {
            override fun areItemsTheSame(oldItem: Drug, newItem: Drug): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Drug, newItem: Drug): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnMyClickListener {
        fun onItemClick(drug: Drug, position: Int)
    }
}