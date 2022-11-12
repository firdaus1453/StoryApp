package com.firdaus1453.storyapp.presentation.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.remote.body.SignupRequest
import com.firdaus1453.storyapp.utils.CoroutineTestRule
import com.firdaus1453.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SignupViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: SignupViewModel

    @Before
    fun setUp() {
        viewModel = SignupViewModel(repository)
    }

    @Test
    fun `when sign up Success`() {
        val result = Result.Success("success")
        val signUpGiven = SignupRequest("test", "email@test.com", "testPass")

        Mockito.`when`(repository.signUp(signUpGiven)).thenReturn(flowOf(result))
        viewModel.signUp(signUpGiven)

        val actual = viewModel.signupResult.getOrAwaitValue()

        Mockito.verify(repository).signUp(signUpGiven)
        assertNotNull(actual)
        assertTrue(actual is Result.Success)
        assertEquals("success", (actual as Result.Success).data)
    }

    @Test
    fun `when sign up Error`() {
        val result = Result.Error("error")
        val signUpGiven = SignupRequest()

        Mockito.`when`(repository.signUp(signUpGiven)).thenReturn(flowOf(result))
        viewModel.signUp(signUpGiven)

        val actual = viewModel.signupResult.getOrAwaitValue()

        Mockito.verify(repository).signUp(signUpGiven)
        assertNotNull(actual)
        assertTrue(actual is Result.Error)
        assertEquals("error", (actual as Result.Error).error)
    }
}