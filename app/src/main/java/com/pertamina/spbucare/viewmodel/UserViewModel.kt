package com.pertamina.spbucare.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pertamina.spbucare.model.User
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.repository.OrderRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: OrderRepository,
    private val auth: FirebaseAuth,
    userType: String
) : BaseViewModel() {

    private var supervisorJob = SupervisorJob()
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
        get() = _users
    private val _userTypeInput = MutableLiveData<String>()
    val userTypeInput : LiveData<String>
        get() = _userTypeInput
    val networkState = MutableLiveData<NetworkState>()

    init {
        when (userType) {
            "spbu", "pertamina", "ser" -> getUsers(userType) //get user list by type
            "" -> getUser() //get current user login
            else -> getUser(userType) //get single user with specific uid
        }
    }

    fun setUserTypeInput(userType: String) {
        _userTypeInput.postValue(userType)
    }

    fun getUser(uid: String = auth.currentUser?.uid.toString()) {
        networkState.postValue(NetworkState.RUNNING)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            _user.postValue(repository.getUser(uid))
            networkState.postValue(NetworkState.SUCCESS)
        }
    }

    fun getUsers(userType: String) {
        networkState.postValue(NetworkState.RUNNING)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            _users.postValue(repository.getUsers(userType))
            networkState.postValue(NetworkState.SUCCESS)
        }
    }

    fun createUser(email: String, password: String, user: User){
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            user.userId = repository.createAccount(email, password)
            Log.d("testing", "uid: ${user.userId}")
            repository.createUserData(user.userId, user)
        }
    }

    fun updateAccount(uid: String, email: String, password: String) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            repository.updateAccount(uid, email, password)
        }
    }

    private val _accountChanged = MutableLiveData<Boolean>()
    val accountChanged : LiveData<Boolean>
        get() = _accountChanged

    fun changePassword(password: String) {
        _accountChanged.postValue(false)
            auth.currentUser?.updatePassword(password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _accountChanged.postValue(true)
                    }
                }
    }

    fun updateUser(user: User) {
        _accountChanged.postValue(false)
        networkState.postValue(NetworkState.RUNNING)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            repository.updateUser(user)
            networkState.postValue(NetworkState.SUCCESS)
            _accountChanged.postValue(true)

        }
    }

    fun signout() {
        networkState.postValue(NetworkState.RUNNING)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            val user = repository.getUser(auth.currentUser?.uid.toString())
            user?.token = ""
            user?.let { repository.updateUser(it) }
            auth.signOut()
            networkState.postValue(NetworkState.SUCCESS)
        }
    }

    fun refreshUsers(type: String) = getUsers(type)

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, _ ->
        networkState.postValue(NetworkState.FAILED)
    }
}