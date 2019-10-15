package com.pertamina.spbucare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pertamina.spbucare.databinding.ItemDocumentBinding
import com.pertamina.spbucare.model.Document
import com.pertamina.spbucare.ui.adapter.DocumentAdapter.ViewHolder.Companion.from

class DocumentAdapter(val clickListener: DocumentListener) :
        ListAdapter<Document, DocumentAdapter.ViewHolder>(DocumentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), clickListener)

    class ViewHolder private constructor(val binding: ItemDocumentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(document: Document, clickListener: DocumentListener) {
            binding.document = document
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) = ViewHolder(
                ItemDocumentBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )
        }
    }
}

class DocumentDiffCallback : DiffUtil.ItemCallback<Document>() {
    override fun areItemsTheSame(oldItem: Document, newItem: Document) =
            oldItem.documentId == newItem.documentId

    override fun areContentsTheSame(oldItem: Document, newItem: Document) =
            oldItem == newItem
}

class DocumentListener(val clickListener: (document: Document) -> Unit) {
    fun onClick(document: Document) = clickListener(document)
}