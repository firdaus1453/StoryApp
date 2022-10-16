package com.firdaus1453.storyapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.local.model.UserPreference
import com.firdaus1453.storyapp.data.remote.ApiConfig

object Injection {
    fun provideRepository(dataStore: DataStore<Preferences>): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(dataStore)
        return StoryRepository.getInstance(apiService, userPreference)
    }
}