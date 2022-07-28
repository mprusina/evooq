package com.mprusina.evooq.ui.patientlist.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mprusina.evooq.data.entities.Patient
import com.mprusina.evooq.databinding.FragmentPatientListRowBinding

class PatientListViewHolder(private val binding: FragmentPatientListRowBinding, private val onMyClickListener: PatientListAdapter.OnMyClickListener) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private lateinit var item: Patient

    fun bind(item: Patient) {
        this.item = item

        binding.tvPatientId.text = item.id.toString()
        binding.tvPatientState.text = item.stateName
        binding.tvPatientStateSymbol.text = item.stateSymbol

        binding.btnDrugSelectionDialog.setOnClickListener {
            this.onMyClickListener.onItemButtonClick(item)
        }

        binding.root.setOnClickListener(this)
    }

    companion object {
        fun create(parent: ViewGroup, onMyClickListener: PatientListAdapter.OnMyClickListener) : PatientListViewHolder {
            val patientListItemBinding = FragmentPatientListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PatientListViewHolder(patientListItemBinding, onMyClickListener)
        }
    }

    override fun onClick(v: View?) {
        this.onMyClickListener.onItemClick(item)
    }
}