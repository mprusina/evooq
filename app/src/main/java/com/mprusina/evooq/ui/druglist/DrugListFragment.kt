package com.mprusina.evooq.ui.druglist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mprusina.evooq.databinding.FragmentDrugListBinding
import com.mprusina.evooq.ui.SimulatorViewModel
import com.mprusina.evooq.ui.druglist.utils.DrugListAdapter

class DrugListFragment : Fragment() {

    private lateinit var binding: FragmentDrugListBinding
    private lateinit var drugAdapter: DrugListAdapter
    private val viewModel: SimulatorViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDrugListBinding.inflate(inflater, container, false)
        drugAdapter = DrugListAdapter()

        with(binding.drugList) {
            layoutManager = LinearLayoutManager(context)
            adapter = drugAdapter
        }

        return binding.root
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getDrugs().observe(viewLifecycleOwner) { drugData ->
            if (!drugData.isNullOrEmpty()) {
                updateUiDisplayData()
                drugAdapter.submitList(drugData)
            } else {
                updateUiDisplayError()
            }
        }
    }

    private fun updateUiDisplayError() {
        binding.drugList.visibility = View.GONE
        binding.tvDrugNoDataMessage.visibility = View.VISIBLE
    }

    private fun updateUiDisplayData() {
        binding.tvDrugNoDataMessage.visibility = View.GONE
        binding.drugList.visibility = View.VISIBLE
    }
}