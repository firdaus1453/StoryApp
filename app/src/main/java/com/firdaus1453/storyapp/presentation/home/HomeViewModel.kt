package com.firdaus1453.storyapp.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.remote.response.Stories
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<Result<List<Stories>?>>()
    val stories: LiveData<Result<List<Stories>?>> = _stories

    private val _notLogin = MutableLiveData<String>()
    val notLogin: LiveData<String> = _notLogin

    fun getStories(token: String) {
        viewModelScope.launch {
            storyRepository.getStories(token).collect {
                _stories.value = it
            }

            when (val dataStories = _stories.value) {
                is Result.Success -> {
                    if (dataStories.data?.isNotEmpty() == true) {
                        val jsonString = Gson().toJson(dataStories.data)
                        storyRepository.saveStories(jsonString)
                    }
                }
                else -> {
                    // not use
                }
            }
        }

    }

    fun getToken() {
        viewModelScope.launch {
            _notLogin.value = storyRepository.getUser().first().token
        }
    }
}