package com.firdaus1453.storyapp.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.remote.response.Stories
import kotlinx.coroutines.launch

class MapViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<Result<List<Stories>?>>()
    val stories: LiveData<Result<List<Stories>?>> = _stories

    fun getStories() {
        viewModelScope.launch {
            storyRepository.getStories().collect {
                _stories.value = it
            }
        }
    }
}