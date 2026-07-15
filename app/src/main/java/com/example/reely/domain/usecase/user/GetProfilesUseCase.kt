package com.example.reely.domain.usecase.profile

import com.example.reely.domain.model.Profile
import com.example.reely.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfilesUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {

    suspend operator fun invoke(): List<Profile> {
        return repository.getProfiles()
    }
}