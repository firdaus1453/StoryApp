package com.firdaus1453.storyapp.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.firdaus1453.storyapp.data.local.UserPreference
import com.firdaus1453.storyapp.data.local.model.UserModel
import com.firdaus1453.storyapp.data.local.room.StoriesEntity
import com.firdaus1453.storyapp.data.local.room.StoryDatabase
import com.firdaus1453.storyapp.data.remote.ApiService
import com.firdaus1453.storyapp.data.remote.body.LoginRequest
import com.firdaus1453.storyapp.data.remote.body.SignupRequest
import com.firdaus1453.storyapp.data.remote.response.FileUploadResponse
import com.firdaus1453.storyapp.data.remote.response.LoginResult
import com.firdaus1453.storyapp.data.remote.response.Stories
import com.firdaus1453.storyapp.data.remote.response.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
    private val userPreference: UserPreference
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getPagingStories(): Flow<PagingData<StoriesEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoriesRemoteMediator(apiService, storyDatabase, userPreference),
            pagingSourceFactory = {
                storyDatabase.storiesDao().getAllStories()
            }
        ).flow
    }

    fun addNewStory(
        file: MultipartBody.Part,
        description: RequestBody
    ): Flow<Result<FileUploadResponse?>> = flow {
        emit(Result.Loading)
        try {
            val token = "Bearer " + userPreference.getToken().first()
            val response = apiService.addNewStory(token, file, description)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.d("StoryRepository", "addNewStory: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getDetailStory(id: String): Flow<Result<Story?>> = flow {
        emit(Result.Loading)
        try {
            val token = "Bearer " + userPreference.getToken().first()
            val response = apiService.getDetailStory(token, id)
            val result = response.story
            emit(Result.Success(result))
        } catch (e: Exception) {
            Log.d("StoryRepository", "getDetailStory: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStories(): Flow<Result<List<Stories>?>> = flow {
        emit(Result.Loading)
        try {
            val token = "Bearer " + userPreference.getToken().first()
            val response = apiService.getStories(token)
            val result = response.listStory
            emit(Result.Success(result))
        } catch (e: Exception) {
            Log.d("StoryRepository", "getStories: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun loginUser(loginRequest: LoginRequest): Flow<Result<LoginResult?>> = flow {
        emit(Result.Loading)
        try {
            val responseLogin = apiService.loginUser(loginRequest.email, loginRequest.password)
            val result = responseLogin.loginResult
            emit(Result.Success(result))
        } catch (e: Exception) {
            Log.d("StoryRepository", "loginUser: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun signUp(signupRequest: SignupRequest): Flow<Result<String?>> = flow {
        emit(Result.Loading)
        try {
            val response =
                apiService.signUp(signupRequest.name, signupRequest.email, signupRequest.password)
            val result = response.message
            emit(Result.Success(result))
        } catch (e: Exception) {
            Log.d("StoryRepository", "signup: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getUser(): Flow<UserModel> {
        return userPreference.getUser()
    }

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
            storyDatabase: StoryDatabase,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storyDatabase, userPreference)
            }.also { instance = it }
    }
}