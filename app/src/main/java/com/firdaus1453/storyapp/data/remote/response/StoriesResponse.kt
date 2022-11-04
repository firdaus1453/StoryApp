package com.firdaus1453.storyapp.data.remote.response

data class StoriesResponse(
    val error: Boolean? = false,
    val listStory: List<Stories>? = listOf(),
    val message: String? = ""
)

data class Stories(
    val id: String? = "",
    val name: String? = "",
    val photoUrl: String? = "",
    val description: String? = "",
    val createdAt: String? = "",
    val lat: Double? = 0.0,
    val lon: Double? = 0.0
)