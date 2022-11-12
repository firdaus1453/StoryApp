package com.firdaus1453.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.firdaus1453.storyapp.data.local.pref.UserPreference
import com.firdaus1453.storyapp.data.local.remotekeys.RemoteKeys
import com.firdaus1453.storyapp.data.local.room.StoriesEntity
import com.firdaus1453.storyapp.data.local.room.StoryDatabase
import com.firdaus1453.storyapp.data.remote.ApiService
import com.firdaus1453.storyapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
class StoriesRemoteMediator(
    private val api: ApiService,
    private val database: StoryDatabase,
    private val userPreference: UserPreference
) : RemoteMediator<Int, StoriesEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoriesEntity>,
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }
        wrapEspressoIdlingResource {
            try {
                val token = "Bearer " + userPreference.getToken().first()
                val service = api.getStories(token, page = page, size = state.config.pageSize)
                val endOfPaginationReached = service.listStory?.isEmpty()

                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.remotesKeysDao().deleteRemoteKeys()
                        database.storiesDao().deleteAll()
                    }

                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached == true) null else page + 1
                    val keys = service.listStory?.map {
                        RemoteKeys(id = it.id ?: "", prevKey = prevKey, nextKey = nextKey)
                    }
                    val listStories = service.listStory?.map {
                        StoriesEntity(
                            id = it.id ?: "",
                            name = it.name ?: "",
                            photoUrl = it.photoUrl ?: "",
                            description = it.description ?: "",
                            createdAt = it.createdAt ?: "",
                            latitude = it.lat ?: 0.0,
                            longitude = it.lon ?: 0.0
                        )
                    }
                    database.remotesKeysDao().insertAll(keys ?: listOf())
                    database.storiesDao().insertAll(listStories ?: listOf())
                }
                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached == true)
            } catch (e: Exception) {
                return MediatorResult.Error(e)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoriesEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remotesKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoriesEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remotesKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoriesEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remotesKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}