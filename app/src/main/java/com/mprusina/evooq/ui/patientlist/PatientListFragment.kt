package com.mprusina.evooq.ui.patientlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mprusina.evooq.R
import com.mprusina.evooq.data.entities.Patient
import com.mprusina.evooq.databinding.FragmentPatientListBinding
import com.mprusina.evooq.ui.HospitalFragmentDirections
import com.mprusina.evooq.ui.SimulatorViewModel
import com.mprusina.evooq.ui.drugdialog.DrugSelectionDialogFragment
import com.mprusina.evooq.ui.patientlist.utils.PatientListAdapter
import com.mprusina.hospital_lib.DataSymbolToNameMap

class PatientListFragment : Fragment(), PatientListAdapter.OnMyClickListener {

    private lateinit var binding: FragmentPatientListBinding
    private lateinit var patientAdapter: PatientListAdapter
    private val viewModel: SimulatorViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPatientListBinding.inflate(inflater, container, false)
        patientAdapter = PatientListAdapter(this)

        with(binding.patientList) {
            layoutManager = LinearLayoutManager(context)
            adapter = patientAdapter
        }

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPatients().observe(viewLifecycleOwner) { patientData ->
            if (!patientData.isNullOrEmpty()) {
                updateUiDisplayData()
                patientAdapter.submitList(patientData)
                // Calling notifyDataSetChanged() since displayed data is not updated after drug administration, as submitList() compares lists (references) and not list items
                patientAdapter.notifyDataSetChanged()
            } else {
                updateUiDisplayError()
            }
        }
    }

    private fun updateUiDisplayError() {
        binding.patientList.visibility = View.GONE
        binding.tvPatientNoDataMessage.visibility = View.VISIBLE
    }

    private fun updateUiDisplayData() {
        binding.tvPatientNoDataMessage.visibility = View.GONE
        binding.patientList.visibility = View.VISIBLE
    }

    override fun onItemClick(item: Patient) {
        findNavController().navigate(HospitalFragmentDirections.actionNavFragmentToNavPatientDetails(item.id))
    }

    override fun onItemButtonClick(item: Patient) {
        when {
            item.stateName == DataSymbolToNameMap.patientSymbolToNameMap["X"] -> {
                Toast.makeText(context, getString(R.string.patient_state_dead), Toast.LENGTH_SHORT).show()
            }
            viewModel.getDrugs().value.isNullOrEmpty() -> {
                Toast.makeText(context, getString(R.string.error_no_drug_data_message), Toast.LENGTH_SHORT).show()
            }
            else -> {
                DrugSelectionDialogFragment.newInstance(item.id).show(childFragmentManager, DrugSelectionDialogFragment.TAG)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearAll()
    }
}