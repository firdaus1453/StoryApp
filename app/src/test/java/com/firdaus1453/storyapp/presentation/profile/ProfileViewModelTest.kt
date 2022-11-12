package com.firdaus1453.storyapp.presentation.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.local.model.UserModel
import com.firdaus1453.storyapp.utils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ProfileViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var repository: StoryRepository
    private val viewModel by lazy { ProfileViewModel(repository) }

    @Test
    fun `when get user name Success`() = runTest {
        val result = UserModel("IlhamBaik", "", true)

        `when`(repository.getUser()).thenReturn(flowOf(result))
        val actual = viewModel.nameUser.value

        verify(repository).getUser()
        assertNotNull(actual)
        assertEquals("IlhamBaik", actual)
    }

    @Test
    fun `when get user name empty`() = runTest {
        val result = UserModel("", "", true)

        `when`(repository.getUser()).thenReturn(flowOf(result))
        val actual = viewModel.nameUser.value

        verify(repository).getUser()
        assertNotNull(actual)
        assertEquals("", actual)
    }

    @Test
    fun `logout success`() = runTest {
        viewModel.logout()

        verify(repository).logout()
    }
}