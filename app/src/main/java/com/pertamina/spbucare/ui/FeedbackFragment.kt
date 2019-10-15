package com.pertamina.spbucare.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pertamina.spbucare.databinding.FragmentFeedbackBinding


class FeedbackFragment : Fragment() {
    private lateinit var binding: FragmentFeedbackBinding
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedbackBinding.inflate(inflater, container, false)
            binding.apply {
        }
        return binding.root
    }
}