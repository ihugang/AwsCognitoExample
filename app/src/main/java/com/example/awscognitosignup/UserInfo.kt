package com.example.awscognitosignup

data class UserInformation(
    val nickname: String,
    val email: String,
    val sub: String,
    val emailVerified: String?
    )