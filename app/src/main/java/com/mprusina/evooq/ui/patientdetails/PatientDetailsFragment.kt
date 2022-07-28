package com.mprusina.evooq.ui.patientdetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mprusina.evooq.R
import com.mprusina.evooq.data.entities.Drug
import com.mprusina.evooq.databinding.FragmentPatientDetailsBinding
import com.mprusina.evooq.ui.SimulatorViewModel
import com.mprusina.evooq.ui.patientdetails.utils.DetailsDrugListAdapter
import com.mprusina.evooq.utils.isConnectedToNetwork
import com.mprusina.hospital_lib.DataSymbolToNameMap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PatientDetailsFragment : Fragment(), DetailsDrugListAdapter.OnMyClickListener {

    private lateinit var binding: FragmentPatientDetailsBinding
    private val viewModel: SimulatorViewModel by activityViewModels()
    private val safeArgs: PatientDetailsFragmentArgs by navArgs()
    private lateinit var detailsDrugAdapter: DetailsDrugListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPatientDetailsBinding.inflate(inflater, container, false)
        detailsDrugAdapter = DetailsDrugListAdapter(this)

        with(binding.detailsPatientDrugList) {
            layoutManager = LinearLayoutManager(context)
            adapter = detailsDrugAdapter
        }

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getDrugs().observe(viewLifecycleOwner) { drugData ->
            if (drugData.isNotEmpty()) {
                updateUiDisplayData()
                detailsDrugAdapter.submitList(drugData)
            } else {
                updateUiDisplayError(getString(R.string.error_no_data_message))
            }
        }

        viewModel.getPatientWithId(safeArgs.patientId).observe(viewLifecycleOwner) { patient ->
            if (patient != null) {
                binding.tvDetailsPatientId.text = patient.id.toString()
                binding.tvDetailsPatientState.text = patient.stateName
                binding.tvDetailsPatientStateSymbol.text = patient.stateSymbol

                when (patient.stateName) {
                    DataSymbolToNameMap.patientSymbolToNameMap["X"] -> {
                        updateUiForXPatient()
                    }
                }
            }
        }

        binding.detailsButtonAdministerDrugs.setOnClickListener {
            viewModel.administerDrugsToPatient()
            detailsDrugAdapter.notifyDataSetChanged()
        }

        binding.detailsButtonGetData.setOnClickListener {
            if (isConnectedToNetwork(context)) {
                binding.detailsDrugLoadingIndicator.visibility = View.VISIBLE
                viewModel.fetchDrugData()
            } else {
                Toast.makeText(context, getString(R.string.error_no_network_message), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isFetchDrugDataResponseResultError().observe(viewLifecycleOwner) { responseError ->
            binding.detailsDrugLoadingIndicator.visibility = View.GONE
            if (responseError) {
                // If data already exists, don't hide it, instead display error in Toast message
                if (viewModel.getDrugs().value != null) {
                    Toast.makeText(context,getString(R.string.error_server_message), Toast.LENGTH_SHORT).show()
                } else {
                    updateUiDisplayError(getString(R.string.error_server_message))
                }
            } else {
                updateUiDisplayData()
            }
        }
    }

    private fun updateUiDisplayError(errorMessage: String) {
        binding.detailsPatientDrugList.visibility = View.GONE
        with(binding.tvDetailsStatusMessage) {
            text = errorMessage
            visibility = View.VISIBLE
        }
    }

    private fun updateUiDisplayData() {
        binding.tvDetailsStatusMessage.visibility = View.GONE
        binding.detailsPatientDrugList.visibility = View.VISIBLE
    }

    private fun updateUiForXPatient() {
        binding.detailsButtonAdministerDrugs.isEnabled = false
        binding.detailsButtonGetData.isEnabled = false
        binding.detailsPatientDrugList.visibility = View.GONE
        with(binding.tvDetailsStatusMessage) {
            text = getString(R.string.patient_state_dead)
            visibility = View.VISIBLE
        }
    }

    override fun onItemClick(drug: Drug, position: Int) {
        viewModel.updateSelectedDrugs(drug)
        detailsDrugAdapter.notifyItemChanged(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearAll()
    }
}