package com.firdaus1453.storyapp.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.local.model.UserModel
import com.firdaus1453.storyapp.data.local.model.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return storyRepository.getUser().asLiveData()
    }

    fun login() {
        viewModelScope.launch {
            storyRepository.login()
        }
    }
}