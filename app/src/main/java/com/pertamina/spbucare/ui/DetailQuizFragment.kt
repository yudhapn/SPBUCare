package com.pertamina.spbucare.ui


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.opencsv.CSVWriter
import com.pertamina.spbucare.databinding.FragmentDetailQuizBinding
import com.pertamina.spbucare.model.Quiz
import com.pertamina.spbucare.model.QuizResult
import com.pertamina.spbucare.viewmodel.QuizViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DetailQuizFragment : Fragment() {
    private lateinit var binding: FragmentDetailQuizBinding
    private var cookieType: String? = ""
    private var cookieName: String? = ""
    private lateinit var viewModel: QuizViewModel
    private var hasJoined = listOf<QuizResult>()
    private var resultQuiz = listOf<QuizResult>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailQuizBinding.inflate(inflater, container, false)
        val user = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        cookieType = user.getString("type", "missing")
        cookieName = user.getString("userName", "missing")
        var quiz = Quiz()
        arguments?.let { quiz = DetailQuizFragmentArgs.fromBundle(it).quizArg }
        val vm: QuizViewModel by viewModel { parametersOf(quiz.quizId) }
        viewModel = vm
        binding.detailQuiz = quiz
        setupUI(quiz)
        setupListener(quiz)
        return binding.root
    }

    private fun setupListener(quiz: Quiz) {
        binding.apply {
            btnWork.setOnClickListener {
                val action = DetailQuizFragmentDirections.actionDoQuiz(quiz)
                findNavController().navigate(action)
            }

            btnClose.setOnClickListener {
                quiz.complete = true
                quiz.open = false
                viewModel.updateQuiz(quiz)
            }

            btnOpen.setOnClickListener {
                quiz.complete = false
                quiz.open = true
                viewModel.updateQuiz(quiz)
            }
            btnGenerate.setOnClickListener {
                generateFile(quiz, resultQuiz, requireContext())
            }
        }
    }

    private fun setupUI(quiz: Quiz) {
        binding.apply {
            viewModel.quizResult.observe(this@DetailQuizFragment, androidx.lifecycle.Observer {
                resultQuiz = it
            })
            viewModel.listHasJoined.observe(this@DetailQuizFragment, androidx.lifecycle.Observer {
                it.forEach {
                    if (it.name == cookieName) {
                        binding.btnWork.isEnabled = false
                    }
                }

                if (cookieType != null && !cookieType.equals("spbu")) {
                    if (quiz.complete) {
                        btnGenerate.visibility = View.VISIBLE
                    } else {
                        if (quiz.open) {
                            btnClose.visibility = View.VISIBLE
                            btnOpen.visibility = View.GONE
                        } else {
                            btnClose.visibility = View.GONE
                            btnOpen.visibility = View.VISIBLE
                        }
                        btnGenerate.visibility = View.GONE
                    }
                } else {
                    if (quiz.open) {
                        btnWork.visibility = View.VISIBLE
                    } else {
                        btnWork.visibility = View.GONE
                    }
                }

            })
        }
    }

    private fun generateFile(quiz: Quiz, quizResult: List<QuizResult>, context: Context) {
        val writer: CSVWriter?
        try {
            val myFormat = "dd-MMM-yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            val beHeldOn = sdf.format(quiz.beHeldOn)
            val name = String.format("Hasil-Kuis-${quiz.name}-%s.csv", beHeldOn)
            var outputDir = context.filesDir
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                outputDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            }
            if (!outputDir.exists()) {
                outputDir.mkdirs() // should succeed
            }
            val outputFile = File(outputDir, name)
            writer = CSVWriter(FileWriter(outputFile))

            val data = ArrayList<Array<String>>()
            data.add(arrayOf(name))
            data.add(arrayOf("SPBU", "Skor"))
            quizResult.forEach { result ->
                data.add(arrayOf(result.name, result.score.toString()))
            }

            writer.writeAll(data) // data is adding to csv

            writer.close()

            val path = "${outputDir.path}/$name"
            Toast.makeText(context, path, Toast.LENGTH_LONG).show()
            val file = File(path)
            val target = Intent(Intent.ACTION_VIEW)

            val apkURI = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )
            target.setDataAndType(apkURI, "text/csv")
            target.flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            val intent = Intent.createChooser(target, "Open File")
            context.startActivity(intent)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun createInformation() {
//        val imageName = System.currentTimeMillis().toString() + "." + imageUri?.let { getFileExtension(it) }
//        var document = Document()
//        fileUri?.let {
//            val caption = binding.etCaption.text.toString()
//            val fileExtension = getFileExtension(it)
//            val fileName = System.currentTimeMillis().toString() + "." + fileExtension
//            document = Document(
//                documentName = fileName,
//                caption = caption
//            )
//
//        }
//        val information = News(
//            imageName = imageName,
//            author = authorName.toString(),
//            title = binding.etTitle.text.toString(),
//            description = binding.etDescription.text.toString()
//        )
//        informationVM.createInformation(information, imageUri, document, fileUri)
    }

}