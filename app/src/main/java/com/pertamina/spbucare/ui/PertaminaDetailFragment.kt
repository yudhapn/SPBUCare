package com.pertamina.spbucare.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pertamina.spbucare.databinding.FragmentPertaminaDetailBinding
import com.pertamina.spbucare.model.User


class PertaminaDetailFragment : Fragment() {
    private lateinit var binding: FragmentPertaminaDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPertaminaDetailBinding.inflate(inflater, container, false)
        var user = User()
        arguments?.let { user = PertaminaDetailFragmentArgs.fromBundle(it).userArg }
        binding.userArg = user
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