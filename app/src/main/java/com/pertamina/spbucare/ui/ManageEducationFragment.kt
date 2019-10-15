package com.pertamina.spbucare.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pertamina.spbucare.databinding.FragmentManageEducationBinding
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.ui.adapter.EducationAdapter
import com.pertamina.spbucare.ui.adapter.EducationListener
import com.pertamina.spbucare.viewmodel.EducationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ManageEducationFragment : Fragment() {
    private lateinit var binding: FragmentManageEducationBinding
    private val eduVM: EducationViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageEducationBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.educationVM = eduVM
        setupRecyclerView()
        setupListener()
        setupObserver()
        return binding.root
    }

    private fun setupObserver() {
        binding.apply {
            eduVM.networkState.observe(viewLifecycleOwner, Observer {
                refreshLayout.isRefreshing = it == NetworkState.RUNNING
                refreshLayout.setOnRefreshListener {
                    eduVM.refreshEducation()
                }
            })
        }
    }

    private fun setupListener() {
        binding.apply {
            btnAddCategory.setOnClickListener {
                val action = ManageEducationFragmentDirections.actionAddEducation()
                findNavController().navigate(action)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvEducation.adapter = EducationAdapter(EducationListener { education ->
                val action = ManageEducationFragmentDirections.actionShowDetailEducation(education)
                findNavController().navigate(action)
            })
        }
    }
}
