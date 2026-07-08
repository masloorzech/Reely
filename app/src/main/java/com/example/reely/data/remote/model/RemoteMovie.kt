package com.example.reely.data.remote.model

import com.example.reely.data.remote.tmdb.toTmdbPosterUrl
import com.example.reely.domain.model.Movie
import com.example.reely.domain.model.PosterPaths
import com.example.reely.domain.model.PublicRating

data class RemoteMovie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val rating: Double?,
    val voteCount: Int?,
    val popularity: Double?,
    val runtime: Int? = null,
    val genres: List<String> = emptyList()
)

data class RemoteMovieWatchProviders(
    val movieId: Int,
    val countryCode: String = "PL",
    val link: String? = null,
    val flatrate: List<RemoteWatchProvider> = emptyList(),
    val rent: List<RemoteWatchProvider> = emptyList(),
    val buy: List<RemoteWatchProvider> = emptyList(),
    val ads: List<RemoteWatchProvider> = emptyList(),
    val free: List<RemoteWatchProvider> = emptyList()
)

data class RemoteWatchProvider(
    val providerId: Int,
    val providerName: String,
    val logoPath: String?,
    val displayPriority: Int?
)


fun RemoteMovie.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterPaths = PosterPaths(
            localPosterPath = null,
            remotePosterPath = posterPath?.toTmdbPosterUrl()
        ),
        releaseDate = releaseDate,
        runtime = runtime,
        genres = genres,
        publicRating = PublicRating(
            average = rating,
            voteCount = voteCount
        )
    )
}