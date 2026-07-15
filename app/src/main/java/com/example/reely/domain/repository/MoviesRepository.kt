package com.example.reely.domain.repository
import com.example.reely.domain.model.MovieCredits
import com.example.reely.domain.model.MovieDetails
import com.example.reely.domain.model.MoviePreview
import com.example.reely.domain.model.MovieRating
import com.example.reely.domain.model.MovieViewing
import com.example.reely.domain.model.MovieWatchProviders
import com.example.reely.domain.model.Rating
import com.example.reely.domain.model.RecentWatchedMovie
import com.example.reely.domain.model.WatchlistEntry
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun observePopularMovies(): Flow<List<MoviePreview>>

    fun observeRecentSearches() : Flow<List<String>>

    suspend fun refreshPopularMovies(
        forceRefresh: Boolean = false,
    ): Result<Unit>

    suspend fun searchMovies(
        query: String,
    ): Result<List<MoviePreview>>

    suspend fun getMovieDetails(
        movieId: Int,
    ): Result<MovieDetails>

    fun observeWatchlist(): Flow<List<WatchlistEntry>>

    fun observeViewings():
            Flow<List<MovieViewing>>

    fun observeViewingsForMovie(
        movieId: Int,
    ): Flow<List<MovieViewing>>

    fun observeRatingsForMovie(
        movieId: Int,
    ): Flow<List<MovieRating>>

    suspend fun isOnWatchlist(
        movieId: Int,
    ): Boolean

    suspend fun addToWatchlist(
        movie: MovieDetails,
    ): Result<Boolean>

    suspend fun markAsWatched(
        movie: MovieDetails,
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Result<Long>

    suspend fun rewatchMovie(
        movie: MovieDetails,
        watchedAt: LocalDate,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Result<Long>

    suspend fun updateRatings(
        movieId: Int,
        firstProfileRating: Rating,
        secondProfileRating: Rating,
    ): Result<Unit>

    suspend fun removeFromWatchlist(
        movieId: Int,
    ): Result<Boolean>

    suspend fun deleteViewing(
        viewingId: Long,
    ): Result<Boolean>

    suspend fun getMovieCredits(
        movieId: Int,
    ): Result<MovieCredits>

    suspend fun getMovieProviders(movieId: Int, ): Result<MovieWatchProviders>

    fun observeRecentWatchedMovies(
        limit: Int,
    ): Flow<List<RecentWatchedMovie>>

    suspend fun removeFromWatched(
        movieId: Int,
    ): Result<Boolean>

    fun observeWatchedMovies():
            Flow<List<RecentWatchedMovie>>


}
