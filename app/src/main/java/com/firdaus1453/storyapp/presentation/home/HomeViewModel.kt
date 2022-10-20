package com.firdaus1453.storyapp.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.remote.response.Stories
import kotlinx.coroutines.launch

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<Result<List<Stories>?>>()
    val stories: LiveData<Result<List<Stories>?>> = _stories

    private var token: String? = ""

    init {
        getStories()
    }

    fun getStories() {
        getToken()
        viewModelScope.launch {
            storyRepository.getStories(token ?: "").collect {
                _stories.value = it
            }
        }
    }

    private fun getToken() {
        viewModelScope.launch {
            storyRepository.getUser().collect {
                token = it.token
            }
        }
    }
}