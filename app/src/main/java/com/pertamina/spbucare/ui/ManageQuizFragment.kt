package com.pertamina.spbucare.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pertamina.spbucare.databinding.FragmentManageQuizBinding
import com.pertamina.spbucare.ui.adapter.QuizAdapter
import com.pertamina.spbucare.ui.adapter.QuizListener
import com.pertamina.spbucare.viewmodel.QuizViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ManageQuizFragment : Fragment() {
    private lateinit var binding: FragmentManageQuizBinding
    private val quizVM: QuizViewModel by viewModel{ parametersOf("")}

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageQuizBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = quizVM
        setupRecyclerView()
        setupListener()
        return binding.root
    }

    private fun setupListener() {
        binding.apply {
            fabQuiz.setOnClickListener {
                val action = ManageQuizFragmentDirections.actionShowCreateQuiz()
                findNavController().navigate(action)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvQuiz.adapter = QuizAdapter(QuizListener { quiz ->
                val action = ManageQuizFragmentDirections.actionShowListQuiz(quiz)
                findNavController().navigate(action)
            })
        }
    }
}