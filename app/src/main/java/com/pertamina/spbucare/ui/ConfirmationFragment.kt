package com.pertamina.spbucare.ui


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.FragmentConfirmationBinding
import com.pertamina.spbucare.model.Order
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.ui.adapter.ConfirmAdapter
import com.pertamina.spbucare.ui.adapter.OrderListener
import com.pertamina.spbucare.ui.adapter.RecyclerItemTouchHelper
import com.pertamina.spbucare.ui.adapter.RecyclerItemTouchHelper.RecyclerItemTouchHelperListener
import com.pertamina.spbucare.viewmodel.OrderViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class ConfirmationFragment : Fragment() {
    private lateinit var adapter: ConfirmAdapter
    private var cookieType: String? = ""
    private lateinit var snackbar: Snackbar
    private lateinit var binding: FragmentConfirmationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfirmationBinding.inflate(inflater, container, false)
        val user = activity?.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        cookieType = user?.getString("type", "missing")
        val orderVM: OrderViewModel by viewModel { parametersOf(cookieType) }
        binding.lifecycleOwner = this
        binding.viewModel = orderVM
        setupRecyclerView(orderVM)
        setupObserver(orderVM)
        return binding.root
    }

    private fun setupObserver(orderVM: OrderViewModel) {
        binding.apply {
            orderVM.networkState.observe(viewLifecycleOwner, Observer {
                refreshLayout.isRefreshing = it == NetworkState.RUNNING
                refreshLayout.setOnRefreshListener {
                    orderVM.refreshOrderConfirm(cookieType ?: "")
                }
            })
            orderVM.isEmptyList.observe(viewLifecycleOwner, Observer { isEmpty ->
                orderVM.networkState.observe(viewLifecycleOwner, Observer { networkState ->
                    refreshLayout.isRefreshing = networkState == NetworkState.RUNNING
                    if (isEmpty) {
                        when (networkState) {
                            NetworkState.SUCCESS -> {
                                ivEmptyState.setImageResource(R.drawable.ic_empty_state)
                                ivEmptyState.visibility = View.VISIBLE
                                tvEmptyState.visibility = View.VISIBLE
                                rvOrder.visibility = View.GONE
                            }
                            NetworkState.FAILED -> {
                                ivEmptyState.visibility = View.GONE
                                tvEmptyState.visibility = View.GONE
                                rvOrder.visibility = View.VISIBLE
                            }
                            else -> {
                                ivEmptyState.visibility = View.GONE
                                tvEmptyState.visibility = View.GONE
                                rvOrder.visibility = View.VISIBLE
                            }
                        }
                    }
                })
            })
        }
    }

    private fun setupRecyclerView(orderVM: OrderViewModel) {
        binding.apply {
            adapter = ConfirmAdapter(OrderListener { history ->
                val action = HistorySpbuFragmentDirections.actionShowDetailOrder(history, -1)
                findNavController().navigate(action)
            })
            rvOrder.setHasFixedSize(true)
            rvOrder.adapter = adapter
            rvOrder.itemAnimator = DefaultItemAnimator()
            val itemTouchHelperCallback =
                RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, object :
                    RecyclerItemTouchHelperListener {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
                        if (viewHolder is ConfirmAdapter.ViewHolder) {
                            val orderItem = orderVM.getItem(position)
                            snackbar = Snackbar.make(
                                binding.refreshLayout,
                                "Permintaan telah disetujui!",
                                Snackbar.LENGTH_LONG
                            )
                            snackbar.setBackgroundTint(Color.parseColor("#85C226"))
                            orderVM.removeItem(position)
                            if (direction == ItemTouchHelper.RIGHT) {
                                snackbar = Snackbar
                                    .make(binding.refreshLayout, "Permintaan telah ditolak!", Snackbar.LENGTH_LONG)
                                snackbar.setBackgroundTint(Color.parseColor("#DA251C"))
                                updateOrder(orderItem, position, "menolak", snackbar, orderVM)
                            } else {
                                updateOrder(orderItem, position, "menerima", snackbar, orderVM)
                            }
                        }
                    }

                    override fun onRefreshDisable(dX: Float, dY: Float) {
                        val enable = dY == 0f && dX == 0f
                        binding.refreshLayout.isEnabled = enable
                    }

                })
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvOrder)
            rvOrder.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun updateOrder(
        orderItem: Order,
        position: Int,
        status: String,
        snackbar: Snackbar,
        orderVM: OrderViewModel
    ) {
        MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setMessage("Apakah anda ingin $status permintaan ini?")
            .setNegativeButton("Tidak") { _, _ ->
                orderVM.restoreItem(orderItem, position)
            }
            .setPositiveButton("Iya") { _, _ ->
                orderVM.updateOrder(orderItem, cookieType, status)
                snackbar.setActionTextColor(Color.YELLOW)
                snackbar.show()
            }
            .show()

    }
}