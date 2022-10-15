package com.firdaus1453.storyapp.data

import com.firdaus1453.storyapp.data.remote.ApiService

class StoryRepository(
    private val apiService: ApiService,
) {
    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }
}