package com.pertamina.spbucare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pertamina.spbucare.databinding.ItemEducationBinding
import com.pertamina.spbucare.model.Education
import com.pertamina.spbucare.ui.adapter.EducationAdapter.ViewHolder.Companion.from

class EducationAdapter(val clickListener: EducationListener) :
        ListAdapter<Education, EducationAdapter.ViewHolder>(EducationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), clickListener)

    class ViewHolder private constructor(val binding: ItemEducationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(education: Education, clickListener: EducationListener) {
            binding.education = education
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) = ViewHolder(
                    ItemEducationBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )
        }
    }
}

class EducationDiffCallback : DiffUtil.ItemCallback<Education>() {
    override fun areItemsTheSame(oldItem: Education, newItem: Education) =
            oldItem.educationId == newItem.educationId

    override fun areContentsTheSame(oldItem: Education, newItem: Education) =
            oldItem == newItem
}

class EducationListener(val clickListener: (education: Education) -> Unit) {
    fun onClick(education: Education) = clickListener(education)
}