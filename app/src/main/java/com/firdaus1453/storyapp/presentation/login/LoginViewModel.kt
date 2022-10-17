package com.firdaus1453.storyapp.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.local.model.UserModel
import com.firdaus1453.storyapp.data.remote.body.LoginRequest
import com.firdaus1453.storyapp.data.remote.response.LoginResult
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<LoginResult?>>()
    val loginResult: LiveData<Result<LoginResult?>> = _loginResult

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin

    fun loginUser(loginRequest: LoginRequest) {
        viewModelScope.launch {
            storyRepository.loginUser(loginRequest).collect {
                _loginResult.value = it
            }
        }
    }

    fun saveUser(userModel: UserModel) {
        viewModelScope.launch {
            storyRepository.saveUser(userModel)
        }
    }

    fun checkLogin() {
        viewModelScope.launch {
            storyRepository.isLogin().collect {
                _isLogin.value = it
            }
        }
    }
}