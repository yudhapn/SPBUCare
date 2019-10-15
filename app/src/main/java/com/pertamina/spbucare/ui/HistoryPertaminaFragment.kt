package com.pertamina.spbucare.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.FragmentHistoryPertaminaBinding
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.ui.adapter.TopListener
import com.pertamina.spbucare.ui.adapter.TopTenAdapter
import com.pertamina.spbucare.viewmodel.HistoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class HistoryPertaminaFragment : Fragment() {
    private lateinit var binding: FragmentHistoryPertaminaBinding
    private val historyVM: HistoryViewModel by viewModel { parametersOf("Top") }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryPertaminaBinding.inflate(inflater, container, false)
        setupListener()
        setupObserver()
        setupRecyclerView()
        binding.lifecycleOwner = this
        binding.viewModel = historyVM
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvHistory.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            rvHistory.adapter = TopTenAdapter(TopListener { history, position ->
                val action = HistoryPertaminaFragmentDirections.actionShowDetailOrder(history, position)
                findNavController().navigate(action)
            })
        }
    }

    private fun setupObserver() {
        binding.apply {
            historyVM.networkState.observe(viewLifecycleOwner, Observer {
                refreshLayout.isRefreshing = it == NetworkState.RUNNING
                refreshLayout.setOnRefreshListener {
                    historyVM.refreshHistory()
                }
            })
        }
    }

    private fun setupListener() {
        binding.apply {
            val listType = resources.getStringArray(R.array.history_type).toList()
            dropdownSer.setAdapter(configureAdapter(listType))
            cancelMenu.setOnClickListener {
                val action =
                    HistoryPertaminaFragmentDirections.actionShowOrderList(requireContext().getString(R.string.pembatalan))
                findNavController().navigate(action)
            }
            depositMenu.setOnClickListener {
                val action =
                    HistoryPertaminaFragmentDirections.actionShowOrderList(requireContext().getString(R.string.hi))
                findNavController().navigate(action)
            }
            addMenu.setOnClickListener {
                val action =
                    HistoryPertaminaFragmentDirections.actionShowOrderList(requireContext().getString(R.string.tambah))
                findNavController().navigate(action)
            }
            withdrawMenu.setOnClickListener {
                val action =
                    HistoryPertaminaFragmentDirections.actionShowOrderList(requireContext().getString(R.string.tarik_lo))
                findNavController().navigate(action)
            }
            emergencyMenu.setOnClickListener {
                val action =
                    HistoryPertaminaFragmentDirections.actionShowOrderList(requireContext().getString(R.string.darurat))
                findNavController().navigate(action)
            }
            allMenu.setOnClickListener {
                val action =
                    HistoryPertaminaFragmentDirections.actionShowOrderList(requireContext().getString(R.string.semua))
                findNavController().navigate(action)
            }
            dropdownSer.setOnItemClickListener { _, _, position, _ ->
                historyVM.setRegion(listType[position])
            }
        }
    }
    private fun configureAdapter(stringArray: List<String>) =
        ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            stringArray
        )
}
