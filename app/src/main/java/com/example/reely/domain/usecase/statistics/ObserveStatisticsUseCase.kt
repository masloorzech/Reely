package com.example.reely.domain.usecase.statistics


import com.example.reely.domain.model.ReelyStatistics
import com.example.reely.domain.repository.ProfileRepository
import com.example.reely.domain.repository.StatisticsRepository
import java.time.Clock
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveStatisticsUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val profileRepository: ProfileRepository,
    private val statisticsCalculator: StatisticsCalculator,
    private val clock: Clock,
) {

    operator fun invoke(): Flow<ReelyStatistics> {
        return combine(
            statisticsRepository.observeSourceData(),
            profileRepository.observeProfiles(),
        ) { sourceData, profiles ->
            statisticsCalculator.calculate(
                sourceData = sourceData,
                profiles = profiles,
                currentMonth = YearMonth.now(clock),
            )
        }
    }
}