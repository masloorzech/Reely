package com.example.reely.domain.model

@JvmInline
value class UserId(val value: Int)

val SHARED_WATCHLIST_USER_ID : UserId = UserId(0)
data class User(
    val id : UserId,
    val nickname: String,
    val avatarPath: String?
)