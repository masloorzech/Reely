package com.example.reely.domain.usecase.user

import com.example.reely.domain.repository.ProfileRepository
import javax.inject.Inject

class CheckProfilesConfiguredUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {

    suspend operator fun invoke(): Boolean {
        return repository.areProfilesConfigured()
    }
}