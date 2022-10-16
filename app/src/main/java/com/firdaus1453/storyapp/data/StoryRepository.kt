package com.firdaus1453.storyapp.data

import com.firdaus1453.storyapp.data.local.model.UserModel
import com.firdaus1453.storyapp.data.local.UserPreference
import com.firdaus1453.storyapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class StoryRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    fun getUser(): Flow<UserModel> {
       return userPreference.getUser()
    }

    suspend fun saveUser(user: UserModel) {
       userPreference.saveUser(user)
    }

    suspend fun login() {
        userPreference.login()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
}