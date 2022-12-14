package com.firdaus1453.storyapp.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firdaus1453.storyapp.data.StoryRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _nameUser = MutableLiveData<String>()
    val nameUser: LiveData<String> = _nameUser

    init {
        getNameUser()
    }

    fun logout() {
        viewModelScope.launch {
            storyRepository.logout()
        }
    }

    private fun getNameUser() {
        viewModelScope.launch {
            storyRepository.getUser().collect {
                _nameUser.value = it.name
            }
        }
    }
}