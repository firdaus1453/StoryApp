package com.firdaus1453.storyapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.firdaus1453.storyapp.data.local.pref.UserPreference
import com.firdaus1453.storyapp.data.local.room.StoriesDao
import com.firdaus1453.storyapp.data.local.room.StoryDatabase
import com.firdaus1453.storyapp.data.remote.ApiService
import com.firdaus1453.storyapp.utils.DataDummy
import com.firdaus1453.storyapp.utils.MainDispatcherRule
import com.firdaus1453.storyapp.utils.PagingSourceUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


@OptIn(ExperimentalCoroutinesApi::class)
class StoryRepositoryTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private val apiService = mock(ApiService::class.java)
    private val storyDatabase = mock(StoryDatabase::class.java)
    private val userPreference = mock(UserPreference::class.java)
    private val storiesDao = mock(StoriesDao::class.java)
    private lateinit var repository: StoryRepository

    @Before
    fun setUp() {
        repository = StoryRepository(apiService, storyDatabase, storiesDao, userPreference)
    }

    @Test
    fun `get story paging source success`() = runTest {
        val stories = DataDummy.generateDummyStoriesEntity()
        `when`(storiesDao.getAllStories()).thenReturn(PagingSourceUtils(stories))

        val actual = repository.getPagingStories().first()

        Assert.assertNotNull(actual)
    }
}