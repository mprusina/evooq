package com.mprusina.evooq.ui.druglist.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mprusina.evooq.data.entities.Drug
import com.mprusina.evooq.databinding.FragmentDrugListRowBinding

class DrugListViewHolder(private val binding: FragmentDrugListRowBinding) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var item: Drug

    fun bind(item: Drug) {
        this.item = item

        binding.tvDrugId.text = item.id.toString()
        binding.tvDrugName.text = item.drugName
        binding.tvDrugSymbol.text = item.drugSymbol
    }

    companion object {
        fun create(parent: ViewGroup) : DrugListViewHolder {
            val drugListItemBinding = FragmentDrugListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DrugListViewHolder(drugListItemBinding)
        }
    }
}