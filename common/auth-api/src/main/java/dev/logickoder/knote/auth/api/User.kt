package dev.logickoder.knote.auth.api

data class User(
    val id: UserId,
    val name: String,
    val email: String,
)
