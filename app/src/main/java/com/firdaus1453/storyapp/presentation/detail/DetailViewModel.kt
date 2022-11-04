package com.firdaus1453.storyapp.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.remote.response.Story
import kotlinx.coroutines.launch

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _story = MutableLiveData<Result<Story?>>()
    val story: LiveData<Result<Story?>> = _story

    fun getDetailStory(id: String) {
        viewModelScope.launch {
            storyRepository.getDetailStory(id).collect {
                _story.value = it
            }
        }
    }
}