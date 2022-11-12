package com.firdaus1453.storyapp.presentation.startup

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
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class StartupViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var repository: StoryRepository
    private val viewModel by lazy { StartupViewModel(repository) }

    @Test
    fun `when check user is login`() = runTest {
        val result = UserModel("IlhamBaik", "", true)

        Mockito.`when`(repository.getUser()).thenReturn(flowOf(result))
        val actual = viewModel.userModel.value

        Mockito.verify(repository).getUser()
        assertNotNull(actual)
        assertEquals(true, actual?.isLogin)
    }

    @Test
    fun `when check user is not login`() = runTest {
        val result = UserModel("", "", false)

        Mockito.`when`(repository.getUser()).thenReturn(flowOf(result))
        val actual = viewModel.userModel.value

        Mockito.verify(repository).getUser()
        assertNotNull(actual)
        assertEquals(false, actual?.isLogin)
    }
}