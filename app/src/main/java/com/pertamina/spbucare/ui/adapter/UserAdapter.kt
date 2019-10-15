package com.pertamina.spbucare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pertamina.spbucare.databinding.ItemUserBinding
import com.pertamina.spbucare.model.User
import com.pertamina.spbucare.ui.adapter.UserAdapter.ViewHolder.Companion.from

class UserAdapter(val clickListener: UserListener) :
        ListAdapter<User, UserAdapter.ViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), clickListener)

    class ViewHolder private constructor(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, clickListener: UserListener) {
            binding.user = user
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) = ViewHolder(
                ItemUserBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User) =
            oldItem.userId == newItem.userId

    override fun areContentsTheSame(oldItem: User, newItem: User) =
            oldItem == newItem
}

class UserListener(val clickListener: (education: User) -> Unit) {
    fun onClick(education: User) = clickListener(education)
}