package com.example.reely.domain.model

@JvmInline
value class ProfileId(
    val value: Int,
)

enum class ProfilePosition{
    FIRST,
    SECOND,
}

data class Profile(
    val id: ProfileId,
    val position: ProfilePosition,
    val name: String,
    val avatarPath: String?
)
