package com.pertamina.spbucare.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pertamina.spbucare.databinding.FragmentHistorySpbuBinding
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.ui.adapter.HistoryAdapter
import com.pertamina.spbucare.ui.adapter.OrderListener
import com.pertamina.spbucare.viewmodel.HistoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import androidx.recyclerview.widget.DividerItemDecoration




class HistorySpbuFragment : Fragment() {
    private val historyVM: HistoryViewModel by viewModel{ parametersOf("")}
    private lateinit var binding: FragmentHistorySpbuBinding
    private var viewFragment: View? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistorySpbuBinding.inflate(inflater, container, false).apply {
            val user = activity?.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
            val cookieType = user?.getString("type", "missing")
            lifecycleOwner = this@HistorySpbuFragment
            viewModel = historyVM
            rvHistory.setHasFixedSize(true)
            rvHistory.adapter = HistoryAdapter(OrderListener { history ->
                val action = HistorySpbuFragmentDirections.actionShowDetailOrder(history, -1)
                findNavController().navigate(action)
            })
            historyVM.networkState.observe(viewLifecycleOwner, Observer {
                refreshLayout.isRefreshing = it == NetworkState.RUNNING
                refreshLayout.setOnRefreshListener {
                    historyVM.refreshHistory(cookieType ?: "")
                }
            })
            rvHistory.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        if(viewFragment == null) {
            viewFragment = binding.root
        }
        return viewFragment
    }
}