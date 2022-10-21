package com.firdaus1453.storyapp.presentation.startup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.local.model.UserModel
import kotlinx.coroutines.launch

class StartupViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _userModel = MutableLiveData<UserModel>()
    val userModel: LiveData<UserModel> = _userModel

    init {
        getToken()
    }

    private fun getToken() {
        viewModelScope.launch {
            storyRepository.getUser().collect {
                _userModel.value = it
            }
        }
    }
}