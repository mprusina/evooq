package com.mprusina.evooq.ui.patientdetails.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mprusina.evooq.data.entities.Drug
import com.mprusina.evooq.databinding.FragmentPatientDrugListRowBinding

class DetailsDrugListViewHolder(private val binding: FragmentPatientDrugListRowBinding, private val onMyClickListener: DetailsDrugListAdapter.OnMyClickListener) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private lateinit var drug: Drug

    fun bind(drug: Drug) {
        this.drug = drug

        binding.tvDetailsDrugId.text = drug.id.toString()
        binding.tvDetailsDrugName.text = drug.drugName
        binding.tvDetailsDrugSymbol.text = drug.drugSymbol
        binding.detailsDrugCheckbox.isChecked = drug.isSelected

        with(binding.detailsDrugCheckbox) {
            setOnClickListener {
                onMyClickListener.onItemClick(drug, bindingAdapterPosition)
            }
        }

        binding.root.setOnClickListener(this)
    }

    companion object {
        fun create(parent: ViewGroup, onMyClickListener: DetailsDrugListAdapter.OnMyClickListener) : DetailsDrugListViewHolder {
            val patientDrugListAdapter = FragmentPatientDrugListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DetailsDrugListViewHolder(patientDrugListAdapter, onMyClickListener)
        }
    }

    override fun onClick(v: View?) {
        this.onMyClickListener.onItemClick(drug, bindingAdapterPosition)
    }
}