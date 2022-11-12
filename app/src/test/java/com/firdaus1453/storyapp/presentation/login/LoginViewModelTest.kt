package com.firdaus1453.storyapp.presentation.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.local.model.UserModel
import com.firdaus1453.storyapp.data.remote.body.LoginRequest
import com.firdaus1453.storyapp.data.remote.response.LoginResult
import com.firdaus1453.storyapp.utils.CoroutineTestRule
import com.firdaus1453.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel(repository)
    }

    @Test
    fun `when login is Success`() {
        val result: Result<LoginResult?> = Result.Success(LoginResult(token = "tokenTest"))
        val loginGiven = LoginRequest("email@test.com", "passTest")

        `when`(repository.loginUser(loginGiven)).thenReturn(flowOf(result))
        viewModel.loginUser(loginGiven)

        val actual = viewModel.loginResult.getOrAwaitValue()

        verify(repository).loginUser(loginGiven)
        assertNotNull(actual)
        assertTrue(actual is Result.Success)
        assertEquals("tokenTest", (actual as Result.Success).data?.token)
    }

    @Test
    fun `when login is Error`() {
        val result: Result<LoginResult?> = Result.Error("error")
        val loginGiven = LoginRequest("email@test.com", "passTest")

        `when`(repository.loginUser(loginGiven)).thenReturn(flowOf(result))
        viewModel.loginUser(loginGiven)

        val actual = viewModel.loginResult.getOrAwaitValue()

        verify(repository).loginUser(loginGiven)
        assertNotNull(actual)
        assertTrue(actual is Result.Error)
        assertEquals("error", (actual as Result.Error).error)
    }

    @Test
    fun `when save user after login`() = runTest {
        val givenUserModel = UserModel("ilhamBaik", "testToken", true)
        viewModel.saveUser(givenUserModel)

        verify(repository).saveUser(givenUserModel)
    }
}