package com.firdaus1453.storyapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.firdaus1453.storyapp.data.local.remotekeys.RemoteKeys
import com.firdaus1453.storyapp.data.local.remotekeys.RemoteKeysDao

@Database(
    entities = [
        StoriesEntity::class,
        RemoteKeys::class
    ], version = 1, exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storiesDao(): StoriesDao
    abstract fun remotesKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, "story.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}