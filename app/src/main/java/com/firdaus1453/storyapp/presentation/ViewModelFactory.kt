package com.firdaus1453.storyapp.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.presentation.login.LoginViewModel
import com.firdaus1453.storyapp.presentation.signup.SignupViewModel
import com.firdaus1453.storyapp.di.Injection
import com.firdaus1453.storyapp.presentation.createstory.CreateStoryViewModel
import com.firdaus1453.storyapp.presentation.detail.DetailViewModel
import com.firdaus1453.storyapp.presentation.home.HomeViewModel
import com.firdaus1453.storyapp.presentation.profile.ProfileViewModel
import com.firdaus1453.storyapp.presentation.startup.StartupViewModel


class ViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(CreateStoryViewModel::class.java) -> {
                CreateStoryViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(StartupViewModel::class.java) -> {
                StartupViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {

        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}