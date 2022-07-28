package com.mprusina.evooq.ui.drugdialog.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mprusina.evooq.data.entities.Drug
import com.mprusina.evooq.databinding.FragmentDialogDrugSelectionListRowBinding

class DialogDrugListViewHolder(private val binding: FragmentDialogDrugSelectionListRowBinding, private val onMyClickListener: DialogDrugListAdapter.OnMyClickListener) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private lateinit var drug: Drug

    fun bind(drug: Drug) {
        this.drug = drug

        binding.tvDialogDrugId.text = drug.id.toString()
        binding.tvDialogDrugName.text = drug.drugName
        binding.tvDialogDrugSymbol.text = drug.drugSymbol
        binding.dialogDrugCheckbox.isChecked = drug.isSelected

        with(binding.dialogDrugCheckbox) {
            setOnClickListener {
                onMyClickListener.onItemClick(drug, bindingAdapterPosition)
            }
        }

        binding.root.setOnClickListener(this)
    }

    companion object {
        fun create(parent: ViewGroup, onMyClickListener: DialogDrugListAdapter.OnMyClickListener) : DialogDrugListViewHolder {
            val dialogDrugListAdapter = FragmentDialogDrugSelectionListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DialogDrugListViewHolder(dialogDrugListAdapter, onMyClickListener)
        }
    }

    override fun onClick(v: View?) {
        this.onMyClickListener.onItemClick(drug, bindingAdapterPosition)
    }
}