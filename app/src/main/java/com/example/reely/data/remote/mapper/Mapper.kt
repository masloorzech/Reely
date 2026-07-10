package com.example.reely.data.remote.mapper

import com.example.reely.data.remote.model.RemoteCastMember
import com.example.reely.data.remote.model.RemoteCrewMember
import com.example.reely.data.remote.model.RemoteMovieCredits
import com.example.reely.data.remote.model.RemoteMovieWatchProviders
import com.example.reely.data.remote.model.RemoteWatchProvider
import com.example.reely.domain.model.CastMember
import com.example.reely.domain.model.CrewMember
import com.example.reely.domain.model.LogoPaths
import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.model.ProfilePaths
import com.example.reely.domain.model.WatchProvider

fun RemoteMovieCredits.toDomain(): MovieCredits {
    return MovieCredits(
        movieId = movieId,
        cast = cast.map { it.toDomain() },
        crew = crew.map { it.toDomain() }
    )
}

fun RemoteCastMember.toDomain(): CastMember {
    return CastMember(
        id = id,
        name = name,
        originalName = originalName,
        profilePaths = ProfilePaths(
            localProfilePath = null,
            remoteProfilePath = profilePath
        ),
        character = character,
        order = order
    )
}

fun RemoteCrewMember.toDomain(): CrewMember {
    return CrewMember(
        id = id,
        name = name,
        originalName = originalName,
        profilePath = ProfilePaths(
            localProfilePath = null,
            remoteProfilePath = profilePath
        ),
        department = department,
        job = job,
        creditId = creditId
    )
}

fun RemoteMovieWatchProviders.toDomain(): MovieWatchProviders {
    return MovieWatchProviders(
        movieId = movieId,
        countryCode = countryCode,
        justWatchLink = link,
        flatrate = flatrate.map { it.toDomain() },
        rent = rent.map { it.toDomain() },
        buy = buy.map { it.toDomain() },
        ads = ads.map { it.toDomain() },
        free = free.map { it.toDomain() }
    )
}

fun RemoteWatchProvider.toDomain(): WatchProvider {
    return WatchProvider(
        providerId = providerId,
        providerName = providerName,
        logoPaths = LogoPaths(
            localLogoPath = null,
            remoteLogoPath = logoPath
        ),
        displayPriority = displayPriority
    )
}