package com.example.reely.domain.model

data class MovieCredits(
    val movieId: Int,
    val cast: List<CastMember>,
    val crew: List<CrewMember>,
)

data class CastMember(
    val id: Int,
    val name: String,
    val originalName: String?,
    val profilePath: String?,
    val character: String?,
    val order: Int?,
)

data class CrewMember(
    val id: Int,
    val name: String,
    val originalName: String?,
    val profilePath: String?,
    val department: String?,
    val job: String?,
    val creditId: String?,
)