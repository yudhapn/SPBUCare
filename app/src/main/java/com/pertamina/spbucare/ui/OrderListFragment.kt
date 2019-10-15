package com.pertamina.spbucare.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.FragmentOrderListBinding
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.ui.adapter.HistoryAdapter
import com.pertamina.spbucare.ui.adapter.OrderListener
import com.pertamina.spbucare.util.*
import com.pertamina.spbucare.viewmodel.HistoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*

class OrderListFragment : Fragment(), FilterFragment.IFilterListener {
    private lateinit var binding: FragmentOrderListBinding
    private var cal = Calendar.getInstance()
    private var endDate = cal.time
    private var beginDate = cal.time
    private var category: String? = ""
    private lateinit var historyVM: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderListBinding.inflate(inflater, container, false)
        category = arguments?.let { OrderListFragmentArgs.fromBundle(it).categoryArg }
        (activity as AppCompatActivity).supportActionBar?.title = category
        val historyVM: HistoryViewModel by viewModel { parametersOf(category) }
        this.historyVM = historyVM
        binding.lifecycleOwner = this
        binding.viewModel = historyVM
        cal.add(Calendar.DATE, -7)
        beginDate = cal.time
        requestPermissionsIfNecessary()
        setupObserver()
        setupListener()
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvHistory.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            rvHistory.adapter = HistoryAdapter(OrderListener { history ->
                val action = OrderListFragmentDirections.actionShowDetailOrder(history, -1)
                findNavController().navigate(action)
            })
        }
    }

    private fun setupObserver() {
        binding.apply {
            historyVM.isEmptyList.observe(viewLifecycleOwner, Observer { isEmpty ->
                historyVM.networkState.observe(viewLifecycleOwner, Observer { networkState ->
                    refreshLayout.isRefreshing = networkState == NetworkState.RUNNING
                    refreshLayout.setOnRefreshListener {
                        historyVM.refreshHistory()
                    }
                    if (isEmpty) {
                        when (networkState) {
                            NetworkState.SUCCESS -> {
                                ivEmptyState.setImageResource(R.drawable.ic_empty_state)
                                ivEmptyState.visibility = View.VISIBLE
                                tvEmptyState.visibility = View.VISIBLE
                                rvHistory.visibility = View.GONE
                            }
                            NetworkState.FAILED -> {
                                ivEmptyState.visibility = View.GONE
                                tvEmptyState.visibility = View.GONE
                                rvHistory.visibility = View.VISIBLE
                            }
                            else -> {
                                ivEmptyState.visibility = View.GONE
                                tvEmptyState.visibility = View.GONE
                                rvHistory.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        ivEmptyState.visibility = View.GONE
                        tvEmptyState.visibility = View.GONE
                        rvHistory.visibility = View.VISIBLE
                    }
                })
            })
        }
    }

    private fun setupListener() {
        binding.apply {
            ivRange.setOnClickListener {
                val filterDialog = FilterFragment(this@OrderListFragment, beginDate, endDate)
                filterDialog.show(requireActivity().supportFragmentManager, "Filter")
            }

            ivGenerate.setOnClickListener {
                val myFormat = "dd-MMM-yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                val beginDate = sdf.format(this@OrderListFragment.beginDate.time)
                val endDate = sdf.format(this@OrderListFragment.endDate.time)
                MaterialAlertDialogBuilder(context)
                    .setMessage("Apakah anda ingin membuat laporan pada periode $beginDate s/d $endDate?")
                    .setNegativeButton("Tidak", null)
                    .setPositiveButton("Iya") { _, _ ->
                        generateFile(
                            beginDate,
                            endDate,
                            historyVM.accumulateHistory(),
                            category.toString(),
                            requireContext()
                        )
                    }
                    .show()
            }
        }
    }

    override fun onFilterOrder(beginDate: Date, endDate: Date) {
        this.beginDate = beginDate
        this.endDate = endDate

        historyVM.getHistory(USER_TYPE, category.toString(), beginDate, endDate)
    }

    private fun requestPermissionsIfNecessary() {
        if (!checkAllPermissions(requireContext())) {
            if (permissionRequestCount < maxNumberRequestPermissions) {
                permissionRequestCount += 1
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissions.toTypedArray(),
                    requestCodePermission
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.set_permissions_in_settings,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}