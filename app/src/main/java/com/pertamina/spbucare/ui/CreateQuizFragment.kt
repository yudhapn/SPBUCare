package com.pertamina.spbucare.ui


import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pertamina.spbucare.util.InputFilterMinMax
import com.pertamina.spbucare.databinding.FragmentCreateQuizBinding
import com.pertamina.spbucare.model.Question
import com.pertamina.spbucare.model.Quiz
import com.pertamina.spbucare.viewmodel.QuizViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*


class CreateQuizFragment : Fragment(), TextWatcher {
    private lateinit var binding: FragmentCreateQuizBinding
    private val quizVM: QuizViewModel by viewModel{parametersOf("")}
    private var cal = Calendar.getInstance()
    private var beheldDate = cal.time

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateQuizBinding.inflate(inflater, container, false)
        setMaxDuration()
        setupListener()
        setObserver()
        return binding.root
    }

    private fun setObserver() {
        binding.apply {

            quizVM.review.observe(viewLifecycleOwner, Observer {
                tvReview.text = it
            })
            quizVM.currentQuestions.observe(viewLifecycleOwner, Observer {
                etQ1.setText(it.question)
                etQ1A1.setText(it.option1)
                etQ1A2.setText(it.option2)
                etQ1A3.setText(it.option3)
                etQ1A4.setText(it.option4)
                etQ1A5.setText(it.option5)
                etQ1A6.setText(it.option6)
                etQ1A7.setText(it.option7)
                when (it.answer) {
                    it.option1 -> rgQ1.check(rbQ1A1.id)
                    it.option2 -> rgQ1.check(rbQ1A2.id)
                    it.option3 -> rgQ1.check(rbQ1A3.id)
                    it.option4 -> rgQ1.check(rbQ1A4.id)
                    it.option5 -> rgQ1.check(rbQ1A5.id)
                    it.option6 -> rgQ1.check(rbQ1A6.id)
                    it.option7 -> rgQ1.check(rbQ1A7.id)
                }
            })
            quizVM.position.observe(viewLifecycleOwner, Observer {
                progressBar.progress = it
                etQ1.hint = "Soal Nomor ${it.plus(1)}"
                when {
                    it == 0 -> btnBack.visibility = View.GONE
                    it in 1..9 -> {
                        btnBack.visibility = View.VISIBLE
                        btnNext.visibility = View.VISIBLE
                        btnDone.visibility = View.GONE
                        svReview.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                        rgQ1.visibility = View.VISIBLE
                        etQ1.visibility = View.VISIBLE
                        etQ1A1.visibility = View.VISIBLE
                        etQ1A2.visibility = View.VISIBLE
                        etQ1A3.visibility = View.VISIBLE
                        etQ1A4.visibility = View.VISIBLE
                        etQ1A5.visibility = View.VISIBLE
                        etQ1A6.visibility = View.VISIBLE
                        etQ1A7.visibility = View.VISIBLE
                    }
                    it >= 10 -> {
                        btnNext.visibility = View.GONE
                        btnDone.visibility = View.VISIBLE
                        svReview.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        rgQ1.visibility = View.GONE
                        etQ1.visibility = View.GONE
                        etQ1A1.visibility = View.GONE
                        etQ1A2.visibility = View.GONE
                        etQ1A3.visibility = View.GONE
                        etQ1A4.visibility = View.GONE
                        etQ1A5.visibility = View.GONE
                        etQ1A6.visibility = View.GONE
                        etQ1A7.visibility = View.GONE
                    }
                }
            })
        }
    }

    private fun setMaxDuration() {
        val range = arrayOf<InputFilter>(InputFilterMinMax("1", "30"))
        binding.apply {
            etQuizDuration.filters = range
        }
    }

    private fun setupListener() {
        binding.apply {
            etQuizName.addTextChangedListener(this@CreateQuizFragment)
            etQuizDuration.addTextChangedListener(this@CreateQuizFragment)
            etQuizBeheld.addTextChangedListener(this@CreateQuizFragment)
            etQ1.addTextChangedListener(this@CreateQuizFragment)
            etQ1A1.addTextChangedListener(this@CreateQuizFragment)
            etQ1A2.addTextChangedListener(this@CreateQuizFragment)
            etQ1A3.addTextChangedListener(this@CreateQuizFragment)
            etQ1A4.addTextChangedListener(this@CreateQuizFragment)
            etQ1A5.addTextChangedListener(this@CreateQuizFragment)
            etQ1A6.addTextChangedListener(this@CreateQuizFragment)
            etQ1A7.addTextChangedListener(this@CreateQuizFragment)

            val dateBeheldListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                updateDateInView(year, monthOfYear, dayOfMonth)
            }
            etQuizBeheld.setOnClickListener {
                showCalendar(dateBeheldListener)
            }

            quizVM.quiz.observe(viewLifecycleOwner, Observer {
                if (it.name.isNotEmpty()) {
                    showInputView()
                }
            })

            btnDone.setOnClickListener { view ->
                MaterialAlertDialogBuilder(context)
                        .setMessage("Apakah anda yakin ingin MEMBUAT kuis ini?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Iya") { _, _ ->
                            quizVM.createQuiz()
                            val action = CreateQuizFragmentDirections.actionShowManageQuiz()
                            findNavController().navigate(action)
                            Snackbar.make(view, "Kuis telah berhasil dibuat", Snackbar.LENGTH_SHORT).show()
                        }
                        .show()
            }

            btnBack.setOnClickListener {
                rbQ1A1.text = etQ1A1.text.toString()
                rbQ1A2.text = etQ1A2.text.toString()
                rbQ1A3.text = etQ1A3.text.toString()
                rbQ1A4.text = etQ1A4.text.toString()
                rbQ1A5.text = etQ1A5.text.toString()
                rbQ1A6.text = etQ1A6.text.toString()
                rbQ1A7.text = etQ1A7.text.toString()
                val idx = rgQ1.indexOfChild(rgQ1.findViewById(rgQ1.checkedRadioButtonId))
                val rb = rgQ1.getChildAt(idx) as RadioButton
                val answer = rb.text.toString()
                quizVM.previousQuestion(
                        Question(
                                etQ1.text.toString(),
                                etQ1A1.text.toString(),
                                etQ1A2.text.toString(),
                                etQ1A3.text.toString(),
                                etQ1A4.text.toString(),
                                etQ1A5.text.toString(),
                                etQ1A6.text.toString(),
                                etQ1A7.text.toString(),
                                answer
                        )
                )
                rbQ1A1.text = ""
                rbQ1A2.text = ""
                rbQ1A3.text = ""
                rbQ1A4.text = ""
                rbQ1A5.text = ""
                rbQ1A6.text = ""
                rbQ1A7.text = ""
            }

            btnNext.setOnClickListener {
                if (etQuizName.isVisible) {
                    if (etQuizName.text.isNotEmpty() && etQuizDuration.text.isNotEmpty()) {
                        val name = etQuizName.text.toString()
                        val duration = etQuizDuration.text.toString().toInt() * 60

                        quizVM.setQuiz(
                                Quiz(
                                        name = name,
                                        duration = duration,
                                        beHeldOn = beheldDate
                                )
                        )
                    } else {

                    }
                } else {
                    rbQ1A1.text = etQ1A1.text.toString()
                    rbQ1A2.text = etQ1A2.text.toString()
                    rbQ1A3.text = etQ1A3.text.toString()
                    rbQ1A4.text = etQ1A4.text.toString()
                    rbQ1A5.text = etQ1A5.text.toString()
                    rbQ1A6.text = etQ1A6.text.toString()
                    rbQ1A7.text = etQ1A7.text.toString()
                    val idx = rgQ1.indexOfChild(rgQ1.findViewById(rgQ1.checkedRadioButtonId))
                    val rb = rgQ1.getChildAt(idx) as RadioButton
                    val answer = rb.text.toString()
                    quizVM.nextQuestion(
                            Question(
                                    etQ1.text.toString(),
                                    etQ1A1.text.toString(),
                                    etQ1A2.text.toString(),
                                    etQ1A3.text.toString(),
                                    etQ1A4.text.toString(),
                                    etQ1A5.text.toString(),
                                    etQ1A6.text.toString(),
                                    etQ1A7.text.toString(),
                                    answer
                            )
                    )
                    rbQ1A1.text = ""
                    rbQ1A2.text = ""
                    rbQ1A3.text = ""
                    rbQ1A4.text = ""
                    rbQ1A5.text = ""
                    rbQ1A6.text = ""
                    rbQ1A7.text = ""
                }
            }
        }
    }

    private fun showCalendar(dateListener: DatePickerDialog.OnDateSetListener) {
        DatePickerDialog(
                requireContext(),
                dateListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show()

    }

    private fun updateDateInView(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        val currentDate = Calendar.getInstance()
        currentDate.add(Calendar.DATE, -1)
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        beheldDate = cal.time
        binding.etQuizBeheld.setText(sdf.format(cal.time))
    }

    var position = 0

    private fun showInputView() {
        binding.apply {
            progressBar.progress = position++
            etQuizName.visibility = View.GONE
            etQuizDuration.visibility = View.GONE
            etQuizBeheld.visibility = View.GONE
            etQ1.visibility = View.VISIBLE
            etQ1A1.visibility = View.VISIBLE
            rgQ1.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            btnNext.isEnabled = false
        }
    }

    override fun afterTextChanged(s: Editable?) {
        binding.apply {
            if (etQuizName.isVisible) {
                btnNext.isEnabled = etQuizName.text.isNotEmpty() && etQuizDuration.text.isNotEmpty() && etQuizBeheld.text.isNotEmpty()
            } else if (progressBar.isVisible) {
                btnNext.isEnabled = (etQ1.text.isNotEmpty() && etQ1A1.text.isNotEmpty() && etQ1A2.text.isNotEmpty()
                        && etQ1A3.text.isNotEmpty() && etQ1A4.text.isNotEmpty() && etQ1A5.text.isNotEmpty()
                        && etQ1A6.text.isNotEmpty() && etQ1A7.text.isNotEmpty())
            }
        }

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}