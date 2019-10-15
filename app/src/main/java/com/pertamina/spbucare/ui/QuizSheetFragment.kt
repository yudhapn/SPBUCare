package com.pertamina.spbucare.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pertamina.spbucare.databinding.FragmentQuizSheetBinding
import com.pertamina.spbucare.model.Quiz
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.viewmodel.QuizViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class QuizSheetFragment : Fragment() {
    private lateinit var binding: FragmentQuizSheetBinding
    private lateinit var viewModel: QuizViewModel
    private var cookieName: String? = ""
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuizSheetBinding.inflate(inflater, container, false)
        val user = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        cookieName = user.getString("userName", "missing")
        var quiz = Quiz()
        arguments?.let { quiz = QuizSheetFragmentArgs.fromBundle(it).quizArg }
        val vm: QuizViewModel by viewModel { parametersOf(quiz.quizId) }
        viewModel = vm
        binding.lifecycleOwner = this
        binding.quizVM = vm
        setupListener(quiz)
        setObserver(quiz)
        return binding.root
    }

    private fun setObserver(quiz: Quiz) {
        binding.apply {
            viewModel.networkState.observe(viewLifecycleOwner, Observer {
                refreshLayout.isRefreshing = it == NetworkState.RUNNING
                refreshLayout.setOnRefreshListener {
                    viewModel.refreshQuestion(quiz.quizId)
                }
            })
            viewModel.isQuizDone.observe(viewLifecycleOwner, Observer {
                if (it) {
                    val idx = rgQ1.indexOfChild(rgQ1.findViewById(rgQ1.checkedRadioButtonId))
                    val rb = rgQ1.getChildAt(idx) as RadioButton
                    val answer = rb.text.toString()
                    viewModel.submitQuiz(answer, cookieName, quiz.quizId)
                    val action = QuizSheetFragmentDirections.actionShowListQuiz()
                    findNavController().navigate(action)
                    Toast.makeText(requireContext(), "Terima kasih telah mengikuti kuis pada hari ini", Toast.LENGTH_LONG).show()
                }
            })
            viewModel.position.observe(viewLifecycleOwner, Observer {
                pbQuiz.progress = it + 1
//                etQ1.hint = "Soal Nomor ${it.plus(1)}"
                when {
                    it + 1 == 1 -> btnBack.visibility = View.GONE
                    it  + 1 in 2..9 -> {
                        btnBack.visibility = View.VISIBLE
                        btnNext.visibility = View.VISIBLE
                        btnDone.visibility = View.GONE
                    }
                    it + 1 >= 10 -> {
                        btnNext.visibility = View.GONE
                        btnDone.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

    private fun setupListener(quiz: Quiz) {
        binding.apply {
            btnDone.setOnClickListener { view ->
                val idx = rgQ1.indexOfChild(rgQ1.findViewById(rgQ1.checkedRadioButtonId))
                val rb = rgQ1.getChildAt(idx) as RadioButton
                val answer = rb.text.toString()
                MaterialAlertDialogBuilder(context)
                        .setMessage("Apakah anda yakin ingin MENGIRIM jawaban kuis ini?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Iya") { _, _ ->
                            viewModel.submitQuiz(answer, cookieName, quiz.quizId)
                            val action = QuizSheetFragmentDirections.actionShowListQuiz()
                            findNavController().navigate(action)
                            Toast.makeText(requireContext(), "Terima kasih telah mengikuti kuis pada hari ini", Toast.LENGTH_LONG).show()
                        }
                        .show()
            }

            val answers = mutableListOf<String>()
            btnBack.setOnClickListener {
                val idx = rgQ1.indexOfChild(rgQ1.findViewById(rgQ1.checkedRadioButtonId))
                val rb = rgQ1.getChildAt(idx) as RadioButton
                val answer = rb.text.toString()
                viewModel.doPreviousQuestion(answer)
            }

            btnNext.setOnClickListener {
                val idx = rgQ1.indexOfChild(rgQ1.findViewById(rgQ1.checkedRadioButtonId))
                val rb = rgQ1.getChildAt(idx) as RadioButton
                val answer = rb.text.toString()
                viewModel.doNextQuestion(answer)
            }
        }
    }

    var position = 0
}