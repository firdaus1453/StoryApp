package com.firdaus1453.storyapp.utils

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.firdaus1453.storyapp.data.local.room.StoriesEntity

class StoryPagingSource : PagingSource<Int, StoriesEntity>() {
    companion object {
        fun snapshot(items: List<StoriesEntity>): PagingData<StoriesEntity> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoriesEntity>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoriesEntity> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}