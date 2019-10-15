package com.pertamina.spbucare.viewmodel

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.FirebaseAuth
import com.pertamina.spbucare.model.Question
import com.pertamina.spbucare.model.Quiz
import com.pertamina.spbucare.model.QuizResult
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.repository.OrderRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class QuizViewModel(
    private val repo: OrderRepository,
    private val auth: FirebaseAuth,
    quizId: String
) : BaseViewModel() {
    companion object {
        private const val DONE = 0L
        private const val ONE_SECOND = 1000L
        private const val COUNTDOWN_TIME = 10000L
    }

    private val timer: CountDownTimer
    private var supervisorJob = SupervisorJob()
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState
    private val _listQuiz = MutableLiveData<List<Quiz>>()
    val listQuiz: LiveData<List<Quiz>>
        get() = _listQuiz
    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean>
        get() = _isEmptyList

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>>
        get() = _questions

    private val _quizResult = MutableLiveData<List<QuizResult>>()
    val quizResult: LiveData<List<QuizResult>>
        get() = _quizResult

    private val _quiz = MutableLiveData<Quiz>()
    val quiz: LiveData<Quiz>
        get() = _quiz

    val review: LiveData<String> = Transformations.map(questions) {
        var result = ""
        it.forEachIndexed { index, question ->
            result += "${index.plus(1)}. ${question.question}\n" +
                    "\tA. ${question.option1}\n" +
                    "\tB. ${question.option2}\n" +
                    "\tC. ${question.option3}\n" +
                    "\tD. ${question.option4}\n" +
                    "\tE. ${question.option5}\n" +
                    "\tF. ${question.option6}\n" +
                    "\tG. ${question.option7}\n" +
                    "\tJawaban benar: ${question.answer}\n\n"
        }
        result
    }

    var questionNumber: LiveData<String>

    private val _currentQuestions = MutableLiveData<Question>()
    val currentQuestions: LiveData<Question>
        get() = _currentQuestions
    private val _position = MutableLiveData<Int>()

    val position: LiveData<Int>
        get() = _position
    val listQuestion = mutableListOf<Question>()

    private val _isQuizDone = MutableLiveData<Boolean>()
    val isQuizDone: LiveData<Boolean>
        get() = _isQuizDone

    init {
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onFinish() {
                _isQuizDone.postValue(true)
            }

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.postValue(millisUntilFinished)
            }
        }
        if (quizId.isNotEmpty()) {
            _isQuizDone.postValue(false)
            getQuestions(quizId)
            getQuizResult(quizId)
            hasJoinedQuiz(quizId)
            _currentQuestions.postValue(Question())
            timer.start()
        } else {
            val quiz = Quiz()
            _quiz.postValue(quiz)
            getQuiz()
        }
        _position.postValue(0)

        questionNumber = Transformations.map(position) {
            var result = ""
            if (it < 10)
                result = "0${it + 1}/10"
            else
                result = "${it + 1}/10"
            result
        }
    }

    val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    val currentTimeString = Transformations.map(currentTime, {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(it) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(it)
            ),
            TimeUnit.MILLISECONDS.toSeconds(it) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(it)
            )
        )
    })

    fun setQuiz(quiz: Quiz) {
        _quiz.postValue(quiz)
    }

    fun createQuiz() {
        _networkState.postValue(NetworkState.RUNNING)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            repo.createQuiz(_quiz.value, listQuestion)
            _networkState.postValue(NetworkState.SUCCESS)
        }
    }

    fun nextQuestion(question: Question) {
        if (_position.value!! < listQuestion.size) {
            listQuestion.set(_position.value!!, question)
        } else {
            listQuestion.add(question)
        }
        val position = position.value?.plus(1)
        _position.value = position
        val sizeExpected = position?.plus(1)
        Log.d(
            "testing",
            "position $position ,expected size: $sizeExpected ,list size: ${listQuestion.size}"
        )
        if (listQuestion.size >= sizeExpected!!) {
            _currentQuestions.postValue(listQuestion[position])
        } else {
            _currentQuestions.postValue(Question())
        }
        _questions.postValue(listQuestion)
    }

    fun previousQuestion(question: Question) {
        if (_position.value!! < listQuestion.size) {
            listQuestion.set(_position.value!!, question)
        }
        val position = _position.value?.minus(1)
        _position.value = position
        if (position != null && position >= 0) {
            _currentQuestions.postValue(listQuestion[position])
        }
        _questions.postValue(listQuestion)
    }

    val expectedAnswers = mutableListOf<String>()

    fun doNextQuestion(answer: String) {
        if (_position.value!! < expectedAnswers.size) {
            expectedAnswers.set(_position.value!!, answer)
        } else {
            expectedAnswers.add(answer)
        }
        val position = position.value?.plus(1)
        _position.value = position
        val sizeExpected = position?.plus(1)
        Log.d(
            "testing",
            "position $position ,expected size: $sizeExpected ,list size: ${listQuestion.size}"
        )
        _currentQuestions.postValue(questions.value!![position!!])
    }

    fun doPreviousQuestion(answer: String) {
        if (_position.value!! < expectedAnswers.size) {
            expectedAnswers.set(_position.value!!, answer)
        }
        val position = _position.value?.minus(1)
        _position.value = position
        if (position != null && position >= 0) {
            _currentQuestions.postValue(questions.value!![position])
        }
    }

    fun getQuiz() {
        _networkState.postValue(NetworkState.RUNNING)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            val quizList = repo.getQuiz()
            _isEmptyList.postValue(quizList.size == 0)
            _listQuiz.postValue(quizList)
            _networkState.postValue(NetworkState.SUCCESS)
        }
    }

    fun getQuestions(quizId: String) {
        _networkState.postValue(NetworkState.RUNNING)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            val questionList = repo.getQuestions(quizId)
            _isEmptyList.postValue(questionList.size == 0)
            _questions.postValue(questionList)
            _currentQuestions.postValue(questionList[0])
            _networkState.postValue(NetworkState.SUCCESS)
        }
    }

    private val _listHasJoined = MutableLiveData<List<QuizResult>>()
    val listHasJoined: LiveData<List<QuizResult>>
        get() = _listHasJoined

    fun hasJoinedQuiz(quizId: String) {
        listHasJoined
        _networkState.postValue(NetworkState.RUNNING)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            val resultList = repo.hasJoinedQuiz(quizId)
            _listHasJoined.postValue(resultList)
            _networkState.postValue(NetworkState.SUCCESS)
        }
    }

    fun getQuizResult(quizId: String) {
        _networkState.postValue(NetworkState.RUNNING)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            val resultList = repo.getQuizResult(quizId)
            _isEmptyList.postValue(resultList.size == 0)
            _quizResult.postValue(resultList)
            _networkState.postValue(NetworkState.SUCCESS)
        }
    }

    fun refreshQuiz() = getQuiz()

    fun refreshQuestion(quizId: String) = getQuestions(quizId)


    fun updateQuiz(quiz: Quiz) {
        _networkState.postValue(NetworkState.RUNNING)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            repo.updateQuiz(quiz)
            _networkState.postValue(NetworkState.SUCCESS)
        }
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(QuizViewModel::class.java.simpleName, "An error happened: $e")
        _networkState.postValue(NetworkState.FAILED)
    }

    fun submitQuiz(lastAnswer: String, cookieName: String?, quizId: String) {
        expectedAnswers.add(lastAnswer)
        Log.d("testing", "size list: ${expectedAnswers.size}")
        val score = getScoreQuiz()
        _networkState.postValue(NetworkState.RUNNING)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            repo.submitQuiz(
                QuizResult(cookieName.toString(), score), quizId
            )
            _networkState.postValue(NetworkState.SUCCESS)
        }
    }

    private fun getScoreQuiz(): Int {
        var score = 0
        expectedAnswers.forEachIndexed { position, expectedAnswer ->
            if (questions.value!![position].answer.equals(expectedAnswer)) {
                score += 10
            }
        }
        return score
    }
}