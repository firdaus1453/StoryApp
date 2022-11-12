package com.firdaus1453.storyapp.di

import android.content.Context
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.local.pref.UserPreference
import com.firdaus1453.storyapp.data.local.room.StoryDatabase
import com.firdaus1453.storyapp.data.remote.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context)
        val storyDatabase = StoryDatabase.getDatabase(context)
        val dao = storyDatabase.storiesDao()
        return StoryRepository.getInstance(apiService, storyDatabase, dao, userPreference)
    }
}