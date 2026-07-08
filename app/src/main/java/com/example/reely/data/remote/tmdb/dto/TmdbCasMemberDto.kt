package com.example.reely.data.remote.tmdb.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieCreditsDto(
    val id: Int,
    val cast: List<TmdbCastMemberDto> = emptyList(),
    val crew: List<TmdbCrewMemberDto> = emptyList()
)

@Serializable
data class TmdbCastMemberDto(
    val id: Int,

    val name: String? = null,

    @SerialName("original_name")
    val originalName: String? = null,

    @SerialName("profile_path")
    val profilePath: String? = null,

    val character: String? = null,

    val adult: Boolean? = null,

    val gender: Int? = null,

    @SerialName("known_for_department")
    val knownForDepartment: String? = null,

    val popularity: Double? = null,

    @SerialName("cast_id")
    val castId: Int? = null,

    @SerialName("credit_id")
    val creditId: String? = null,

    val order: Int? = null
)

@Serializable
data class TmdbCrewMemberDto(
    val id: Int,

    val name: String? = null,

    @SerialName("original_name")
    val originalName: String? = null,

    @SerialName("profile_path")
    val profilePath: String? = null,

    val adult: Boolean? = null,

    val gender: Int? = null,

    @SerialName("known_for_department")
    val knownForDepartment: String? = null,

    val popularity: Double? = null,

    @SerialName("credit_id")
    val creditId: String? = null,

    val department: String? = null,

    val job: String? = null
)