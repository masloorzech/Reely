package com.example.reely.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieUserStateEntity

@Dao
interface MovieDao {

    @Upsert
    suspend fun upsertMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovie(movieId: Int): MovieEntity?

    @Query("SELECT * FROM movie_user_states WHERE movieId = :movieId")
    suspend fun getUserStatesForMovie(movieId: Int): List<MovieUserStateEntity>

    @Upsert
    suspend fun upsertUserState(state: MovieUserStateEntity)

    @Query("DELETE FROM movie_user_states WHERE movieId = :movieId AND userId = :userId")
    suspend fun deleteUserState(movieId: Int, userId: Int)
}