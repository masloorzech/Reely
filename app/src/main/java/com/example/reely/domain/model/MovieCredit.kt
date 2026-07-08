package com.example.reely.domain.model

data class MovieCredits(
    val movieId: Int,
    val cast: List<CastMember> = emptyList()
)

data class CastMember(
    val id: Int,
    val name: String,
    val originalName: String? = null,
    val profilePaths: ProfilePaths? = null,
    val character: String? = null,
    val order: Int? = null
)

data class ProfilePaths(
    val localProfilePath: String? = null,
    val remoteProfilePath: String? = null
)