package com.pertamina.spbucare.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pertamina.spbucare.databinding.FragmentOrderDetailBinding
import com.pertamina.spbucare.model.Order

class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false).apply {
            var order = Order()
            var position = -1
            arguments?.let {
                order = OrderDetailFragmentArgs.fromBundle(it).detailOrderArg
                position = OrderDetailFragmentArgs.fromBundle(it).positionArg
            }
            detailOrder = order
            positionArg = position
        }
        return binding.root
    }

}