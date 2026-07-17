package com.example.reely.data.local.model

import com.example.reely.data.local.room.entity.GenreEntity
import com.example.reely.data.local.room.entity.MovieEntity
import com.example.reely.data.local.room.entity.MovieGenreCrossRef
import com.example.reely.data.local.room.entity.MovieRatingEntity
import com.example.reely.data.local.room.entity.MovieViewingEntity
import com.example.reely.data.local.room.entity.WatchlistEntryEntity

data class StatisticsLocalData(
    val movies: List<MovieEntity>,
    val viewings: List<MovieViewingEntity>,
    val ratings: List<MovieRatingEntity>,
    val genres: List<GenreEntity>,
    val movieGenres: List<MovieGenreCrossRef>,
    val watchlistEntries: List<WatchlistEntryEntity>,
)