package com.pertamina.spbucare.ui


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.iid.FirebaseInstanceId
import com.pertamina.spbucare.databinding.FragmentDashboardSpbuBinding
import com.pertamina.spbucare.ui.adapter.EducationAdapter
import com.pertamina.spbucare.ui.adapter.EducationListener
import com.pertamina.spbucare.ui.adapter.InformationAdapter
import com.pertamina.spbucare.ui.adapter.InformationListener
import com.pertamina.spbucare.viewmodel.EducationViewModel
import com.pertamina.spbucare.viewmodel.InformationViewModel
import com.pertamina.spbucare.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class DashboardSpbuFragment : Fragment() {
    private lateinit var binding: FragmentDashboardSpbuBinding
    private val userVM: UserViewModel by viewModel { parametersOf("") }
    private val eduVM: EducationViewModel by viewModel()
    private val infoVM: InformationViewModel by viewModel()
    private var viewFragment: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardSpbuBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.educationVM = eduVM
        binding.informationVM = infoVM
        setupListener()
        setupRecyclerView()
        if (viewFragment == null) {
            viewFragment = binding.root
        }
        return viewFragment
    }


    private fun setupRecyclerView() {
        binding.apply {
            rvEducation.adapter = EducationAdapter(EducationListener { education ->
                val action = DashboardPertaminaFragmentDirections.actionShowDetailEducation(education)
                findNavController().navigate(action)
            })
            rvInformation.adapter = InformationAdapter(InformationListener { information ->
                val action = DashboardPertaminaFragmentDirections.actionShowDetailInformation(information)
                findNavController().navigate(action)
            })
        }
    }

    private fun setupListener() {
        binding.apply {
            cancelMenu.setOnClickListener {
                val action = DashboardSpbuFragmentDirections.actionShowCancel()
                findNavController().navigate(action)
            }

            depositMenu.setOnClickListener {
                val action = DashboardSpbuFragmentDirections.actionShowDeposit()
                findNavController().navigate(action)
            }

            additionMenu.setOnClickListener {
                val action = DashboardSpbuFragmentDirections.actionShowAddition()
                findNavController().navigate(action)
            }
            quizMenu.setOnClickListener {
                val action = DashboardSpbuFragmentDirections.actionShowQuiz()
                findNavController().navigate(action)
            }
            withdrawMenu.setOnClickListener {
                val action = DashboardSpbuFragmentDirections.actionShowWithdraw()
                findNavController().navigate(action)
            }
            emergencyMenu.setOnClickListener {
                val action = DashboardSpbuFragmentDirections.actionShowEmergency()
                findNavController().navigate(action)
            }
        }
    }
}