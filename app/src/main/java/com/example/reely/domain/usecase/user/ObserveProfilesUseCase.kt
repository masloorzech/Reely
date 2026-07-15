package com.example.reely.domain.usecase.user

import com.example.reely.domain.model.Profile
import com.example.reely.domain.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveProfilesUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {

    operator fun invoke(): Flow<List<Profile>> {
        return repository.observeProfiles()
    }
}