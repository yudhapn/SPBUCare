package com.pertamina.spbucare.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pertamina.spbucare.databinding.FragmentSpbuDetailBinding
import com.pertamina.spbucare.model.User
import com.pertamina.spbucare.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SpbuDetailFragment : Fragment() {
    private lateinit var binding: FragmentSpbuDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpbuDetailBinding.inflate(inflater, container, false)
        var user = User()
        arguments?.let { user = SpbuDetailFragmentArgs.fromBundle(it).userArg }
        binding.userArg = user
        val userVM: UserViewModel by viewModel { parametersOf(user.salesRetailId) }
        binding.lifecycleOwner = this@SpbuDetailFragment
        binding.vm = userVM
        setupListener(user)

        return binding.root
    }

    private fun setupListener(user: User) {
        binding.btnSetAccount.setOnClickListener {
            val action = SpbuDetailFragmentDirections.actionEditAccount(user)
            findNavController().navigate(action)
        }
        binding.btnSetProfile.setOnClickListener {
            val action = SpbuDetailFragmentDirections.actionEditProfile(user)
            findNavController().navigate(action)
        }
    }
}