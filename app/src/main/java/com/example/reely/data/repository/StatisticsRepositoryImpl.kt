package com.example.reely.data.repository

import com.example.reely.data.local.StatisticsLocalDataSource
import com.example.reely.data.local.room.mapper.toDomain
import com.example.reely.domain.model.Genre
import com.example.reely.domain.model.StatisticsSourceData
import com.example.reely.domain.model.StatisticsSourceMovie
import com.example.reely.domain.repository.StatisticsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class StatisticsRepositoryImpl @Inject constructor(
    private val localDataSource: StatisticsLocalDataSource,
) : StatisticsRepository {

    override fun observeSourceData(): Flow<StatisticsSourceData> {
        return localDataSource.observeData().map { data ->
            val genresById = data.genres.associateBy { genre ->
                genre.id
            }

            val genresByMovieId = data.movieGenres
                .groupBy { reference ->
                    reference.movieId
                }
                .mapValues { (_, references) ->
                    references.mapNotNull { reference ->
                        genresById[reference.genreId]
                    }
                }

            StatisticsSourceData(
                movies = data.movies.map { movie ->
                    StatisticsSourceMovie(
                        id = movie.id,
                        title = movie.title,
                        posterPath = movie.posterPath,
                        runtimeMinutes = movie.runtimeMinutes,
                        genres = genresByMovieId[movie.id]
                            .orEmpty()
                            .map { genre ->
                                Genre(
                                    id = genre.id,
                                    name = genre.name,
                                )
                            },
                    )
                },
                viewings = data.viewings.map { viewing ->
                    viewing.toDomain()
                },
                ratings = data.ratings.map { rating ->
                    rating.toDomain()
                },
                watchlistMovieIds = data.watchlistEntries
                    .map { entry ->
                        entry.movieId
                    }
                    .toSet(),
            )
        }
    }
}