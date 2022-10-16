package com.firdaus1453.storyapp.presentation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.presentation.login.LoginViewModel
import com.firdaus1453.storyapp.presentation.signup.SignupViewModel
import com.firdaus1453.storyapp.di.Injection


class ViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(dataStore : DataStore<Preferences>): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(dataStore))
            }.also { instance = it }
    }
}