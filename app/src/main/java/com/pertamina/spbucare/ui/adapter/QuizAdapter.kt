package com.pertamina.spbucare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pertamina.spbucare.databinding.ItemQuizBinding
import com.pertamina.spbucare.model.Quiz
import com.pertamina.spbucare.ui.adapter.QuizAdapter.ViewHolder.Companion.from


class QuizAdapter(val clickListener: QuizListener) :
    ListAdapter<Quiz, QuizAdapter.ViewHolder>(QuizDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), clickListener)

    class ViewHolder private constructor(val binding: ItemQuizBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quiz: Quiz, clickListener: QuizListener) {
            binding.quiz = quiz
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) = ViewHolder(
                ItemQuizBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}

class QuizDiffCallback : DiffUtil.ItemCallback<Quiz>() {
    override fun areItemsTheSame(oldItem: Quiz, newItem: Quiz) =
        oldItem.quizId == newItem.quizId

    override fun areContentsTheSame(oldItem: Quiz, newItem: Quiz) =
        oldItem == newItem
}

class QuizListener(val clickListener: (quiz: Quiz) -> Unit) {
    fun onClick(quiz: Quiz) = clickListener(quiz)
}