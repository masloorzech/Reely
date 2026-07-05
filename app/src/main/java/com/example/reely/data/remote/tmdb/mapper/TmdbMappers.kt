package com.example.reely.data.remote.tmdb.mapper

import com.example.reely.data.remote.model.RemoteMovie
import com.example.reely.data.remote.tmdb.dto.TmdbMovieDetailsDto
import com.example.reely.data.remote.tmdb.dto.TmdbMovieDto

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