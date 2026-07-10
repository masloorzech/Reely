package com.example.reely.domain.model

data class MovieCredits(
    val movieId: Int,
    val cast: List<CastMember> = emptyList(),
    val crew: List<CrewMember> = emptyList()
)

data class CastMember(
    val id: Int,
    val name: String,
    val originalName: String? = null,
    val profilePaths: ProfilePaths? = null,
    val character: String? = null,
    val order: Int? = null
)

data class CrewMember(
    val id: Int,
    val name: String,
    val originalName: String?,
    val profilePath: ProfilePaths?,
    val department: String?,
    val job: String?,
    val creditId: String?
)

data class ProfilePaths(
    val localProfilePath: String? = null,
    val remoteProfilePath: String? = null
)