package com.firdaus1453.storyapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stories: List<StoriesEntity>)

    @Query("SELECT * from stories")
    fun getAllStories(): PagingSource<Int, StoriesEntity>

    @Query("DELETE from  stories")
    suspend fun deleteAll()
}