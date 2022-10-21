package com.firdaus1453.storyapp.data.remote.response

data class DetailStoryResponse(
    val error: Boolean? = false,
    val message: String? = "",
    val story: Story? = Story()
)

data class Story(
    val createdAt: String? = "",
    val description: String? = "",
    val id: String? = "",
    val lat: Double? = 0.0,
    val lon: Double? = 0.0,
    val name: String? = "",
    val photoUrl: String? = ""
)