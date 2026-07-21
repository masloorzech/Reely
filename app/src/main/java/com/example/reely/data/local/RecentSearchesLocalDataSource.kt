package com.example.reely.data.local

import kotlinx.coroutines.flow.Flow

interface RecentSearchesLocalDataSource {

    suspend fun add(query: String)

    fun observeRecentSearches(): Flow<List<String>>
    suspend fun clear()
}