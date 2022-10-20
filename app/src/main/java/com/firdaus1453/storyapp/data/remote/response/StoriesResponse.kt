package com.firdaus1453.storyapp.data.remote.response

data class StoriesResponse(
    val error: Boolean? = false,
    val listStory: List<Stories>? = listOf(),
    val message: String? = ""
)

data class Stories(
    val createdAt: String? = "",
    val description: String? = "",
    val id: String? = "",
    val lat: Any? = Any(),
    val lon: Any? = Any(),
    val name: String? = "",
    val photoUrl: String? = ""
)