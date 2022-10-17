package com.firdaus1453.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.firdaus1453.storyapp.data.local.UserPreference
import com.firdaus1453.storyapp.data.local.model.UserModel
import com.firdaus1453.storyapp.data.remote.ApiService
import com.firdaus1453.storyapp.data.remote.body.LoginRequest
import com.firdaus1453.storyapp.data.remote.body.SignupRequest
import com.firdaus1453.storyapp.data.remote.response.LoginResult
import kotlinx.coroutines.flow.Flow

class StoryRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    fun loginUser(loginRequest: LoginRequest) : LiveData<Result<LoginResult?>> = liveData {
      emit(Result.Loading)
      try {
          val responseLogin = apiService.loginUser(loginRequest.email,loginRequest.password)
          val result = responseLogin.loginResult
          emit(Result.Success(result))
      }  catch (e: Exception) {
          Log.d("StoryRepository", "loginUser: ${e.message.toString()} ")
          emit(Result.Error(e.message.toString()))
      }
    }

    fun signUp(signupRequest: SignupRequest) : LiveData<Result<String?>> = liveData {
      emit(Result.Loading)
      try {
          val response = apiService.signUp(signupRequest.name,signupRequest.email,signupRequest.password)
          val result = response.message
          emit(Result.Success(result))
      }  catch (e: Exception) {
          Log.d("StoryRepository", "signup: ${e.message.toString()} ")
          emit(Result.Error(e.message.toString()))
      }
    }

    fun getUser(): Flow<UserModel> {
       return userPreference.getUser()
    }

    fun isLogin() = userPreference.isLogin()

    suspend fun saveUser(user: UserModel) {
       userPreference.saveUser(user)
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