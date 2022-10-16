package com.firdaus1453.storyapp.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.local.model.UserModel
import com.firdaus1453.storyapp.data.local.model.UserPreference
import kotlinx.coroutines.launch

class SignupViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            storyRepository.saveUser(user)
        }
    }
}