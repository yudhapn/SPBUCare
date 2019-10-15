package com.pertamina.spbucare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pertamina.spbucare.databinding.ItemHistoryBinding
import com.pertamina.spbucare.model.Order
import com.pertamina.spbucare.ui.adapter.HistoryAdapter.ViewHolder.Companion.from


class HistoryAdapter(val clickListener: OrderListener) :
    ListAdapter<Order, HistoryAdapter.ViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), clickListener)

    class ViewHolder private constructor(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order,clickListener: OrderListener) {
            binding.order = order
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) = ViewHolder(
                ItemHistoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}

class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order) =
        oldItem.orderId == newItem.orderId

    override fun areContentsTheSame(oldItem: Order, newItem: Order) =
        oldItem == newItem
}

class OrderListener(val clickListener: (history: Order) -> Unit) {
    fun onClick(history: Order) = clickListener(history)
}