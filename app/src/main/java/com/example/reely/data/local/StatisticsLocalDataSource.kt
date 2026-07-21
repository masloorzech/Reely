package com.example.reely.data.local

import com.example.reely.data.local.model.StatisticsLocalData
import kotlinx.coroutines.flow.Flow

interface StatisticsLocalDataSource {

    fun observeData(): Flow<StatisticsLocalData>

}