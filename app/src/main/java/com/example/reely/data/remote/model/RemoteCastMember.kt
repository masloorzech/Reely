package com.example.reely.data.remote.model

data class RemoteMovieCredits(
    val movieId: Int,
    val cast: List<RemoteCastMember> = emptyList(),
    val crew: List<RemoteCrewMember> = emptyList()
)

data class RemoteCastMember(
    val id: Int,
    val name: String,
    val originalName: String?,
    val profilePath: String?,
    val character: String?,
    val order: Int?
)

data class RemoteCrewMember(
    val id: Int,
    val name: String,
    val originalName: String?,
    val profilePath: String?,
    val department: String?,
    val job: String?,
    val creditId: String?
)
