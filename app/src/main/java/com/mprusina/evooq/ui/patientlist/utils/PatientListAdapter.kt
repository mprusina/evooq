package com.mprusina.evooq.ui.patientlist.utils

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mprusina.evooq.data.entities.Patient

class PatientListAdapter(private val onMyClickListener: OnMyClickListener) : ListAdapter<Patient, PatientListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientListViewHolder {
        return PatientListViewHolder.create(parent, this.onMyClickListener)
    }

    override fun onBindViewHolder(holder: PatientListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Patient>() {
            override fun areItemsTheSame(oldItem: Patient, newItem: Patient): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Patient, newItem: Patient): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnMyClickListener {
        fun onItemClick(item: Patient)
        fun onItemButtonClick(item: Patient)
    }
}