package com.example.reely.domain.model

@JvmInline
value class UserId(val value: Int)

data class User(
    val id : UserId,
    val nickname: String,
    val avatarPath: String?
)