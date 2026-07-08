package com.example.reely.data.remote.tmdb.mapper

import com.example.reely.data.remote.model.RemoteCastMember
import com.example.reely.data.remote.model.RemoteMovie
import com.example.reely.data.remote.model.RemoteMovieCredits
import com.example.reely.data.remote.model.RemoteMovieWatchProviders
import com.example.reely.data.remote.model.RemoteWatchProvider
import com.example.reely.data.remote.tmdb.dto.TmdbCastMemberDto
import com.example.reely.data.remote.tmdb.dto.TmdbMovieCreditsDto
import com.example.reely.data.remote.tmdb.dto.TmdbMovieDetailsDto
import com.example.reely.data.remote.tmdb.dto.TmdbMovieDto
import com.example.reely.data.remote.tmdb.dto.TmdbMovieWatchProvidersDto
import com.example.reely.data.remote.tmdb.dto.TmdbWatchProviderDto
import com.example.reely.data.remote.tmdb.toTmdbLogoUrl
import com.example.reely.data.remote.tmdb.toTmdbPosterUrl
import com.example.reely.data.remote.tmdb.toTmdbProfileUrl

fun TmdbMovieDto.toRemoteMovie(): RemoteMovie {
    return RemoteMovie(
        id = id,
        title = title ?: originalTitle.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        rating = voteAverage,
        voteCount = voteCount,
        popularity = popularity
    )
}

fun TmdbMovieDetailsDto.toRemoteMovie(): RemoteMovie {
    return RemoteMovie(
        id = id,
        title = title ?: originalTitle.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        rating = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        runtime = runtime,
        genres = genres.map { it.name }
    )
}

fun TmdbMovieWatchProvidersDto.toRemote(
    countryCode: String = "PL"
): RemoteMovieWatchProviders {

    val region = results[countryCode]

    return RemoteMovieWatchProviders(
        movieId = id,
        countryCode = countryCode,
        link = region?.link,
        flatrate = region?.flatrate.orEmpty().map { it.toRemote() },
        rent = region?.rent.orEmpty().map { it.toRemote() },
        buy = region?.buy.orEmpty().map { it.toRemote() },
        ads = region?.ads.orEmpty().map { it.toRemote() },
        free = region?.free.orEmpty().map { it.toRemote() }
    )
}

fun TmdbWatchProviderDto.toRemote(): RemoteWatchProvider {
    return RemoteWatchProvider(
        providerId = providerId,
        providerName = providerName.orEmpty(),
        logoPath = logoPath?.toTmdbLogoUrl(),
        displayPriority = displayPriority
    )
}

fun TmdbMovieCreditsDto.toRemote(): RemoteMovieCredits {
    return RemoteMovieCredits(
        movieId = id,
        cast = cast.map { it.toRemote() }
    )
}

fun TmdbCastMemberDto.toRemote(): RemoteCastMember {
    return RemoteCastMember(
        id = id,
        name = name.orEmpty(),
        originalName = originalName,
        profilePath = profilePath?.toTmdbProfileUrl(),
        character = character,
        order = order
    )
}