package com.mprusina.evooq.ui.drugdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mprusina.evooq.R
import com.mprusina.evooq.data.entities.Drug
import com.mprusina.evooq.databinding.FragmentDialogDrugSelectionBinding
import com.mprusina.evooq.ui.SimulatorViewModel
import com.mprusina.evooq.ui.drugdialog.utils.DialogDrugListAdapter
import com.mprusina.evooq.utils.build

class DrugSelectionDialogFragment: DialogFragment(), DialogDrugListAdapter.OnMyClickListener {

    companion object {
        val TAG: String = DrugSelectionDialogFragment::class.java.simpleName
        private const val KEY_PATIENT_ID = "key_patient_id"

        fun newInstance(id: Int): DrugSelectionDialogFragment = DrugSelectionDialogFragment().build {
            putInt(KEY_PATIENT_ID, id)
        }
    }

    private lateinit var binding: FragmentDialogDrugSelectionBinding
    private lateinit var drugAdapter: DialogDrugListAdapter
    private val viewModel: SimulatorViewModel by activityViewModels()

    private val argPatientId: Int by lazy { arguments?.getInt(KEY_PATIENT_ID) ?: -1}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDialogDrugSelectionBinding.inflate(inflater, container,false)
        drugAdapter = DialogDrugListAdapter(this)

        with(binding.dialogDrugList) {
            layoutManager = LinearLayoutManager(context)
            adapter = drugAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (argPatientId == -1) {
            Toast.makeText(requireContext(), getString(R.string.error_selecting_user_text), Toast.LENGTH_SHORT).show()
            this.dismiss()
        }
        val patient = viewModel.getPatientWithId(argPatientId).value!!

        viewModel.checkIfPreviouslySelectedDrugsExistForPatientWithId(patient.id)

        viewModel.getDrugs().observe(viewLifecycleOwner) {
                drugAdapter.submitList(it)
            }

        binding.btnDialogConfirm.setOnClickListener {
            viewModel.createPatientDrugsPair(patient)
            this.dismiss()
        }
    }

    override fun onItemClick(drug: Drug, position: Int) {
        viewModel.updateSelectedDrugs(drug)
        drugAdapter.notifyItemChanged(position)
    }
}