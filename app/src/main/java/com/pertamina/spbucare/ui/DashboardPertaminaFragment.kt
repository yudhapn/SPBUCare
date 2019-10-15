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
import com.pertamina.spbucare.databinding.FragmentDashboardPertaminaBinding
import com.pertamina.spbucare.ui.adapter.EducationAdapter
import com.pertamina.spbucare.ui.adapter.EducationListener
import com.pertamina.spbucare.ui.adapter.InformationAdapter
import com.pertamina.spbucare.ui.adapter.InformationListener
import com.pertamina.spbucare.viewmodel.EducationViewModel
import com.pertamina.spbucare.viewmodel.InformationViewModel
import com.pertamina.spbucare.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DashboardPertaminaFragment : Fragment() {
    private lateinit var binding: FragmentDashboardPertaminaBinding
    private val userVM: UserViewModel by viewModel { parametersOf("") }
    private val eduVM: EducationViewModel by viewModel()
    private val infoVM: InformationViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardPertaminaBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.educationVM = eduVM
        binding.informationVM = infoVM
        setupListener()
        return binding.root
    }

    private fun setupListener() {
        binding.apply {
            informationMenu.setOnClickListener {
                val action = DashboardPertaminaFragmentDirections.actionShowManageInformation()
                findNavController().navigate(action)
            }

            quizMenu.setOnClickListener {
                val action = DashboardPertaminaFragmentDirections.actionShowManageQuiz()
                findNavController().navigate(action)
            }

            educationMenu.setOnClickListener {
                val action = DashboardPertaminaFragmentDirections.actionShowManageEducation()
                findNavController().navigate(action)
            }
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
}