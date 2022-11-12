package com.firdaus1453.storyapp.utils

import com.firdaus1453.storyapp.data.local.room.StoriesEntity
import com.firdaus1453.storyapp.data.remote.response.Stories

object DataDummy {
    fun generateDummyStoriesEntity(): List<StoriesEntity> {
        val stories = ArrayList<StoriesEntity>()
        for (i in 0..10) {
            val news = StoriesEntity(
                "$i",
                "name $i",
                "https://story-api.dicoding.dev/images/stories/photos-1668070458217_KBCAcozo.jpg",
                "desc $i",
                "2022-11-10T08:56:49.255Z",
                -3.3186066,
                114.594376,
            )
            stories.add(news)
        }
        return stories
    }

    fun generateDummyStories(): List<Stories> {
        val stories = ArrayList<Stories>()
        for (i in 0..10) {
            val news = Stories(
                "$i",
                "name $i",
                "https://story-api.dicoding.dev/images/stories/photos-1668070458217_KBCAcozo.jpg",
                "desc $i",
                "2022-11-10T08:56:49.255Z",
                -3.3186066,
                114.594376,
            )
            stories.add(news)
        }
        return stories
    }
}
