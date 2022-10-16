package com.firdaus1453.storyapp.presentation.login

import androidx.lifecycle.*
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.local.model.UserModel
import com.firdaus1453.storyapp.data.remote.body.LoginRequest
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun loginUser(loginRequest: LoginRequest) = storyRepository.loginUser(loginRequest)

    fun saveUser(userModel: UserModel) {
        viewModelScope.launch {
            storyRepository.saveUser(userModel)
        }
    }

    fun checkLogin() = storyRepository.isLogin().asLiveData()
}