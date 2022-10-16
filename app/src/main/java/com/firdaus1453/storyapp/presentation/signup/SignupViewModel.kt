package com.firdaus1453.storyapp.presentation.signup

import androidx.lifecycle.ViewModel
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.remote.body.SignupRequest

class SignupViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun signUp(signupRequest: SignupRequest) = storyRepository.signUp(signupRequest)
}