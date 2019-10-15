package com.pertamina.spbucare.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pertamina.spbucare.model.Notification
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.repository.OrderRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NotificationViewModel(private val repo: OrderRepository, private val auth: FirebaseAuth) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState
    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>>
        get() = _notifications
    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean>
        get() = _isEmptyList

    init {
        getNotifications()
    }

    fun getNotifications() {
        _networkState.postValue(NetworkState.RUNNING)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            val notificationList = repo.getNotifications(auth.currentUser?.uid.toString())
            _isEmptyList.postValue(notificationList.size == 0)
            _notifications.postValue(notificationList)
            _networkState.postValue(NetworkState.SUCCESS)
        }
    }

    fun refreshNotification() = getNotifications()

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(NotificationViewModel::class.java.simpleName, "An error happened: $e")
        _networkState.postValue(NetworkState.FAILED)
    }
}