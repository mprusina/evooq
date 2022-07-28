package com.mprusina.evooq.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.mprusina.evooq.R
import com.mprusina.evooq.databinding.FragmentHospitalBinding
import com.mprusina.evooq.ui.druglist.DrugListFragment
import com.mprusina.evooq.ui.patientlist.PatientListFragment
import com.mprusina.evooq.utils.isConnectedToNetwork

class HospitalFragment : Fragment() {

    private lateinit var binding: FragmentHospitalBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private val viewModel: SimulatorViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHospitalBinding.inflate(inflater, container, false)

        tabLayout = binding.tabLayout
        viewPager = binding.viewPager

        val tabs = ArrayDeque<String>()
        tabs.add(getString(R.string.tab_patients))
        tabs.add(getString(R.string.tab_drugs))

        val marketplacePagerAdapter = MarketplacePagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = marketplacePagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int -> tab.text = tabs[position] }.attach()
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) { viewPager.currentItem = tab.position }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainButtonGetData.setOnClickListener {
            if (isConnectedToNetwork(context)) {
                binding.loadingIndicator.visibility = View.VISIBLE
                viewModel.fetchPatientAndDrugData()
            } else {
                Toast.makeText(context, getString(R.string.error_no_network_message), Toast.LENGTH_SHORT).show()
            }
        }

        binding.mainButtonAdministerDrugs.setOnClickListener {
            viewModel.administerDrugsToAllPatients()
        }

        viewModel.isFetchPatientDrugDataResponseResultError().observe(viewLifecycleOwner) { responseError ->
            binding.loadingIndicator.visibility = View.GONE
            if (responseError) {
                // If data already exists, don't hide it, instead display error in Toast message
                if (viewModel.getPatients().value != null || viewModel.getDrugs().value != null) {
                    Toast.makeText(context,getString(R.string.error_server_message), Toast.LENGTH_SHORT).show()
                } else {
                    updateUiDisplayError()
                }
            } else {
                updateUiDisplayData()
            }
        }
    }

    private fun updateUiDisplayError() {
        binding.viewPager.visibility = View.GONE
        binding.tvServerErrorMessage.visibility = View.VISIBLE
    }

    private fun updateUiDisplayData() {
        binding.tvServerErrorMessage.visibility = View.GONE
        binding.viewPager.visibility = View.VISIBLE
    }

    inner class MarketplacePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> PatientListFragment()
                1 -> DrugListFragment()
                else -> PatientListFragment()
            }
        }

        override fun getItemCount(): Int {
            return 2
        }
    }
}