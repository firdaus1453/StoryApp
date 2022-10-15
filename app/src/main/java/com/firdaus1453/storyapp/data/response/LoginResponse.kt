package com.firdaus1453.storyapp.data.response

data class LoginResponse(
    val error: Boolean? = false,
    val loginResult: LoginResult? = LoginResult(),
    val message: String? = ""
)

data class LoginResult(
    val name: String? = "",
    val token: String? = "",
    val userId: String? = ""
)