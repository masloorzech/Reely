package com.example.reely.data.remote.tmdb.mapper

import com.example.reely.data.remote.tmdb.dto.TmdbCastMemberDto
import com.example.reely.data.remote.tmdb.dto.TmdbCrewMemberDto
import com.example.reely.data.remote.tmdb.dto.TmdbMovieCreditsDto
import com.example.reely.data.remote.tmdb.dto.TmdbMovieDetailsDto
import com.example.reely.data.remote.tmdb.dto.TmdbMovieDto
import com.example.reely.data.remote.tmdb.dto.TmdbMovieWatchProvidersDto
import com.example.reely.data.remote.tmdb.dto.TmdbWatchProviderDto
import com.example.reely.data.remote.tmdb.toTmdbLogoUrl
import com.example.reely.data.remote.tmdb.toTmdbPosterUrl
import com.example.reely.data.remote.tmdb.toTmdbProfileUrl
import com.example.reely.domain.model.CastMember
import com.example.reely.domain.model.CrewMember
import com.example.reely.domain.model.Genre
import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.model.MoviePreview
import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.model.WatchProvider
import java.time.LocalDate

fun TmdbMovieDto.toMoviePreview(): MoviePreview {
    return MoviePreview(
        id = id,
        title = title ?: originalTitle.orEmpty(),
        posterPath = posterPath?.toTmdbPosterUrl(),
        releaseDate = releaseDate.toLocalDateOrNull()
    )
}

fun TmdbMovieDetailsDto.toMovieDetails(): MovieDetails {
    return MovieDetails(
        id = id,
        title = title ?: originalTitle.orEmpty(),
        originalTitle = originalTitle,
        overview = overview.orEmpty(),
        posterPath = posterPath?.toTmdbPosterUrl(),
        backdropPath = backdropPath,
        releaseDate = releaseDate.toLocalDateOrNull(),
        runtimeMinutes = runtime,
        originalLanguage = originalLanguage,
        publicRating = voteAverage,
        publicVoteCount = voteCount,
        genres = genres.map { genre ->
            Genre(
                id = genre.id,
                name = genre.name,
            )
        },
    )
}

fun TmdbMovieCreditsDto.toMovieCredits(): MovieCredits {
    return MovieCredits(
        movieId = id,
        cast = cast
            .map { it.toCastMember() }
            .sortedBy { it.order ?: Int.MAX_VALUE },
        crew = crew.map { it.toCrewMember() },
    )
}

private fun TmdbCastMemberDto.toCastMember(): CastMember {
    return CastMember(
        id = id,
        name = name ?: originalName.orEmpty(),
        originalName = originalName,
        profilePath = profilePath?.toTmdbProfileUrl(),
        character = character,
        order = order,
    )
}

private fun TmdbCrewMemberDto.toCrewMember(): CrewMember {
    return CrewMember(
        id = id,
        name = name ?: originalName.orEmpty(),
        originalName = originalName,
        profilePath = profilePath?.toTmdbProfileUrl(),
        department = department,
        job = job,
        creditId = creditId,
    )
}

fun TmdbMovieWatchProvidersDto.toMovieWatchProviders(
    countryCode: String = "PL",
): MovieWatchProviders {
    val region = results[countryCode]

    return MovieWatchProviders(
        movieId = id,
        countryCode = countryCode,
        justWatchLink = region?.link,
        flatrate = region
            ?.flatrate
            .orEmpty()
            .map { it.toWatchProvider() }
            .sortedBy { it.displayPriority ?: Int.MAX_VALUE },
        rent = region
            ?.rent
            .orEmpty()
            .map { it.toWatchProvider() }
            .sortedBy { it.displayPriority ?: Int.MAX_VALUE },
        buy = region
            ?.buy
            .orEmpty()
            .map { it.toWatchProvider() }
            .sortedBy { it.displayPriority ?: Int.MAX_VALUE },
        ads = region
            ?.ads
            .orEmpty()
            .map { it.toWatchProvider() }
            .sortedBy { it.displayPriority ?: Int.MAX_VALUE },
        free = region
            ?.free
            .orEmpty()
            .map { it.toWatchProvider() }
            .sortedBy { it.displayPriority ?: Int.MAX_VALUE },
    )
}

private fun TmdbWatchProviderDto.toWatchProvider(): WatchProvider {
    return WatchProvider(
        providerId = providerId,
        providerName = providerName.orEmpty(),
        logoPath = logoPath?.toTmdbLogoUrl(),
        displayPriority = displayPriority,
    )
}

private fun String?.toLocalDateOrNull(): LocalDate? {
    if (this.isNullOrBlank()) {
        return null
    }

    return runCatching {
        LocalDate.parse(this)
    }.getOrNull()
}