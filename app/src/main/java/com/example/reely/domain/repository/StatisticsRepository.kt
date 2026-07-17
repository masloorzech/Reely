package com.example.reely.domain.repository

import com.example.reely.domain.model.StatisticsSourceData
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {

    fun observeSourceData(): Flow<StatisticsSourceData>
}