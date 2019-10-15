package com.pertamina.spbucare.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.pertamina.spbucare.databinding.FragmentManageQuizBinding
import com.pertamina.spbucare.databinding.FragmentQuizBinding
import com.pertamina.spbucare.model.Quiz
import com.pertamina.spbucare.ui.adapter.QuizAdapter
import com.pertamina.spbucare.ui.adapter.QuizListener
import com.pertamina.spbucare.viewmodel.QuizViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class QuizFragment : Fragment() {
    private lateinit var binding: FragmentQuizBinding
    private val quizVM: QuizViewModel by viewModel{ parametersOf("") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = quizVM
        setupRecyclerView()
        return binding.root
    }
    private fun setupRecyclerView() {
        binding.apply {
            rvQuiz.adapter = QuizAdapter(QuizListener { quiz ->
                val action = QuizFragmentDirections.actionShowListQuiz(quiz)
                findNavController().navigate(action)
            })
        }
    }}
