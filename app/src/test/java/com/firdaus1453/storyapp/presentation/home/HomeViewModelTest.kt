package com.firdaus1453.storyapp.presentation.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.data.local.room.StoriesEntity
import com.firdaus1453.storyapp.utils.CoroutineTestRule
import com.firdaus1453.storyapp.utils.DataDummy
import com.firdaus1453.storyapp.utils.StoryPagingSource
import com.firdaus1453.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var homeViewModel: HomeViewModel
    private val dummyData = DataDummy.generateDummyStoriesEntity()

    @Before
    fun setUp() {
        homeViewModel = HomeViewModel(repository)
    }

    @Test
    fun `when Get Paging Stories Success`() = runTest {
        val data: PagingData<StoriesEntity> =
            StoryPagingSource.snapshot(dummyData)

        `when`(repository.getPagingStories()).thenReturn(flowOf(data))
        homeViewModel.getStories()

        val actual = homeViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = HomeAdapter.MyDiffUtil,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actual)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyData, differ.snapshot())
        Assert.assertEquals(dummyData.size, differ.snapshot().size)
    }

    @Test
    fun `when Get Paging Stories data is empty`() = runTest {
        val data: PagingData<StoriesEntity> =
            StoryPagingSource.snapshot(listOf())

        `when`(repository.getPagingStories()).thenReturn(flowOf(data))
        homeViewModel.getStories()

        val actual = homeViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = HomeAdapter.MyDiffUtil,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actual)

        Assert.assertEquals(listOf<StoriesEntity>(), differ.snapshot())
        Assert.assertEquals(0, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}