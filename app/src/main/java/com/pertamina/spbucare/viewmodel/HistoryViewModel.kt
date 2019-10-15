package com.pertamina.spbucare.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.FirebaseAuth
import com.pertamina.spbucare.model.Order
import com.pertamina.spbucare.model.User
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.repository.OrderRepository
import com.pertamina.spbucare.util.USER_TYPE
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*

class HistoryViewModel(
        private val repo: OrderRepository,
        private val auth: FirebaseAuth,
        private val category: String) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState
    private val _history = MutableLiveData<List<Order>>()
    val history: LiveData<List<Order>>
        get() = _history

    private val _region = MutableLiveData<String>()
    val region: LiveData<String>
        get() = _region

    private val _topTen = MutableLiveData<List<Order>>()
    val topTen: LiveData<List<Order>>
        get() = _topTen

    private val _beginDate = MutableLiveData<Date>()
    val beginDate: LiveData<Date>
        get() = _beginDate

    private val _endDate = MutableLiveData<Date>()
    val endDate: LiveData<Date>
        get() = _endDate
    private val _users = MutableLiveData<List<User>>()
    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean>
        get() = _isEmptyList

    init {
        _region.postValue("")
        val date = Calendar.getInstance()
        date.set(Calendar.HOUR_OF_DAY, 23)
        val endDate = date.time //date now
        if (category == "Top") {
            date.add(Calendar.DATE, -30) //date 30 days ago
        } else if(category == "Darurat") {
            getUsersSpbu()
            date.add(Calendar.DATE, -1) //date 7 days ago
        } else {
            getUsersSpbu()
            date.add(Calendar.DATE, -7) //date 7 days ago
        }
        date.set(Calendar.HOUR_OF_DAY, 0)
        val beginDate = date.time
        if (category == "Top") {
            getTopTen(beginDate, endDate, "")
        } else {
            getHistory(USER_TYPE, category, beginDate, endDate)
        }
    }

    fun setRegion(region: String) {
        val date = Calendar.getInstance()
        date.set(Calendar.HOUR_OF_DAY, 23)
        val endDate = date.time //date now
        date.add(Calendar.DATE, -30) //date 30 days ago
        date.set(Calendar.HOUR_OF_DAY, 0)
        val beginDate = date.time
        val ser = when (region) {
            "Blitar T. Agung" -> "Abq4TLEs6zUccXVTPm8JtIuXeHE3"
            "Lumajang" -> "0Nv8LiPFx5NnvnYMptxu4rxrHy12"
            "Malang" -> "6MARPQw4ToVY1uZBqckD5dLeEJm1"
            else -> ""
        }
        getTopTen(beginDate, endDate, ser)
    }

    fun getHistory(userType: String, category: String, beginDate: Date, endDate: Date, ser: String = "") {
        _endDate.postValue(endDate)
        _beginDate.postValue(beginDate)

        _networkState.postValue(NetworkState.RUNNING)
        var historyList: List<Order>
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            historyList = if (category.isEmpty()) {
                repo.getOrders(userType, auth.currentUser?.uid.toString())
            } else {
                if (category == "Top") {
                    repo.getOrdersByCategory(category, beginDate, endDate, ser)
                } else {
                    repo.getOrdersByCategory(category, beginDate, endDate)
                }
            }
            _isEmptyList.postValue(historyList.isEmpty())
            _history.postValue(historyList)
            _networkState.postValue(NetworkState.SUCCESS)
        }
    }

    fun getTopTen(beginDate: Date, endDate: Date, ser: String) {
        _endDate.postValue(endDate)
        _beginDate.postValue(beginDate)

        _networkState.postValue(NetworkState.RUNNING)
        var historyList: List<Order>
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            historyList = repo.getOrdersByCategory(category, beginDate, endDate, ser)
            _isEmptyList.postValue(historyList.isEmpty())
            _topTen.postValue(historyList)
            _networkState.postValue(NetworkState.SUCCESS)
        }
    }

    private fun getUsersSpbu() {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            _users.postValue(repo.getUsers("spbu"))
        }
    }

    fun accumulateHistory(): MutableList<Order> {
        val orders = _history.value
        val users = _users.value
        var orders2 = mutableListOf<Order>()
        orders?.forEach {
            var position = -1
            if (orders2.size == 0) {
                orders2.add(it)
            } else {
                for (i in 0 until orders2.size) {
                    if (orders2[i].applicantName == it.applicantName) {
                        position = i
                    }
                }
                if (position != -1) {
                    orders2[position].orderVolume.premium += it.orderVolume.premium
                    orders2[position].orderVolume.biosolar += it.orderVolume.biosolar
                    orders2[position].orderVolume.pertamax += it.orderVolume.pertamax
                    orders2[position].orderVolume.pertalite += it.orderVolume.pertalite
                    orders2[position].orderVolume.dexlite += it.orderVolume.dexlite
                    orders2[position].orderVolume.pertadex += it.orderVolume.pertadex
                    orders2[position].orderVolume.pxturbo += it.orderVolume.pxturbo
                } else {
                    orders2.add(it)
                }
            }
        }
            val orders3 = mutableListOf<Order>()
            // convert list users to mutablelist users
            val convertUsers = users?.toMutableList()
            // mutablelist for object that same between mutablelist _users and _orders2 by applicantname OR name
            val users2 = mutableListOf<User>()

        for (i in 0 until orders2.size) {
                convertUsers?.forEach inner@{ user ->
                    if (orders2[i].applicantName == user.name) {
                        orders2[i].adress = user.adress
                        users2.add(user)
                        return@inner
                    }
                }
            }

            //remove object in collection _users which same object with object in collection _users2
            convertUsers?.removeAll(users2)
            convertUsers?.forEach {
                orders3.add(
                    Order(
                        userId = it.userId,
                        applicantName = it.name,
                        adress = it.adress,
                        open = false
                    )
                )
            }
            orders2.addAll(orders3)
        orders2 = orders2.sortedBy {it.applicantName}.toMutableList()
        return orders2
    }

    fun refreshHistory(userType: String = "") {
        val date = Calendar.getInstance()
        date.set(Calendar.HOUR_OF_DAY, 23)
        val end = _endDate.value ?: date.time
        if (category == "Semua") {
            date.add(Calendar.DATE, -30) //date 30 days ago
        } else {
            date.add(Calendar.DATE, -7) //date 7 days ago
        }
        date.set(Calendar.HOUR_OF_DAY, 1)
        val begin = _beginDate.value ?: date.time
        getHistory(userType, category, begin, end)
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(HistoryViewModel::class.java.simpleName, "An error happened: $e")
        _networkState.postValue(NetworkState.FAILED)
    }
}