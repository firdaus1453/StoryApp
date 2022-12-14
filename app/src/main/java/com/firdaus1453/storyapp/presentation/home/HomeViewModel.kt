package com.firdaus1453.storyapp.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.local.room.StoriesEntity
import kotlinx.coroutines.launch

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<PagingData<StoriesEntity>>()
    val stories: LiveData<PagingData<StoriesEntity>> = _stories

    fun getStories() {
        viewModelScope.launch {
            storyRepository.getPagingStories().cachedIn(viewModelScope).collect {
                _stories.value = it
            }
        }
    }
}