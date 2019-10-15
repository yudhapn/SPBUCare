package com.pertamina.spbucare.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.FragmentNotificationBinding
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.ui.adapter.NotificationAdapter
import com.pertamina.spbucare.viewmodel.NotificationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private val notificationVM: NotificationViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = notificationVM
        binding.rvNotification.adapter = NotificationAdapter()
        setupObserver()
        return binding.root
    }

    private fun setupObserver() {
        binding.apply {
            notificationVM.isEmptyList.observe(viewLifecycleOwner, Observer { isEmpty ->
                notificationVM.networkState.observe(viewLifecycleOwner, Observer { networkState ->
                    refreshLayout.isRefreshing = networkState == NetworkState.RUNNING
                    if (isEmpty) {
                        when (networkState) {
                            NetworkState.SUCCESS -> {
                                ivEmptyState.setImageResource(R.drawable.ic_empty_state)
                                ivEmptyState.visibility = View.VISIBLE
                                tvEmptyState.visibility = View.VISIBLE
                                rvNotification.visibility = View.GONE
                            }
                            NetworkState.FAILED -> {
                                ivEmptyState.visibility = View.GONE
                                tvEmptyState.visibility = View.GONE
                                rvNotification.visibility = View.VISIBLE
                            }
                            else -> {
                                ivEmptyState.visibility = View.GONE
                                tvEmptyState.visibility = View.GONE
                                rvNotification.visibility = View.VISIBLE
                            }
                        }
                    }
                })
            })
        }
    }
}