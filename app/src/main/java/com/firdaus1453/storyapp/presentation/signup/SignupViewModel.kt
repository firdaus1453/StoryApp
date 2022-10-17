package com.firdaus1453.storyapp.presentation.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.remote.body.SignupRequest
import kotlinx.coroutines.launch

class SignupViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _signupResult = MutableLiveData<Result<String?>>()
    val signupResult: LiveData<Result<String?>> = _signupResult

    fun signUp(signupRequest: SignupRequest) {
        viewModelScope.launch {
            storyRepository.signUp(signupRequest).collect {
                _signupResult.value = it
            }
        }
    }
}