package com.pertamina.spbucare.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.pertamina.spbucare.model.User

class UserStateViewModel(private val state: SavedStateHandle) : BaseViewModel() {
    private val name_key = "user"

    fun getUser(): LiveData<User> = state.getLiveData(name_key)

    fun saveUserData(user: User)  = state.set(name_key, user)

    fun removeUserData(user: User) = state.set(name_key, null)
}