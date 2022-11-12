package com.firdaus1453.storyapp.presentation.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.utils.CoroutineTestRule
import com.firdaus1453.storyapp.utils.DataDummy
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
class MapViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: MapViewModel
    private val dummyData = DataDummy.generateDummyStories()

    @Before
    fun setUp() {
        viewModel = MapViewModel(repository)
    }

    @Test
    fun `when getStories Success`() {
        val result = Result.Success(dummyData)

        Mockito.`when`(repository.getStories()).thenReturn(flowOf(result))
        viewModel.getStories()

        val actual = viewModel.stories.getOrAwaitValue()

        Mockito.verify(repository).getStories()
        assertNotNull(actual)
        assertTrue(actual is Result.Success)
        assertEquals(dummyData.size, (actual as Result.Success).data?.size)
    }

    @Test
    fun `when getStories Error`() {
        val result = Result.Error("error")

        Mockito.`when`(repository.getStories()).thenReturn(flowOf(result))
        viewModel.getStories()

        val actual = viewModel.stories.getOrAwaitValue()

        Mockito.verify(repository).getStories()
        assertNotNull(actual)
        assertTrue(actual is Result.Error)
        assertEquals("error", (actual as Result.Error).error)
    }
}