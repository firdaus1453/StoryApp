package com.firdaus1453.storyapp.presentation.createstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.remote.response.FileUploadResponse
import kotlinx.coroutines.launch
import java.io.File

class CreateStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _addNewStory = MutableLiveData<Result<FileUploadResponse?>>()
    val addNewStory: LiveData<Result<FileUploadResponse?>> = _addNewStory

    fun addNewStory(
        file: File,
        description: String,
        lat: Float?,
        lon: Float?,
    ) {
        viewModelScope.launch {
            storyRepository.addNewStory(file, description, lat, lon).collect {
                _addNewStory.value = it
            }
        }
    }
}