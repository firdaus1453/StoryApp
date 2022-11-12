package com.firdaus1453.storyapp.presentation.createstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.remote.response.FileUploadResponse
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
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class CreateStoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var file: File

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: CreateStoryViewModel

    @Before
    fun setUp() {
        viewModel = CreateStoryViewModel(repository)
    }

    @Test
    fun `when addNewStory Success`() {
        val result: Result<FileUploadResponse?> =
            Result.Success(FileUploadResponse(false, "success"))
        val lat = -2.3213F
        val lon = 1.234F

        `when`(repository.addNewStory(file, "desc", lat, lon)).thenReturn(flowOf(result))
        viewModel.addNewStory(file, "desc", lat, lon)

        val actual = viewModel.addNewStory.getOrAwaitValue()

        verify(repository).addNewStory(file, "desc", lat, lon)
        assertNotNull(actual)
        assertTrue(actual is Result.Success)
        assertEquals("success", (actual as Result.Success).data?.message)
    }

    @Test
    fun `when addNewStory Failed`() {
        val result: Result<FileUploadResponse?> = Result.Error("error")
        val lat = -2.3213F
        val lon = 1.234F

        `when`(repository.addNewStory(file, "desc", lat, lon)).thenReturn(flowOf(result))
        viewModel.addNewStory(file, "desc", lat, lon)

        val actual = viewModel.addNewStory.getOrAwaitValue()

        verify(repository).addNewStory(file, "desc", lat, lon)
        assertNotNull(actual)
        assertTrue(actual is Result.Error)
        assertEquals("error", (actual as Result.Error).error)
    }
}