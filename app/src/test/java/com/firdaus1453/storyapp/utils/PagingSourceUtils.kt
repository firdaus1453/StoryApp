package com.firdaus1453.storyapp.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState

class PagingSourceUtils<T : Any>(
    private val data: List<T>
) : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return LoadResult.Page(
            data = data,
            prevKey = null,
            nextKey = null
        )
    }
}