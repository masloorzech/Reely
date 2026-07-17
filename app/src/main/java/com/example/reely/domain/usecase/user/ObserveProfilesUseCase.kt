package com.example.reely.domain.usecase.user

import com.example.reely.domain.model.Profile
import com.example.reely.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProfilesUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {

    operator fun invoke(): Flow<List<Profile>> {
        return repository.observeProfiles()
    }
}