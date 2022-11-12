package com.firdaus1453.storyapp.presentation.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.remote.response.Story
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setUp() {
        viewModel = DetailViewModel(repository)
    }

    @Test
    fun `when getDetailStory Success`() {
        val result: Result<Story?> = Result.Success(Story(id = "1"))

        `when`(repository.getDetailStory("1")).thenReturn(flowOf(result))
        viewModel.getDetailStory("1")

        val actual = viewModel.story.getOrAwaitValue()

        verify(repository).getDetailStory("1")
        assertNotNull(actual)
        assertTrue(actual is Result.Success)
        assertEquals("1", (actual as Result.Success).data?.id)
    }

    @Test
    fun `when getDetailStory Error`() {
        val result: Result<Story?> = Result.Error("error")

        `when`(repository.getDetailStory("1")).thenReturn(flowOf(result))
        viewModel.getDetailStory("1")

        val actual = viewModel.story.getOrAwaitValue()

        verify(repository).getDetailStory("1")
        assertNotNull(actual)
        assertTrue(actual is Result.Error)
        assertEquals("error", (actual as Result.Error).error)
    }
}