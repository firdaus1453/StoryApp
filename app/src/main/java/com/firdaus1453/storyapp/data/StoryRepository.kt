package com.firdaus1453.storyapp.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.firdaus1453.storyapp.data.local.model.UserModel
import com.firdaus1453.storyapp.data.local.pref.UserPreference
import com.firdaus1453.storyapp.data.local.room.StoriesDao
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
    private val dao: StoriesDao,
    private val userPreference: UserPreference
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getPagingStories(): Flow<PagingData<StoriesEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoriesRemoteMediator(apiService, storyDatabase, userPreference),
            pagingSourceFactory = {
                dao.getAllStories()
            }
        ).flow
    }

    fun addNewStory(
        file: File,
        description: String,
        lat: Float?,
        lon: Float?
    ): Flow<Result<FileUploadResponse?>> = flow {
        emit(Result.Loading)
        var latRequestBody: RequestBody? = null
        var lonRequestBody: RequestBody? = null
        try {
            if (lat != null && lon != null) {
                latRequestBody = lat.toString().toRequestBody("text/plain".toMediaType())
                lonRequestBody = lon.toString().toRequestBody("text/plain".toMediaType())
            }
            val mDescription =
                description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val token = "Bearer " + userPreference.getToken().first()
            val response =
                apiService.addNewStory(
                    token,
                    imageMultipart,
                    mDescription,
                    latRequestBody,
                    lonRequestBody
                )
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
            storiesDao: StoriesDao,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storyDatabase, storiesDao, userPreference)
            }.also { instance = it }
    }
}