package com.mprusina.evooq.ui.druglist.utils

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mprusina.evooq.data.entities.Drug

class DrugListAdapter() : ListAdapter<Drug, DrugListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugListViewHolder {
        return DrugListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: DrugListViewHolder, position: Int) {
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
}